package com.example.progettolam.ui.homeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.progettolam.R

class HomeFragment: Fragment() {

    lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val textView: TextView = view.findViewById(R.id.textView)
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        textView.setOnClickListener() {
            viewModel.changeTest()
        }

    }
}