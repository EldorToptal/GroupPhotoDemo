package com.groupphoto.app.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.paging.PagedList
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.BackupOptions
import com.groupphoto.app.data.repository.local.pref.SharedPref
import com.groupphoto.app.data.repository.local.pref.SharedPref.backupFromHere
import com.groupphoto.app.data.repository.local.pref.SharedPref.backupOption
import com.groupphoto.app.data.repository.local.pref.SharedPref.backupPaused
import com.groupphoto.app.data.repository.local.dao.BackupRoomDatabase
import com.groupphoto.app.data.repository.local.dao.BackupRoomRepository
import com.groupphoto.app.data.repository.local.entity.BackupEntity
import com.groupphoto.app.data.repository.local.enums.BackUpFilesType
import com.groupphoto.app.data.repository.local.pref.UserPref
import com.groupphoto.app.presentation.MainActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.roundToInt
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.groupphoto.app.GroupPhoto
import com.groupphoto.app.util.Constants
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class UploadWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params), KoinComponent {

    var backupDatabase = BackupRoomDatabase.Companion.getDatabase(
        applicationContext
    ).backupDao()
    val repository: BackupRoomRepository by inject()
    val uploadingImageFiles = GroupPhoto.uploadingImageFiles.value ?: ArrayList()
    var isUploading = false

    companion object {
        fun triggerImageUploadingWorker(
            applicationContext: Context,
            restartUploading: Boolean = false
        ) {
            if (getStateOfWork(applicationContext) != WorkInfo.State.ENQUEUED && getStateOfWork(
                    applicationContext
                ) != WorkInfo.State.RUNNING
            ) {
                Timber.d("startLocationUpdates : Upload worker started")
                startUploadImageWorker(applicationContext, restartUploading)
            } else {
                if(restartUploading){
                    startUploadImageWorker(applicationContext, restartUploading)
                    Timber.d("startLocationUpdates: Restarting")
                }
                Timber.d("startLocationUpdates: Upload worker is already working")
            }
        }

        private fun getStateOfWork(applicationContext: Context): WorkInfo.State? {
            return try {
                if (WorkManager.getInstance(applicationContext)
                        .getWorkInfosForUniqueWork(Constants.UPLOAD_IMAGE_WORKER)
                        .get().size > 0
                ) {
                    WorkManager.getInstance(applicationContext)
                        .getWorkInfosForUniqueWork(Constants.UPLOAD_IMAGE_WORKER)
                        .get()[0].state
                    // this can return WorkInfo.State.ENQUEUED or WorkInfo.State.RUNNING
                    // you can check all of them in WorkInfo class.
                } else {
                    WorkInfo.State.CANCELLED
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
                WorkInfo.State.CANCELLED
            } catch (e: InterruptedException) {
                e.printStackTrace()
                WorkInfo.State.CANCELLED
            }
        }


        private fun startUploadImageWorker(applicationContext: Context, restartUploading: Boolean) {
            val periodicWorkRequest = PeriodicWorkRequest
                .Builder(UploadWorker::class.java, 16, TimeUnit.MINUTES)
                .build()
            Timber.d("Enqueue worker")
            val policy = if (restartUploading)
                ExistingPeriodicWorkPolicy.REPLACE
            else
                ExistingPeriodicWorkPolicy.KEEP
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                Constants.UPLOAD_IMAGE_WORKER,
                policy,
                periodicWorkRequest
            )
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun showNotification(totalItemCount: String, fileName: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = context.getString(R.string.default_notification_channel_id)
        val channelName = context.getString(R.string.default_notification_channel_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Uploading")
            .setContentText(fileName)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(987, notificationBuilder.build())
    }

    override fun doWork(): Result {
        Timber.d("Do work ${SharedPref.backUpOptions.name}")
        getAllImages()
        getAllVideos()
        val backupDb = backupDatabase.getAllSavedData()
        val backupOption = SharedPref.backUpOptions
        if (!isUploading) {
            isUploading = true
            uploadingImageFiles.clear()
            when (backupOption) {
                BackupOptions.ENTIRE_LIBRARY -> {
                    for (i in backupDb.indices) {
                        if (!backupDb[i].isUploaded) {
                            uploadingImageFiles.add(backupDb[i])
                        }
                    }
                }
                BackupOptions.HUNDRED_PHOTOS -> {
                    var photoCounter = 0
                    for (i in backupDb.indices) {
                        if (!backupDb[i].isUploaded) {
                            photoCounter++
                            if (photoCounter > 100) break
                            uploadingImageFiles.add(backupDb[i])
                        }
                    }

                }
                BackupOptions.FROM_NOW_ON -> {
                    if (!backupFromHere) {
                        for (i in backupDb) {
                            backupDatabase.setUploaded(i.id)
                        }
                        backupFromHere = true
                    } else {
                        for (i in backupDb.indices) {
                            if (!backupDb[i].isUploaded) {
                                uploadingImageFiles.add(backupDb[i])
                            }
                        }
                    }

                }
                BackupOptions.DO_NOT_BACK_UP -> {
                    for (i in backupDb) {
                        backupDatabase.setUploaded(i.id)
                    }
                }
            }
            if (uploadingImageFiles.isNotEmpty())
                uploadImage(uploadingImageFiles[0])
        }
        return Result.success()
    }

    private fun fileSHA512(input: ByteArray): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(input)

        // Convert byte array into signum representation
        val no = BigInteger(1, messageDigest)

        // Convert message digest into hex value
        var hashtext: String = no.toString(16)

        // Add preceding 0s to make it 32 bit
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }

        // return the HashText
        return hashtext
    }

    fun uploadImage(backupEntity: BackupEntity, counter: Int = 0) {
        Timber.d("upload Size : ${uploadingImageFiles.size}")
        Timber.d("upload Files : ${uploadingImageFiles.toString()}")
        Timber.d("upload Counter : $counter")
        if (uploadingImageFiles.size - 1 == counter) {
            isUploading = false
            GroupPhoto.uploadingImageFiles.postValue(ArrayList())
            GroupPhoto.uploadingImageFileCounter.postValue(0)
            return
        }
        if (!backupPaused) {
            val storage = Firebase.storage/*("gs://group-photo-prod.appspot.com")*/
            var storageRef = storage.reference
            var extension = backupEntity.fileName.split(".").last() ?: ".321231"
            var backupRef =
                //storageRef.child("assets/${fileSHA512(File(backupEntity.path).readBytes())}.$extension")
                storageRef.child("uploads/${fileSHA512(File(backupEntity.path).readBytes())}.$extension")
            val file =
                Uri.fromFile(File(backupEntity.path))

            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(File(file.path).absolutePath, options)

            var type = if (backupEntity.fileType == "video") {
                "video/mp4"
            } else {
                "image/jpg"
            }
//            Files.hash(new File(fileName), Hashing.sha512()).toString();
            var hash = fileSHA512(File(backupEntity.path).readBytes())
            val metadata = storageMetadata {
                contentType = type
                setCustomMetadata("client-local-identifier", "")
                setCustomMetadata("gp-backup-type", type)
                setCustomMetadata("media-type", type)
                setCustomMetadata("uti", "")
                setCustomMetadata("original-file-name", backupEntity.fileName)
                setCustomMetadata("mime-type", options.outMimeType ?: "")
                setCustomMetadata("media-subtypes", "")
                setCustomMetadata("source-type", "user-library")
                setCustomMetadata("pixel-width", options.outWidth.toString())
                setCustomMetadata("pixel-height", options.outHeight.toString())
                setCustomMetadata("creation-date", backupEntity.creationDate)
                setCustomMetadata("modification-date", "")
                setCustomMetadata("duration", "")
                setCustomMetadata("favorite", "")
                setCustomMetadata("hidden", "false")
                setCustomMetadata("user-id", UserPref.uid)
                setCustomMetadata("sha-512-hash", hash)
                setCustomMetadata("gp-storage-event-type", "upload")

            }
            backupRef.putFile(file, metadata)
                .addOnSuccessListener { taskSnapshot ->
                    backupDatabase.setUploaded(backupEntity.id)
                    backupDatabase.setUploadDone(backupEntity.id)
                    Timber.d("success upload")
                    showNotification("Uploaded", backupEntity.fileName)
                    val nextCount = if (counter + 1 != uploadingImageFiles.size)
                        counter + 1
                    else
                        counter
                    if (nextCount < uploadingImageFiles.size)
                        uploadImage(uploadingImageFiles[nextCount], nextCount)
                    GroupPhoto.uploadingImageFileCounter.value = counter + 1
                }
                .addOnFailureListener {
                    //on fail
                    backupDatabase.setUploadFailed(backupEntity.id)
                    showNotification("Failed", backupEntity.fileName)
                    val nextCount = if (counter + 1 != uploadingImageFiles.size)
                        counter + 1
                    else
                        counter
                    if (nextCount < uploadingImageFiles.size)
                        uploadImage(uploadingImageFiles[nextCount], nextCount)
                    Timber.d("fail upload")
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    backupDatabase.setUploadPercentage(
                        backupEntity.id,
                        (progress * 100).roundToInt() / 100
                    )
                    backupDatabase.setUploading(backupEntity.id)
                    Timber.d("Upload is $progress% done")
                    showNotification("Uploading", backupEntity.fileName)
                }
                .addOnPausedListener {
                    Timber.d("Upload is paused")
                }


            /*backupRef.downloadUrl.addOnCompleteListener{taskSnapshot ->

                val url = taskSnapshot.result
                e("$url")
            }*/

        }

    }

    fun getSHA512(input: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(input.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashtext: String = no.toString(16)
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }
        return hashtext
    }

    fun getAllImages() {
        val config = PagedList.Config.Builder()
            .setPageSize(40)
            .setInitialLoadSizeHint(40)
            .setPrefetchDistance(40)
            .setEnablePlaceholders(false)
            .build()
        var imageList: MutableList<BackupEntity> = mutableListOf()
        var absolutePathOfImage: String? = null
        var imageName: String = ""
        var imageSize: Double = 0.0
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )

        val cursor: Cursor? = applicationContext.contentResolver.query(
            uri, projection, null,
            null, null
        )

        val columnIndexData: Int = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val sizeIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
        val columnIndexFolderName: Int =
            cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        var ctr = 0
        val backupDatabase = BackupRoomDatabase.getDatabase(applicationContext).backupDao()
        var backupDb = backupDatabase.getAllSavedData()

        imageList.clear()
        var imageCounter = 0

        while (cursor!!.moveToNext()) {
            absolutePathOfImage = cursor!!.getString(columnIndexData)
            imageName = cursor!!.getString(columnIndexFolderName)
            //e("FileSize==> ${cursor.getFloat(sizeIndex)} ")
            imageSize = ((cursor.getDouble(sizeIndex) / (1024f * 1024f)))
            var shaName = getSHA512(imageName)
            ctr++
            Timber.d("ImageName: $imageName")
            try {
                backupDb = backupDatabase.getAllSavedData()
                var doesExist = false
                for (i in backupDb) {
                    if (i.path == absolutePathOfImage) {
                        doesExist = true
                    }
                }
                if (!doesExist) {
                    imageCounter++
                    imageList.add(
                        BackupEntity(
                            0,
                            imageName,
                            absolutePathOfImage!!,
                            imageSize,
                            shaName + ctr,
                            false,
                            "image",
                            -1

                        )
                    )

                }
                if (imageCounter == 100) {
                    imageCounter = 0
                    backupDatabase.insertAll(imageList)
                    imageList = mutableListOf()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        Timber.d("Inserting All the images ${imageList.toString()}")
        cursor.close()
        backupDatabase.insertAll(imageList)

    }

    fun getAllVideos() {
        var videoList: MutableList<BackupEntity> = mutableListOf()
        var cursor: Cursor?
        var column_index_data: Int
        var column_index_folder_name: Int

        var absolutePathOfVideo: String? = null
        var videoName: String = ""
        var videouri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        cursor = applicationContext.contentResolver.query(
            videouri, projection, null,
            null, null
        )

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        column_index_folder_name =
            cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        var ctr = 0
        var backupDatabase = BackupRoomDatabase.Companion.getDatabase(
            applicationContext
        ).backupDao()
        var backupDb = backupDatabase.getAllSavedData()

        videoList.clear()
        var imageCounter = 0
        while (cursor!!.moveToNext()) {
            absolutePathOfVideo = cursor!!.getString(column_index_data)
            videoName = cursor!!.getString(column_index_folder_name)
            var shaName = getSHA512(videoName)
//            var myFile = File(absolutePathOfVideo).readBytes().toString().sha512()
            ctr++
            try {
                backupDb = backupDatabase.getAllSavedData()
                var doesExist = false
                for (i in backupDb) {
                    if (i.path == absolutePathOfVideo) {
                        doesExist = true
                    }
                }
                if (!doesExist) {
                    imageCounter++
                    videoList.add(
                        BackupEntity(
                            0,
                            videoName,
                            absolutePathOfVideo!!,
                            0.1,
                            shaName + ctr,
                            false,
                            "video"
                        )
                    )
                }
                if (imageCounter == 100) {
                    imageCounter = 0
                    backupDatabase.insertAll(videoList)
                    videoList = mutableListOf()
                }
//                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        Timber.d("Inserting All the Videos ${videoList.toString()}")

        backupDatabase.insertAll(videoList)
    }

}