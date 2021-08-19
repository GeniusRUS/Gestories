package com.genius.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.genius.gestories.InstagramGestureDetector

class ScrollFragment : Fragment(R.layout.scroll_fragment), InstagramGestureDetector.ActionsListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = view.findViewById<ImageView>(R.id.image)
        val previous = view.findViewById<ImageView>(R.id.previous)
        val detector = InstagramGestureDetector(this)
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

    override fun onPauseProgress() {
        Toast.makeText(requireContext(), "PAUSE", Toast.LENGTH_SHORT).show()
    }

    override fun onResumeProgress() {
        Toast.makeText(requireContext(), "RESUME", Toast.LENGTH_SHORT).show()
    }

    override fun onShowNextStories() {
        Toast.makeText(requireContext(), "NEXT", Toast.LENGTH_SHORT).show()
    }

    override fun onShowPreviousStories() {
        Toast.makeText(requireContext(), "PREVIOUS", Toast.LENGTH_SHORT).show()
    }
}