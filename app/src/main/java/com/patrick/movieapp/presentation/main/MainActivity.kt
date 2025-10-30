package com.patrick.movieapp.presentation.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.patrick.movieapp.R
import com.patrick.movieapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup Bottom Navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Controlar visibilidad del Bottom Navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Mostrar Bottom Nav en estos fragments
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.favoritesFragment,
                R.id.customListsFragment,
                R.id.ratingsFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                // Ocultar en los demás
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
}