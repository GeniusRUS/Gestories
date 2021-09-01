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

    override fun onActionReceive(action: String) {
        Log.d(TAG, action)
        when (action) {
            InstagramGestureDetector.GestureAction.PAUSE -> currentStatus.setText(R.string.stories_paused)
            InstagramGestureDetector.GestureAction.RESUME -> currentStatus.setText(R.string.stories_resumed)
            InstagramGestureDetector.GestureAction.NEXT -> currentStatus.setText(R.string.stories_next)
            InstagramGestureDetector.GestureAction.PREVIOUS -> currentStatus.setText(R.string.stories_previous)
            InstagramGestureDetector.GestureAction.LONG_TAP -> currentStatus.setText(R.string.stories_long_tap)
        }
    }

    private companion object {
        private const val TAG = "Gesture"
    }
}