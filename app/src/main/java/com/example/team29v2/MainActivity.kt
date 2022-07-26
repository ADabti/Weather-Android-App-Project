package com.example.team29v2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.team29v2.databinding.ActivityMainBinding
import com.example.team29v2.ui.PopupVindu
import com.google.android.material.bottomnavigation.BottomNavigationView


// MainActivity med bunnbar hentet fra android studio template.

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bunnmeny
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigasjon_favoritter, R.id.navigasjon_kart, R.id.navigasjon_detaljer
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // TODO: note to self:
        // for aa navigere til et sted, bruk navController.navigate(R.id.navigasjon_kart) !!
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toppmeny, menu)
        return true
    }

    // Funksjoner for hva som skjer naar man trykker paa et dropdown-alternativ.
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_setting -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_profile -> {
//            val aboutus = PopupVindu()
//            aboutus.showPopupWindow(findViewById(R.id.action_profile)!!)
            //msgShow("Profile")
            msgShow(getString(R.string.settingsToastOmAppen))
            true
        }
        R.id.refresh_api -> {
            msgShow(getString(R.string.settingsToastApiRefresh))
            val sharedPref : SharedPreferences = this.getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
            val prefEditor = sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.
            prefEditor.apply {
                putString("mapPrefs", "")
                putBoolean("mapUpToDate", true)
            }.apply() // Husk aa ta med .apply() for aa lagre.
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun msgShow(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}