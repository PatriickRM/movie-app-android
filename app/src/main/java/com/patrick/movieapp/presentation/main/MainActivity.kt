package com.patrick.movieapp.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.patrick.movieapp.R
import com.patrick.movieapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    com.patrick.movieapp.presentation.auth.LoginFragment())
                .commit()
        }
    }
}