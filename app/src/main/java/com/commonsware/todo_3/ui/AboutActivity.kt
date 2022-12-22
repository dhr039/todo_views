package com.commonsware.todo_3.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.commonsware.todo_3.R
import com.commonsware.todo_3.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = getString(R.string.app_name)

        /**
         * file:///android_asset/ points to the root of assets/
         * and yes, file:///android_asset/ is singular, and assets/ is plural (p.145)
         * */
        binding.about.loadUrl("file:///android_asset/about.html")
    }
}