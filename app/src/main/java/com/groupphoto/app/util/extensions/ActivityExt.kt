package com.groupphoto.app.util.extensions

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.Runnables.doNothing
import com.groupphoto.app.R
import com.pawegio.kandroid.e
import com.pawegio.kandroid.fromApi
import com.pawegio.kandroid.hide
import kotlinx.android.synthetic.main.dialog_alert_dark.*
import org.jetbrains.anko.newTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Time
import java.util.*

/**
 * Set Activity to fullscreen and status bar icons to black
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Activity.setToFullScreenAndLightStatusBar() {
    window.apply {
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        fromApi(23, true) { decorView.systemUiVisibility += View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR }
        statusBarColor = Color.TRANSPARENT
    }
}

/**
 * Open link in browser
 * @param url link address to be opened
 */
fun Activity.openLinkInBrowser(url: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW, Uri.parse(
                try {
                    url.precedeLinkWithHttps()
                } catch (e: Exception) {
                    url.precedeLinkWithHttp()
                }
            )
        )
    )
}

/**
 * Open soft keyboard
 * @param view focus this view
 */
fun Activity.showSoftKeyboard(view: View? = null) {
    view?.requestFocus()
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

/**
 * Hide soft keyboard
 */
fun Activity.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    try {
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    } catch (e: Exception) {
//        doNothing()
    }
}

/**
 * Hide keyboard when another uneditable view is touched
 */
fun Activity.hidesSoftKeyboardOnTouch(view: View?) {
    (view ?: (window.decorView.rootView as ViewGroup)).apply {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.rootView.windowToken, 0)
            }
        }
    }
}

/**
 * Listen for keyboard state changes
 */
fun Activity.softKeyboardStateChangeListener(shown: () -> Unit, hidden: () -> Unit) {
    with(window.decorView.rootView as ViewGroup) {
        viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            getWindowVisibleDisplayFrame(r)

            val heightDiff = rootView.height - (r.bottom - r.top)
            if (heightDiff > 100.pxToDp(context)) {
                shown()
            } else {
                hidden()
            }

        }
    }
}

/**
 * Open app in play store
 */
fun Activity.openAppInPlayStore() {
    val uri = Uri.parse("market://details?id=$packageName")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    // To count with Play market backstack, After pressing back button,
    // to taken back to our application, we need to add following flags to intent.
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

/**
 * Open Gallery and select media
 * @param requestCode request code for picking an image from gallery
 */
fun Activity.choosePhotoFromGallery(requestCode: Int? = null) {
    val galleryIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    startActivityForResult(galleryIntent, requestCode ?: RequestCode.GALLERY_PICK)
}

/**
 * Open camera and take photo
 * @param requestCode request code for taking photo using camera
 */
fun Activity.takePhotoFromCamera(requestCode: Int?) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    startActivityForResult(cameraIntent, requestCode ?: RequestCode.TAKE_PHOTO)
}

/**
 * Save image file
 * @param bitmap image to be saved
 * @param directory folder path where the image will be saved
 * @param onSaveFinished callback when file saving is finished
 */
fun Activity.saveImage(
    bitmap: Bitmap,
    directory: String,
    onSaveFinished: (success: Boolean, path: String?) -> Unit
) {

    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
    val wallpaperDirectory = File("${Environment.getExternalStorageDirectory()}$directory")
    // have the object build the directory structure, if needed.
    if (!wallpaperDirectory.exists()) {
        wallpaperDirectory.mkdirs()
    }
    try {
        val f = File(
            wallpaperDirectory, Calendar.getInstance()
                .timeInMillis.toString() + ".jpg"
        )
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        MediaScannerConnection.scanFile(
            this,
            arrayOf(f.path),
            arrayOf("image/jpeg"), null
        )
        fo.close()
        e("File Saved::--->" + f.absolutePath)
        onSaveFinished(true, f.absolutePath)
    } catch (e: IOException) {
        e.printStackTrace()
        onSaveFinished(false, null)
    }
}


