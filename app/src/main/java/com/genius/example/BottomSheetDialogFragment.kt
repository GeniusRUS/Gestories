package com.genius.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import com.genius.gestories.InstagramGestureDetector
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragment_dialog, container, false)
    }

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