package com.genius.example

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class StartFragment : Fragment(R.layout.start_fragment), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.static_fragment).setOnClickListener(this)
        view.findViewById<Button>(R.id.scroll_fragment).setOnClickListener(this)
        view.findViewById<Button>(R.id.viewpager_fragment).setOnClickListener(this)
        view.findViewById<Button>(R.id.bottomsheet_fragment).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val direction = when (v?.id) {
            R.id.static_fragment -> StartFragmentDirections.actionStartFragmentToStaticFragment()
            R.id.scroll_fragment -> StartFragmentDirections.actionStartFragmentToScrollFragment()
            R.id.viewpager_fragment -> StartFragmentDirections.actionStartFragmentToViewPagerFragment()
            R.id.bottomsheet_fragment -> StartFragmentDirections.actionStartFragmentToBottomSheetDialogFragment()
            else -> null
        }
        findNavController().navigate(direction ?: return)
    }
}