/**
 * Alert Dialog button specs
 * @param text button text
 * @param handler click handler
 */
data class ButtonSpecs(
    val text: String? = null,
    val handler: (() -> Unit)? = null
)

/**
 * Alert Dialog Type enum
 * @param symbol character symbol displayed before the tile
 * @param color [symbol] and button colors
 */
enum class AlertDialogType(val symbol: Char, val color: Int) {
    INFO('i', R.color.status_info),
    CONFIRMATION('?', R.color.status_confirmation),
    WARNING('!', R.color.status_warning),
    ERROR('!', R.color.status_error),
    SUCCESS('✓', R.color.status_success),
    DEFAULT(Char.MIN_VALUE, R.color.status_default)
}

fun Activity.createLoaderDialog(rootView: ViewGroup) = AlertDialog.Builder(this).create().apply {
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    setCancelable(false)
    setView(
        layoutInflater.inflate(
            R.layout.dialog_loader,
            rootView
        )
    )
}

/**
 * Base function for alert dialogs
 * @param alertType type of alert dialog. see [AlertDialogType]
 * @param title title text
 * @param message message text
 * @param positiveButton positive button specifications. see [ButtonSpecs]
 * @param negativeButton negative button specifications. see [ButtonSpecs]
 * @param isCancelable set if dialog can be canceled
 * @param dark use dark layout design
 * @param titleFont override title font
 * @param messageFont override message font
 */
fun Activity.showAlertDialog(
    alertType: AlertDialogType,
    title: String,
    message: String,
    positiveButton: ButtonSpecs?,
    negativeButton: ButtonSpecs?,
    isCancelable: Boolean? = false,
    dark: Boolean? = false,
    @FontRes titleFont: Int? = null,
    @FontRes messageFont: Int? = null
) {

    val rootView = window.decorView.rootView as ViewGroup
    // init dialog and view
    val dialog = AlertDialog.Builder(this).create()
    val dialogView =
        rootView.inflate(if (dark == true) R.layout.dialog_alert_dark else R.layout.dialog_alert)
    // setup view
    dialogView.apply {
        // texts
        setFontToViews(
            ResourcesCompat.getFont(context, titleFont ?: R.font.bold),
            dialert_text_symbol,
            dialert_text_title,
            dialert_button_positive,
            dialert_button_negative
        )
        setFontToViews(
            ResourcesCompat.getFont(context, messageFont ?: R.font.medium),
            dialert_text_message
        )
        dialert_text_symbol.apply {
            if (alertType == AlertDialogType.DEFAULT) text = ""
            else {
                typeface = ResourcesCompat.getFont(context, titleFont ?: R.font.bold)
                text = alertType.symbol.toString()
                setTextColor(getCompatColor(alertType.color))
            }
        }
        dialert_text_title.text = title
        dialert_text_message.text = message
        // positive button
        dialert_button_positive.apply {
            text = positiveButton?.text ?: "OK"
            setTextColor(getCompatColor(alertType.color))
            setOnClickListener {
                positiveButton?.handler?.invoke()
                dialog.dismiss()
            }
        }
        // negative button
        negativeButton?.let {
            dialert_button_negative.apply {
                text = negativeButton.text ?: "CANCEL"
                setOnClickListener {
                    negativeButton.handler?.invoke()
                    dialog.dismiss()
                }
            }
        } ?: dialert_button_negative.hide()
    }
    // setup AlertDialog
    dialog.apply {
        setCancelable(isCancelable ?: true)
        setView(dialogView)
        show()
    }
}

/**
 * Set [font] to views
 * @param font [Typeface] to set
 * @param views views to set [font] to
 */
fun setFontToViews(font: Typeface?, vararg views: TextView) {
    views.forEach { view -> view.typeface = font }
}

/**
 * SnackbarType
 */
enum class SnackbarType {
    DEFAULT,
    SUCCESS,
    ERROR
}

