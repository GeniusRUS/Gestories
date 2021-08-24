package com.genius.example

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.genius.gestories.InstagramGestureDetector

class HostActivity : AppCompatActivity(R.layout.activity_main), InstagramGestureDetector.ActionsListener {

    private lateinit var currentStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentStatus = findViewById(R.id.current_status)
    }

    override fun onPauseProgress() {
        Log.d(TAG, "PAUSE")
        currentStatus.setText(R.string.stories_paused)
    }

    override fun onResumeProgress() {
        Log.d(TAG, "RESUME")
        currentStatus.setText(R.string.stories_resumed)
    }

    override fun onShowNextStories() {
        Log.d(TAG, "NEXT")
        currentStatus.setText(R.string.stories_next)
    }

    override fun onShowPreviousStories() {
        Log.d(TAG, "PREVIOUS")
        currentStatus.setText(R.string.stories_previous)
    }

    override fun onLongTapDetected() {
        Log.d(TAG, "LONG TAP")
        currentStatus.setText(R.string.stories_long_tap)
    }

    private companion object {
        private const val TAG = "Gesture"
    }
}