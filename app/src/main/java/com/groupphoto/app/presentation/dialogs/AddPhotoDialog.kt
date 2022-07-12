package com.groupphoto.app.presentation.dialogs

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.groupphoto.app.BuildConfig
import com.groupphoto.app.R
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.dialog_add_photo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoDialog() :
    BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
//    lateinit var imageView: ImageView
//    lateinit var captureButton: Button

    val REQUEST_IMAGE_CAPTURE = 1


    val PERMISSION_REQUEST_CODE: Int = 101

    private var mCurrentPhotoPath: String? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_add_photo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        access_gallery.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission

                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery();
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        access_camera.setOnClickListener(View.OnClickListener {
            if (checkPersmission()) takePicture() else requestPermission()
        })
        edit_profile_cancel.setOnClickListener {
            dismiss()
        }


    }


    //Camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    takePicture()

                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
                when (requestCode) {
                    PERMISSION_CODE -> {
                        if (grantResults.isNotEmpty() && grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            //permission from popup granted
                            pickImageFromGallery()
                        } else {
                            //permission from popup denied
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


    }

    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            Objects.requireNonNull(
                requireContext().applicationContext
            ),
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Activity.RESULT_OK ->
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                    //To get the File for further usage
                    val auxFile = File(mCurrentPhotoPath)


                    var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
//                    avatar.setImageBitmap(bitmap)

                } else toast(data?.data.toString())
            //            image_view.setImageURI(data?.data)
        }

    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }


    //Gallery
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        const val IMAGE_PICK_CODE = 1000
        //Permission code
        const val PERMISSION_CODE = 1001
    }

    //handle requested permission result
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode){
//            PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] ==
//                    PackageManager.PERMISSION_GRANTED){
//                    //permission from popup granted
//                    pickImageFromGallery()
//                }
//                else{
//                    //permission from popup denied
//                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }

    //handle result of picked image
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
//            toast(data?.data.toString())
////            image_view.setImageURI(data?.data)
//        }
//    }


}