/**
 * Base function for snackBars
 * @param length display duration [Snackbar.LENGTH_INDEFINITE], [Snackbar.LENGTH_LONG] or [Snackbar.LENGTH_SHORT]
 * @param type predefined background colors. see [SnackbarType]
 * @param message message text
 * @param actionText action text
 * @param actionHandler action handler function
 * @param textColor text colors
 * @param backgroundColor custom background color
 * @param messageFont override message font
 * @param actionFont override action font
 */
fun Activity.showSnackbar(
    root: ViewGroup? = null,
    length: Int,
    type: SnackbarType? = null,
    message: String,
    actionText: String? = null,
    actionHandler: (() -> Unit)? = null,
    textColor: Int? = null,
    backgroundColor: Int? = null,
    @FontRes messageFont: Int? = null,
    @FontRes actionFont: Int? = null
) {

    val rootView = root
        ?: window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ConstraintLayout
    val snackbarParent = CoordinatorLayout(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    rootView.addView(snackbarParent)

//    rootView.addView(snackbarParent, 0)
//    ConstraintSet().apply {
//        clone(rootView)
//        connect(snackbarParent.id, ConstraintSet.BOTTOM, rootView.id, ConstraintSet.BOTTOM)
//        connect(snackbarParent.id, ConstraintSet.LEFT, rootView.id, ConstraintSet.LEFT)
//        connect(snackbarParent.id, ConstraintSet.RIGHT, rootView.id, ConstraintSet.RIGHT)
//        constrainWidth(snackbarParent.id, ConstraintSet.MATCH_CONSTRAINT)
//        applyTo(rootView)
//    }

    // init
    val snackbar = Snackbar.make(snackbarParent, message, length)
    // custom actionText
    snackbar.view.findViewById<TextView>(R.id.snackbar_text).apply {
        // set font
        typeface = ResourcesCompat.getFont(this@showSnackbar, messageFont ?: R.font.medium)
        // set line spacing
        setLineSpacing(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                3f,
                resources.displayMetrics
            ), 1f
        )
        // color
        textColor?.let { setTextColor(getCompatColor(it)) }
        // center actionText
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) textAlignment =
            View.TEXT_ALIGNMENT_CENTER
        else gravity = Gravity.CENTER_HORIZONTAL
    }
    // custom actionHandler button
    snackbar.view.findViewById<TextView>(R.id.snackbar_action).apply {
        // set font
        typeface = ResourcesCompat.getFont(this@showSnackbar, actionFont ?: R.font.bold)
        // color
        textColor?.let { setTextColor(getCompatColor(it)) }
    }
    // custom snackbar
    snackbar.apply {
        // background
        view.setBackgroundColor(
            ContextCompat.getColor(
                this@showSnackbar,
                when (type) {
                    SnackbarType.DEFAULT -> R.color.NeutralsAquaSpring
                    SnackbarType.SUCCESS -> R.color.status_default
                    SnackbarType.ERROR -> R.color.status_error
                    else -> backgroundColor ?: R.color.status_blank
                }
            )
        )
        // actionHandler actionText color
        setActionTextColor(getCompatColor(R.color.white))
        // actionHandler
        actionText?.let { text ->
            actionHandler?.let { handler ->
                setAction(text) { handler.invoke() }
            }
        }
        show()
    }
}
//
///**
// * Show default dialog
// * @param title title text
// * @param message message text
// * @param positiveButton positive button specifications. see [ButtonSpecs]
// * @param negativeButton negative button specifications. see [ButtonSpecs]
// * @param isCancelable set if dialog can be canceled
// * @param dark use dark layout design
// * @param titleFont override title font
// * @param messageFont override message font
// */
//fun Activity.showDefaultDialog(
//    title: String,
//    message: String,
//    positiveButton: ButtonSpecs? = null,
//    negativeButton: ButtonSpecs? = null,
//    isCancelable: Boolean? = false,
//    dark: Boolean? = false,
//    @FontRes titleFont: Int? = null,
//    @FontRes messageFont: Int? = null
//) {
//
//    showAlertDialog(
//        alertType = AlertDialogType.DEFAULT,
//        title = title,
//        message = message,
//        positiveButton = positiveButton,
//        negativeButton = negativeButton,
//        isCancelable = isCancelable,
//        dark = dark,
//        titleFont = titleFont,
//        messageFont = messageFont
//    )
//}

