package com.example.calculatorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.calculatorapp.databinding.ActivityMainBinding

/**
 * Main Activity and the entry point for the app
 * */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val binding = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setContentView(binding.root)

        // Get the navigation host fragment from this Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController

        // Make sure actions in the ActionBar get propagated to the NavController
        setupActionBarWithNavController(navController)
    }

//    /**
//     * Loads the appropriate orientation layout depending on the device's orientation
//     * */
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        // Check orientation of the device
//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            setContentView(R.layout.fragment_calculator)
//        }else{
//            setContentView(R.layout.fragment_calculator)
//        }
//    }
}