package com.groupphoto.app.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.groupphoto.app.R

class AddPhotoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_item, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        access_gallery.setOnClickListener {

//        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }






    companion object {
        private const val TAG = "Photos Fragment"
        const val PERMISSION_CODE = 1001
        const val IMAGE_PICK_CODE = 1000
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            Activity.RESULT_OK ->
//                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//
//                    //To get the File for further usage
//                    val auxFile = File(mCurrentPhotoPath)
//
//
//                    var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
////                    avatar.setImageBitmap(bitmap)
//
//                } else toast(data?.data.toString())
//            //            image_view.setImageURI(data?.data)
//        }
//
//
//    }
}