/**
 * Show info dialog
 * @param title title text
 * @param message message text
 * @param positiveButton positive button specifications. see [ButtonSpecs]
 * @param negativeButton negative button specifications. see [ButtonSpecs]
 * @param isCancelable set if dialog can be canceled
 * @param dark use dark layout design
 * @param titleFont override title font
 * @param messageFont override message font
 */
//fun Activity.showInfoDialog(
//    title: String,
//    message: String,
//    positiveButton: ButtonSpecs? = null,
//    negativeButton: ButtonSpecs? = null,
//    isCancelable: Boolean? = false,
//    dark: Boolean? = false,
//    @FontRes titleFont: Int? = null,
//    @FontRes messageFont: Int? = null
//) {
//
//    showAlertDialog(
//        alertType = AlertDialogType.INFO,
//        title = title,
//        message = message,
//        positiveButton = positiveButton,
//        negativeButton = negativeButton,
//        isCancelable = isCancelable,
//        dark = dark,
//        titleFont = titleFont,
//        messageFont = messageFont
//    )
//}

/**
 * Show confirmation dialog
 * @param title title text
 * @param message message text
 * @param positiveButton positive button specifications. see [ButtonSpecs]
 * @param negativeButton negative button specifications. see [ButtonSpecs]
 * @param isCancelable set if dialog can be canceled
 * @param dark use dark layout design
 * @param titleFont override title font
 * @param messageFont override message font
 */
//fun Activity.showConfirmationDialog(
//    title: String,
//    message: String,
//    positiveButton: ButtonSpecs? = null,
//    negativeButton: ButtonSpecs? = null,
//    isCancelable: Boolean? = false,
//    dark: Boolean? = false,
//    @FontRes titleFont: Int? = null,
//    @FontRes messageFont: Int? = null
//) {
//
//    showAlertDialog(
//        alertType = AlertDialogType.CONFIRMATION,
//        title = title,
//        message = message,
//        positiveButton = positiveButton,
//        negativeButton = negativeButton,
//        isCancelable = isCancelable,
//        dark = dark,
//        titleFont = titleFont,
//        messageFont = messageFont
//    )
//}

/**
 * Show warning dialog
 * @param title title text
 * @param message message text
 * @param positiveButton positive button specifications. see [ButtonSpecs]
 * @param negativeButton negative button specifications. see [ButtonSpecs]
 * @param isCancelable set if dialog can be canceled
 * @param dark use dark layout design
 * @param titleFont override title font
 * @param messageFont override message font
 */
//fun Activity.showWarningDialog(
//    title: String,
//    message: String,
//    positiveButton: ButtonSpecs? = null,
//    negativeButton: ButtonSpecs? = null,
//    isCancelable: Boolean? = false,
//    dark: Boolean? = false,
//    @FontRes titleFont: Int? = null,
//    @FontRes messageFont: Int? = null
//) {
//
//    showAlertDialog(
//        alertType = AlertDialogType.WARNING,
//        title = title,
//        message = message,
//        positiveButton = positiveButton,
//        negativeButton = negativeButton,
//        isCancelable = isCancelable,
//        dark = dark,
//        titleFont = titleFont,
//        messageFont = messageFont
//    )
//}

/**
 * Show warning dialog
 * @param title title text
 * @param message message text
 * @param positiveButton positive button specifications. see [ButtonSpecs]
 * @param negativeButton negative button specifications. see [ButtonSpecs]
 * @param isCancelable set if dialog can be canceled
 * @param dark use dark layout design
 * @param titleFont override title font
 * @param messageFont override message font
 */
