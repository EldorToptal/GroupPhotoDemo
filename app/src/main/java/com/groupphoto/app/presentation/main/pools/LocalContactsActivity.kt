package com.groupphoto.app.presentation.main.pools

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.groupphoto.app.R
import com.groupphoto.app.presentation.recycleritems.LocalContactsItem
import com.groupphoto.app.presentation.viewmodels.LandingActivityEvent
import com.groupphoto.app.presentation.viewmodels.LandingActivityViewModel
import com.pawegio.kandroid.e
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_local_contacts.*
import org.jetbrains.anko.startActivity

import org.koin.androidx.viewmodel.ext.viewModel


class LocalContactsActivity : AppCompatActivity() {
    @SuppressLint("InlinedApi")
    private val FROM_COLUMNS: Array<String> = arrayOf(
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        } else {
            ContactsContract.Contacts.DISPLAY_NAME
        }
    )

    private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)
    private val viewModel: LandingActivityViewModel by viewModel()

    private var contactListAdapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_contacts)

        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is LandingActivityEvent.TabSwitchRequested -> e("Landing")
            }
        })


        contactListAdapter = GroupAdapter<GroupieViewHolder>().apply {
            setOnItemClickListener { item, view ->
                startActivity<GalleryActivity>()
            }
        }

        contacts_recycler.adapter = contactListAdapter
        loadContacts()
    }


    private fun loadContacts() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }

    private fun getContacts() {
        val builder = StringBuilder()
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null)

        var listOfContacts  = mutableListOf<LocalContactsItem>()
        if (cursor!!.count > 0) {
            while (cursor!!.moveToNext()) {
                val id = cursor!!.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                    if(cursorPhone!!.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                                phoneNumValue).append("\n\n")
                            listOfContacts.add(LocalContactsItem(name,R.drawable.sample_pool_ic))
                            e("Name ===>",phoneNumValue);
                        }
                    }
                    cursorPhone.close()
                }
            }
        } else {
            //   toast("No contacts available!")
        }
        cursor.close()

        contactListAdapter.clear()
        contactListAdapter.addAll(listOfContacts)
//        return builder
    }



    companion object {
        private const val TAG = "Gallery Activity "
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }
}