package com.genius.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.genius.gestories.InstagramGestureDetector

class ViewPagerActiveFragment : Fragment(R.layout.viewpager_active_fragment) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = view.findViewById<ImageView>(R.id.image)
        val previous = view.findViewById<ImageView>(R.id.previous)
        val detector = InstagramGestureDetector(requireActivity() as? InstagramGestureDetector.ActionsListener)
        image.setOnTouchListener(detector)
        image.doOnPreDraw {
            previous.setPadding(
                (image.width / 100F * 33F).toInt(),
                0,
                0,
                0
            )
        }
    }
}