fun Activity.showErrorDialog(
    title: String,
    message: String,
    positiveButton: ButtonSpecs? = null,
    negativeButton: ButtonSpecs? = null,
    isCancelable: Boolean? = false,
    dark: Boolean? = false,
    @FontRes titleFont: Int? = null,
    @FontRes messageFont: Int? = null
) {

    showAlertDialog(
        alertType = AlertDialogType.ERROR,
        title = title,
        message = message,
        positiveButton = positiveButton,
        negativeButton = negativeButton,
        isCancelable = isCancelable,
        dark = dark,
        titleFont = titleFont,
        messageFont = messageFont
    )
}
//
///**
// * Show success dialog
// * @param title title text
// * @param message message text
// * @param positiveButton positive button specifications. see [ButtonSpecs]
// * @param negativeButton negative button specifications. see [ButtonSpecs]
// * @param isCancelable set if dialog can be canceled
// * @param dark use dark layout design
// * @param titleFont override title font
// * @param messageFont override message font
// */
//fun Activity.showSuccessDialog(
//    title: String,
//    message: String,
//    positiveButton: ButtonSpecs? = null,
//    negativeButton: ButtonSpecs? = null,
//    isCancelable: Boolean? = false,
//    dark: Boolean? = false,
//    @FontRes titleFont: Int? = null,
//    @FontRes messageFont: Int? = null
//) {
//
//    showAlertDialog(
//        alertType = AlertDialogType.SUCCESS,
//        title = title,
//        message = message,
//        positiveButton = positiveButton,
//        negativeButton = negativeButton,
//        isCancelable = isCancelable,
//        dark = dark,
//        titleFont = titleFont,
//        messageFont = messageFont
//    )
//}

/**
 * Show date picker dialog
 * @param onDateSet callback once date is set
 */
fun Activity.showDatePickerDialog(onDateSet: (Calendar) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.apply{
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            onDateSet.invoke(calendar)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}

/**
 * Show time picker dialog
 * @param onTimeSet callback once time is set
 */
fun Activity.showTimePickerDialog(onTimeSet: (Time) -> Unit) {
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        this,
        TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            onTimeSet(Time(hour, minute, 0))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
    )
    timePickerDialog.show()
}

/**
 * Show short duration snackbar
 * @param type predefined background colors. see [SnackbarType]
 * @param message message text
 * @param actionText action text
 * @param actionHandler action handler function
 * @param textColor text colors
 * @param backgroundColor custom background color
 * @param messageFont override message font
 * @param actionFont override action font
 */
//fun Activity.showShortSnackbar(
//    root: ViewGroup? = null,
//    type: SnackbarType? = null,
//    message: String,
//    actionText: String? = null,
//    actionHandler: (() -> Unit)? = null,
//    @ColorRes textColor: Int? = null,
//    @ColorRes backgroundColor: Int? = null,
//    @FontRes messageFont: Int? = null,
//    @FontRes actionFont: Int? = null
//) {
//
//    showSnackbar(
//        root = root,
//        length = Snackbar.LENGTH_SHORT,
//        type = type,
//        message = message,
//        actionText = actionText,
//        actionHandler = actionHandler,
//        textColor = textColor,
//        backgroundColor = backgroundColor,
//        messageFont = messageFont,
//        actionFont = actionFont
//    )
//}

/**
 * Show long duration snackbar
 * @param type predefined background colors. see [SnackbarType]
 * @param message message text
 * @param actionText action text
 * @param actionHandler action handler function
 * @param textColor text colors
 * @param backgroundColor custom background color
 * @param messageFont override message font
 * @param actionFont override action font
 */
