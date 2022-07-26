// Fullskjerm innstillingsvindu som kan aapnes fra hovedsiden.
/*
Relevante filer er:
SettingsActivity.kt (denne)
xml/root_preferences.xml - viktig fil som brukes til selve listen over de forskjellige valgene, inkl. forskjellige typer knapper
values/arrays.xml - brukes til spesifikke valgmuligheter naar man klikker paa en innstilling
values/strings.xml - brukes til tekst
*/

package com.example.team29v2

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        // .setDisplayHomeAsUpEnabled er "tilbakepilen" oppe til venstre. True for aa vise.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    // Tilbakepil oeverst til venstre.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                lukkVindu()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Lukke innstillingsvindu
    private fun lukkVindu() {
        finish()
        // NavUtils.navigateUpFromSameTask(this) -- denne sender tilbake kun til favorittlisten?
        // Bruker derfor finish() enn saa lenge.
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}