package com.example.team29v2.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.DialogPreference
import com.example.team29v2.R

// Diverse custom buttons til bruk i instillingsmenyen havner i denne klassen.

// Ja/nei-knapp for aa nullstille lokasjonsdata.
class YesNo(context: Context, attrs: AttributeSet?) :
    DialogPreference(context, attrs) {

    override fun onClick() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(context)

        // Setter tekst i tekstboksen
        dialog.setTitle(context.getString(R.string.fjerneLokasjonsDataYesNoOverskrift))
        dialog.setMessage(context.getString(R.string.fjerneLokasjonsDataYesNoBeskrivelse))

        // Funksjonalitet:
        dialog.setCancelable(true)
        dialog.setPositiveButton(context.getString(R.string.Nullstill)) { dialog, which ->

            // TODO: Kode for aa nullstille brukerens posisjon havner her:
            val sharedPref: SharedPreferences =
                context.getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
            val prefEditor =
                sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.

            Toast.makeText(context, context.getString(R.string.fjernetLokasjonsDataToast), Toast.LENGTH_SHORT).show()
        }

        // Litt kaotisk kode her, men det er bare en cancel.
        dialog.setNegativeButton(context.getString(R.string.Avbryt))
        { dlg, which -> dlg.cancel() }

        // Vise selve dialogboksen:
        val al: AlertDialog = dialog.create()
        al.show()
    }
}

// Ja-nei-knapp for aa nullstille favorittlisten.
class NullstillFavorittYesNo(context: Context, attrs: AttributeSet?) :
    DialogPreference(context, attrs) {

    override fun onClick() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(context)
        dialog.setTitle(context.getString(R.string.fjerneFavorittlisteYesNoPopupOverskrift))
        dialog.setMessage(context.getString(R.string.fjerneFavorittlisteYesNoPopupBeskrivelse))
        dialog.setCancelable(true)
        dialog.setPositiveButton(context.getString(R.string.Nullstill)) { dialog, which ->

            // Kode for aa nullstille favorittlisten havner her:
            val sharedPref: SharedPreferences =
                context.getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
            val prefEditor =
                sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.
            prefEditor.putString("Favorittliste", "").apply()
            Toast.makeText(context, context.getString(R.string.fjerneFavorittlisteFullfoertToast), Toast.LENGTH_SHORT).show()
        }

        dialog.setNegativeButton(context.getString(R.string.Avbryt))
        { dlg, which -> dlg.cancel() }

        val al: AlertDialog = dialog.create()
        al.show()
    }
}