//fun Activity.showLongSnackbar(
//    type: SnackbarType? = null,
//    message: String,
//    actionText: String? = null,
//    actionHandler: (() -> Unit)? = null,
//    @ColorRes textColor: Int? = null,
//    @ColorRes backgroundColor: Int? = null,
//    @FontRes messageFont: Int? = null,
//    @FontRes actionFont: Int? = null
//) {
//
//    showSnackbar(
//        length = Snackbar.LENGTH_LONG,
//        type = type,
//        message = message,
//        actionText = actionText,
//        actionHandler = actionHandler,
//        textColor = textColor,
//        backgroundColor = backgroundColor,
//        messageFont = messageFont,
//        actionFont = actionFont
//    )
//}
//
///**
// * Show indefinite duration snackbar
// * @param type predefined background colors. see [SnackbarType]
// * @param message message text
// * @param actionText action text
// * @param actionHandler action handler function
// * @param textColor text colors
// * @param backgroundColor custom background color
// * @param messageFont override message font
// * @param actionFont override action font
// */
//fun Activity.showIndefiniteSnackbar(
//    type: SnackbarType? = null,
//    message: String,
//    actionText: String? = null,
//    actionHandler: (() -> Unit)? = null,
//    @ColorRes textColor: Int? = null,
//    @ColorRes backgroundColor: Int? = null,
//    @FontRes messageFont: Int? = null,
//    @FontRes actionFont: Int? = null
//) {
//
//    showSnackbar(
//        length = Snackbar.LENGTH_INDEFINITE,
//        type = type,
//        message = message,
//        actionText = actionText,
//        actionHandler = actionHandler,
//        textColor = textColor,
//        backgroundColor = backgroundColor,
//        messageFont = messageFont,
//        actionFont = actionFont
//    )
//}
//
///**
// * Camera permission handler
// */
//fun Activity.handleCameraPermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Camera",
//        permission = Manifest.permission.CAMERA,
//        onAccepted = onAccepted
//    )
//}
//
///**
// * Contacts permission handler
// */
//fun Activity.handleReadContactsPermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Read Contacts",
//        permission = Manifest.permission.READ_CONTACTS,
//        onAccepted = onAccepted
//    )
//}
//
///**
// * NFC permission handler
// */
//fun Activity.handleNFCPermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "NFC",
//        permission = Manifest.permission.NFC,
//        onAccepted = onAccepted
//    )
//}
//
///**
// * Audio permission handler
// */
//fun Activity.handleRecordAudioPermissions(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Record Audio",
//        permission = Manifest.permission.RECORD_AUDIO,
//        onAccepted = onAccepted
//    )
//}

/**
 * Write Storage permission handler
 */
//fun Activity.handleWriteStoragePermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Write Storage",
//        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        onAccepted = onAccepted
//    )
//}
//
///**
// * Location permission handler
// */
//fun Activity.handleFineLocationPermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Location",
//        permission = Manifest.permission.ACCESS_FINE_LOCATION,
//        onAccepted = onAccepted
//    )
//}
//
///**
// * SMS permission handler
// */
//fun Activity.handleSendSMSPermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Send SMS",
//        permission = Manifest.permission.SEND_SMS,
//        onAccepted = onAccepted
//    )
//}
//
///**
// * Read phone state permission handler
// */
//fun Activity.handleReadPhoneStatePermission(onAccepted: (() -> Unit)? = null) {
//    handlePermission(
//        textPermission = "Read Phone State",
//        permission = Manifest.permission.READ_PHONE_STATE,
//        onAccepted = onAccepted
//    )
//}

//fun Activity.handlePermission(
//    textPermission: String,
//    permission: String,
//    onAccepted: (() -> Unit)?
//) {
//    // permission handler
//    val permissionRequest = permissionsBuilder(permission).build()
//    permissionRequest.onAccepted {
//        onAccepted?.invoke()
//    }.onDenied {
//        permissionRequest.send()
//    }.onPermanentlyDenied {
//        // prompt user to update permissions in settings
//        showIndefiniteSnackbar(
//            message = "$textPermission access required. Go to Permissions -> Switch $textPermission ON",
//            actionText = "GO TO\nSETTINGS",
//            actionHandler = {
//                val intent =
//                    Intent(
//                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                        Uri.fromParts("package", packageName, null)
//                    )
//                startActivity(intent.newTask())
//            })
//    }.onShouldShowRationale { _, permissionNonce ->
//        // request for permission
//        showIndefiniteSnackbar(
//            message = "$textPermission access required",
//            actionText = "REQUEST\nPERMISSION",
//            actionHandler = { permissionNonce.use() })
//    }.send()    // check
//}