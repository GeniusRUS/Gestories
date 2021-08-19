package com.genius.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import com.genius.gestories.InstagramGestureDetector

class MainActivity : AppCompatActivity(), InstagramGestureDetector.ActionsListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image = findViewById<ImageView>(R.id.image)
        val previous = findViewById<ImageView>(R.id.previous)
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
        Toast.makeText(this, "PAUSE", Toast.LENGTH_SHORT).show()
    }

    override fun onResumeProgress() {
        Toast.makeText(this, "RESUME", Toast.LENGTH_SHORT).show()
    }

    override fun onShowNextStories() {
        Toast.makeText(this, "NEXT", Toast.LENGTH_SHORT).show()
    }

    override fun onShowPreviousStories() {
        Toast.makeText(this, "PREVIOUS", Toast.LENGTH_SHORT).show()
    }
}