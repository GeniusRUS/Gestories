package com.genius.example

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.genius.gestories.InstagramGestureDetector

class HostActivity : AppCompatActivity(R.layout.activity_main), InstagramGestureDetector.ActionsListener {

    override fun onPauseProgress() {
        Toast.makeText(this, R.string.stories_paused, Toast.LENGTH_SHORT).show()
    }

    override fun onResumeProgress() {
        Toast.makeText(this, R.string.stories_resumed, Toast.LENGTH_SHORT).show()
    }

    override fun onShowNextStories() {
        Toast.makeText(this, R.string.stories_next, Toast.LENGTH_SHORT).show()
    }

    override fun onShowPreviousStories() {
        Toast.makeText(this, R.string.stories_previous, Toast.LENGTH_SHORT).show()
    }
}