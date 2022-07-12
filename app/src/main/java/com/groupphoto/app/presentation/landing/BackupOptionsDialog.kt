package com.groupphoto.app.presentation.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.groupphoto.app.R
import com.groupphoto.app.data.repository.local.pref.SharedPref
import kotlinx.android.synthetic.main.dialog_backup_options.*
import org.jetbrains.anko.startActivity

class BackupOptionsDialog:
    BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_backup_options, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog_radio_group.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = requireActivity().findViewById(checkedId)

        }


        dialog_backup_continue.setOnClickListener {
                var id: Int = dialog_radio_group.checkedRadioButtonId
                if (id != -1) {
                    val radio: RadioButton = requireActivity().findViewById(id)
                    SharedPref.backupOption = radio.text.toString()
                    requireActivity().finish()
                    requireActivity().startActivity<LandingActivity>()
                } else {
                    Toast.makeText(
                        requireActivity(), "Please select one.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }



    companion object {
        val TAG = "Confirm Login Dialog"
    }



}