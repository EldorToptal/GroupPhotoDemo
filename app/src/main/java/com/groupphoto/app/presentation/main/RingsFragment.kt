package com.groupphoto.app.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.groupphoto.app.R
import com.groupphoto.app.domain.UI
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_rings_contacts.*

class RingsFragment : Fragment() {
    private val ringsContactsAdapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_rings_contacts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rings_contacts_recycler.adapter = ringsContactsAdapter

        ringsContactsAdapter.addAll(UI.contacts)

    }
    companion object {
        private const val TAG = "Rings Fragment"
    }
}