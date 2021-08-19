package com.genius.example

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.genius.gestories.InstagramGestureDetector

class MainActivity : AppCompatActivity(), InstagramGestureDetector.ActionsListener {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image = findViewById<ImageView>(R.id.image)
        val detector = InstagramGestureDetector(this)
        image.setOnTouchListener(detector)
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