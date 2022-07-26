package com.example.team29v2.ui.favoritter

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.team29v2.data.Tseries
import com.example.team29v2.databinding.FragmentFavoritterFramelayoutBinding
import com.example.team29v2.ui.DataFragment
import com.example.team29v2.ui.kart.KartViewModel

class FavorittFragment : DataFragment(), SharedPreferences.OnSharedPreferenceChangeListener, RecyclerViewAdapter.CallbackInterface {
    var recyclerView: RecyclerView? = null
    private var _binding: FragmentFavoritterFramelayoutBinding? = null
    private val binding get() = _binding!!
    private var viewmodel = FavorittViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritterFramelayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView = binding.favorittHovedView.favorittListeRecyclerView

        val prefListenerManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefListenerManager.registerOnSharedPreferenceChangeListener(this)

        tegnfavoritter()

        return root
    }

    // Stokke om paa cards:
    // Naar man faar callback fra interfacet, oppdater favorittlisten paa disk.
    override fun passResultCallback(message: MutableList<Tseries>) {
        overskrivFavorittliste(message)
    }

    /* Settings:
    siden SharedPreferences sin interface-listener ikke gir callbacks ved nullstillingsfunksjonene,
    saa kjoerer vi en manuell sjekk hver gang favorittvinduet faar fokus. Hvis favorittlisten er tom,
    saa tegner vi favoritter paa nytt (og som konsekvens faar vi etterhvert melding opp om at listen er tom.)
     */
    override fun onResume() {
        super.onResume()
        tegnfavoritter()
    }


    private fun tegnfavoritter() {
        val favorittListe = hentFavorittListeFraDisk()

        /* Favorittlisteviewet bruker et kombinert xml framelayout-view.
        Det tillater at flere views kan pakkes inn i en ramme og skrus av og på.
        Det krever litt ekstra xml-filer og referanser, men virker veldig nyttig i lengden. */

        // Hvis favorittlisten er tom, vis bakgrunnsmelding istedenfor favorittliste.
        if (favorittListe.size == 0) {
            binding.favorittTomtView.tomFavorittlisteTextView.visibility = View.VISIBLE;
            binding.favorittHovedView.favorittListeRecyclerView.visibility = View.GONE;
        }

        // Hvis favorittlisten har elementer, skjul beskjeden og vis listen istedenfor.
        else {
            binding.favorittTomtView.tomFavorittlisteTextView.visibility = View.GONE;
            binding.favorittHovedView.favorittListeRecyclerView.visibility = View.VISIBLE;

            val mAdapter = RecyclerViewAdapter(favorittListe, this)
            val callback: ItemTouchHelper.Callback = ItemMoveCallback(mAdapter)
            val touchHelper = ItemTouchHelper(callback)

            touchHelper.attachToRecyclerView(recyclerView)
            recyclerView!!.adapter = mAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.d("map", "en innstilling har blitt oppdatert! " + p0.toString() + p1)
        if(p1 == "favnullstilt"){
            //
        }
    }

    private fun oppdaterBadeData(){
        if (!sjekkFinnesBadeData()) {
            Log.d("Map", "Data er ikke up to date. Begynner fetch av badesteder.")
            viewmodel.fetchBader()

            // Tar imot response fra viewmodel-observeren.
            viewmodel.getBader().observe(viewLifecycleOwner) { response ->

                lagreBadeDataTilDisk(response)

                tegnfavoritter()
                Log.d("Map", "Hentet badedata fra nett.")
            }
        }
    }
}