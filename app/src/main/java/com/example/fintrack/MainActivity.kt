package com.example.fintrack

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fintrack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        //val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                    as NavHostFragment

        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_login, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        dbHelper = DatabaseHelper(this)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_login -> {
                    if (isUserLoggedIn) {
                        Toast.makeText(this, "Tekan tombol logout terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false
                    } else {
                        navController.navigate(R.id.navigation_login)
                        return@setOnItemSelectedListener true
                    }
                }
                R.id.navigation_dashboard -> {
                    if (isUserLoggedIn) {
                        navController.navigate(R.id.navigation_dashboard)
                        return@setOnItemSelectedListener true
                    } else {
                        Toast.makeText(this, "Mohon untuk login terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false
                    }

                }
                R.id.navigation_settings -> {
                    if (isUserLoggedIn) {
                        navController.navigate(R.id.navigation_settings)
                        return@setOnItemSelectedListener true
                    } else {
                        Toast.makeText(this, "Mohon untuk login terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false
                    }
                }
                else -> false
            }
        }
    }
}