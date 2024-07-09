package com.example.progettolam.ui.profileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.R
class ProfileFragment: Fragment() {
    lateinit var profileModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val textView: TextView = view.findViewById(R.id.profileText)
        profileModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        textView.setOnClickListener() {
            profileModel.changeTest()
        }
    }
}