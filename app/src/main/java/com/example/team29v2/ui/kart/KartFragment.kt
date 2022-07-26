package com.example.team29v2.ui.kart

import MarkerColour
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.team29v2.R
import com.example.team29v2.data.Tseries
import com.example.team29v2.ui.DataFragment
import com.example.team29v2.ui.detaljer.DetaljFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog


class KartFragment : DataFragment(), OnMapReadyCallback,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var viewmodel = KartViewModel()
    private lateinit var mMap: GoogleMap
    private lateinit var badeData : MutableList<Tseries>
    private var locationList: ArrayList<LatLng>? = null
    private var badeDataDrawable = false // Blir satt til true umiddelbart dersom badedata finnes fra foer.
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_CODE : Int = 101

    @SuppressLint("PotentialBehaviorOverride", "InflateParams", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView = inflater.inflate(R.layout.activity_maps, container, false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) // Setter opp async-loading av funksjonen onMapReady (lenger ned).

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if(isLocationPermissionGranted()){
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
//                //Flytte marker etter bruker lokasjon
//            }
        } else {
            requestLocationPermission()
        }
        locationList = ArrayList()


        /* Listener som triggrer eventet override fun onSharedPreferenceChanged().
         Det skjer dersom settings blir endret fragmentet/vinduet i bakgrunnen.
         Dette gjoer at vi har flere sharedPref-variabler enn foerst planlagt,
         men det er ikke noe stort issue. */
        val prefListenerManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefListenerManager.registerOnSharedPreferenceChangeListener(this)


        // Kaller update/fetch av badesteder dersom det ikke finnes allerede.
        // Default er false, saa ved foerste oppstart vil dette kjoeres:

        // if (!sharedPref.getBoolean("mapUpToDate", false)) { // merk: false i getBoolean()-blokken er bare default response hvis verdien skulle mangle.
        // TODO: Condition for naar/hvor ofte, hvordan kartdata skal oppdatere seg.

        // fjernBadeDataFraDisk()

        if (!sjekkFinnesBadeData()) {
            Log.d("Map", "Data er ikke up to date. Begynner fetch av badesteder.")
            viewmodel.fetchBader()

            // Tar imot response fra viewmodel-observeren.
            viewmodel.getBader().observe(viewLifecycleOwner) { response ->

                lagreBadeDataTilDisk(response)

                badeData = response
                badeDataDrawable = true
                Log.d("Map", "Hentet badedata fra nett.")
                tegnKart(badeData) // Tegner kartet paa nytt saa fort response har blitt mottatt?
            }
        }

        // Hvis data finnes fra foer:
        else {
            badeData = hentBadeDataFraDisk()
            badeDataDrawable = true
            Log.d("Map", "Eksisterende data ble funnet, hentet badedata fra disk.")
        }
        return rootView
    }

    // TODO: Ble android emulator sin performance litt tregere etter at denne ble skilt ut? Litt usikker...
    @SuppressLint("PotentialBehaviorOverride", "SetTextI18n")
    fun tegnKart(response: MutableList<Tseries>) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        //skal sette en marker ved bruk av lat og lon som ble hentet fra api kall

        // Looper over items i badested-listen.
        for (i in response) {

            // Henter koordinater for aa lage kart-markers.
            val lat: String? = i.header?.extra?.pos?.lat
            val lon: String? = i.header?.extra?.pos?.lon
            val markPin = LatLng(lat!!.toDouble(), lon!!.toDouble())
            val navn = i.header.extra.name

            val farge = MarkerColour()
            val rgb = farge.interpolateColor(i.observations?.get(0)?.body?.value!!.toFloat(), 37F, response)
            val marker = mMap.addMarker(
                MarkerOptions().position(markPin).title("Marker in $navn")
                    .icon(BitmapDescriptorFactory.defaultMarker(farge.RGBtoHSV(rgb)))
            )
            marker?.tag = i
        }

        // Trigger naar en marker blir klikket paa.
        mMap.setOnMarkerClickListener { marke ->
            Log.d(TAG, "Clicked on ${marke.tag}")

            if (marke.title == "User Marker")
            {
                //ignorere user marker
            } else {
                // Midlertidig lagring som detaljvisning.
                if (marke.tag != null) {
                    lagreValgtBadestedTilDiskOgSettSomValgt(marke.tag as Tseries)


                val dialog = mapFragment.context?.let { BottomSheetDialog(it) }
                val view = layoutInflater.inflate(R.layout.cardviewet, null)
                val viewNavn = view.findViewById<TextView>(R.id.viewNavn)
                val viewTemp = view.findViewById<TextView>(R.id.viewTemp)
                val viewAirTemp = view.findViewById<TextView>(R.id.viewAirTemp)
                val viewWind = view.findViewById<TextView>(R.id.viewWind)
                val viewVaer = view.findViewById<ImageView>(R.id.viewVaer)

                val cancelBtn = view.findViewById<ImageButton>(R.id.canselBtn)
                val favoritBtn = view.findViewById<ToggleButton>(R.id.favoritBtn)

                viewNavn.text =
                    getString(R.string.kartPopupCardBadested) + (marke.tag as Tseries).header?.extra?.name.toString()
                viewTemp.text =
                    getString(R.string.kartPopupCardVaerNeste6t)
                val str  = "drawable/" + (marke.tag as Tseries).forecastDto.properties.timeseries[0].data.next_6_hours?.summary?.symbol_code.toString()
                val resID : Int = resources.getIdentifier(str, "drawable", context?.packageName)
                viewVaer.setImageResource(resID)
                viewAirTemp.text =
                    getString(R.string.kartPopupCardLuftTemp) + (marke.tag as Tseries).forecastDto.properties.timeseries[0].data.instant?.details?.air_temperature.toString() + getString(
                                            R.string.kartPopupCardTemperaturEnhet)
                viewWind.text =
                    getString(R.string.kartPopupCardVindhastighet) +
                            (marke.tag as Tseries).forecastDto.properties.timeseries[0].data.instant?.details?.wind_speed.toString() +
                            getString(R.string.kartPopupCardVindHastEnhet)
                dialog?.setCancelable(false)

                dialog?.setContentView(view)

                // Klikke utenfor kartet = cancel
                dialog?.setCanceledOnTouchOutside(true)

                // Vise selve dialogen
                dialog?.show()

                // Klikke paa cardet, men ikke paa favoritt eller x, forsoeke aa endre vindu til detaljvisning.
                view.setOnClickListener{
                    // TODO: Vise en toast her? Hvis vi ikke faar detaljview til aa funke.
                    lagreValgtBadestedTilDiskOgSettSomValgt(marke.tag as Tseries)

//                    val fragment = DetaljFragment()
//                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.detaljerHovedView, fragment)
//                    fragmentTransaction.addToBackStack(null)
//                    fragmentTransaction.commit()

                    dialog?.dismiss()
                    /* TODO: Skulle gjerne brukt koden under, men det gaar ikke fordi
                     navigate lager et layer oppaa kartet istedenfor flatt hierarki.
                     Maa finne en riktig maate aa gjoere dette paa.
                    val navController = findNavController()
                    navController.navigate(R.id.navigasjon_detaljer)
                    Log.d("Nav", "Navigert til badested.")
                    */
                }

                cancelBtn.setOnClickListener {
                    dialog?.dismiss()
                }

                    favoritBtn.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            if(favoritBtn.isChecked){
                                lagreBadeStedTilFavorittListe(marke.tag as Tseries)

                                val navnToast = (marke.tag as Tseries).header?.extra?.name

                                // Oppretter midlertidig toast:
                                val tempToast : Toast = Toast.makeText(context, "$navnToast" + getString(R.string.kartToastLagtTilFavoritt), Toast.LENGTH_SHORT)
                                tempToast.setGravity(Gravity.CENTER, 0, 30) // Setter toast sin visning til midten
                                tempToast.show() // Viser toast

                            } else {
                                //remove from favourite
                                val navnToast = (marke.tag as Tseries).header?.extra?.name

                                val tempToast : Toast = Toast.makeText(context, "$navnToast" + getString(R.string.kartToastFjernetFraFavoritt), Toast.LENGTH_SHORT)
                                tempToast.setGravity(Gravity.CENTER, 0, 30) // Setter toast sin visning til midten
                                tempToast.show() // Viser toast
                            }
                        }
                    })
                }
            }
            true
        }
    }



    // onMapReady kjoeres naar mapet er ferdig loaded (async).
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        if (badeDataDrawable) {
            Log.d("map", "tegner kart med det siste av badeData")
            tegnKart(badeData) // Tegner med det siste av data
        }

        val oslo = LatLng(59.91, 10.75)
        mMap.addMarker(MarkerOptions().position(oslo).title("User Marker").icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_baseline_person_pin_24)))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oslo))

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
        }

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.setMinZoomPreference(5.0f)
    }

    fun isLocationPermissionGranted() : Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        } else {
            return false
        }
    }

    fun requestLocationPermission(){
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_CODE)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.d("map", "en innstilling har blitt oppdatert! " + p0.toString() + p1)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}