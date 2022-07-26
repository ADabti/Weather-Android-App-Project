package com.example.team29v2.ui.detaljer

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.example.team29v2.BuildConfig
import com.example.team29v2.R
import com.example.team29v2.databinding.FragmentDetaljerFramelayoutBinding
import com.example.team29v2.ui.DataFragment


class DetaljFragment : DataFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var _binding: FragmentDetaljerFramelayoutBinding? = null

    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetaljerFramelayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /* Settings listener. */
        val prefListenerManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefListenerManager.registerOnSharedPreferenceChangeListener(this)

        tegnDetaljertVisning()

        return root
    }

    // Funksjon for aa oppdatere tekst paa skjermen. Kjoeres i onCreateView,
    // ELLER naar en oppdatering fra API/internett forekommer (TODO).
    @SuppressLint("SetTextI18n")
    private fun tegnDetaljertVisning() {

        /* Detaljvisningen bruker et kombinert xml framelayout-view.
        Det tillater at flere views kan pakkes inn i en ramme og skrus av og på.
        Det krever litt ekstra xml-filer og referanser, men virker veldig nyttig i lengden. */

        // Hvis badested ikke er selected, vis bakgrunnsmelding istedenfor favorittliste.
        if (!finnesDetValgtBadeSted()) {
            binding.detaljerTomtView.tomDetaljerTextView.visibility = View.VISIBLE;
            binding.detaljerHovedView.root.visibility = View.GONE;
        }

        // Hvis favorittlisten har elementer, skjul beskjeden og vis listen istedenfor.
        else {
            binding.detaljerTomtView.tomDetaljerTextView.visibility = View.GONE;
            binding.detaljerHovedView.root.visibility = View.VISIBLE;

            // Hente valgt badested fra minne/disk
            val valgtBadested = hentValgtBadestedFraDisk()

            // Oppdatere tekst med data fra badestedet
            val badestedsNavn: TextView = binding.detaljerHovedView.badeStedsnavnTekst
            badestedsNavn.text = valgtBadested.header?.extra?.name.toString()

            val tempTypeLuft: TextView = binding.detaljerHovedView.tempTypeLuft
            tempTypeLuft.text = getString(R.string.detaljerLuftTemperatur)

            val navnHavvarsel: TextView = binding.detaljerHovedView.badeStedsnavnTekstHavvarsel
            navnHavvarsel.text = getString(R.string.detaljerVanntemperatur)

            val badestedTemp: TextView = binding.detaljerHovedView.badeTempTekstHavvarsel
            val badestedTempe = valgtBadested.observations?.get(0)?.body?.value.toString() + getString(R.string.detaljerLuftTemperaturEnhet)

            badestedTemp.text = badestedTempe

            val luftTemperatur: TextView = binding.detaljerHovedView.badeTempTekst
            luftTemperatur.text =
                valgtBadested.forecastDto.properties.timeseries[0].data.instant?.details?.air_temperature.toString() + getString(R.string.detaljerLuftTemperaturEnhet)

            val vaerMelding: TextView = binding.detaljerHovedView.vaerNeste6timerTekst
            vaerMelding.text =
                getString(R.string.detaljerVaervarsel)

            val img :ImageView = binding.detaljerHovedView.imageView3
            val str  = "drawable/" + valgtBadested.forecastDto.properties.timeseries[0].data.next_6_hours?.summary?.symbol_code.toString()
            val resID : Int = resources.getIdentifier(str, "drawable", context?.packageName)
            img.setImageResource(resID)

            val vindHastighet: TextView = binding.detaljerHovedView.vindHastighetTekst
            vindHastighet.text =
                getString(R.string.detaljerVindhastighet) + valgtBadested.forecastDto.properties.timeseries[0].data.instant?.details?.wind_speed.toString()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        // Innstillinger endret
    }
}
