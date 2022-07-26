package com.example.team29v2.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.example.team29v2.data.Tseries
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*
DataFragment er fragmentsuperklassen som har funksjoner relevante for de tre hovedfragmentene.
Brukes til lagring/henting av data uavhengig av valgt visningsmodus.
Hvert fragment visualiserer saa data paa hver sin maate.

Alt som er felles i alle fragmentene plasseres her, saa man unngaar copy/paste.

For aa inherite fra en class i Kotlin, saa maa den spesifiseres som "open class".
(Classes i kotlin er ikke inheritable by default.)
Funksjoner definert her kan kalles paa vanlig vis fra fragmentene. */
open class DataFragment : Fragment() {

    /*  For aa ta vare paa API-data offline kan vi lagre resultatet i en SharedPreferences-seksjon.
   Lagrer data til app-spesifikk internal storage.
   SharedPreferences stoetter kun primitive typer som string og bool.
   Bruker derfor Gson til deserialisering av respons fra API-et m.m.
   Nyttig naar man ikke vil laste inn data fra nett hver gang, og for at appen tar vare paa
   data mellom sessions. Denne lagrede dataen vil ogsaa fjernes hvis appen avinstalleres. */


    // Hjelpefunksjoner for serialisering og deserialisering.
    // TODO: Sannsynligvis saa trenger man ikke forskjellige funksjoner her?
    private fun deserialiserListe(serialisertStreng: String?): MutableList<Tseries> {
        val minGson = Gson()

        // Strengen deserialiseres ved hjelp av TypeToken og Gson/fromJson.
        val itemType = object : TypeToken<MutableList<Tseries>>(){}.type
        val deserialisert = minGson.fromJson<MutableList<Tseries>>(serialisertStreng, itemType)

        return deserialisert
    }

    private fun serialiserListe(deserialisertObjekt: MutableList<Tseries>): String {
        // Lagre responset til Json:
        val minGson = Gson()
        val badestedListeStreng: String = minGson.toJson(deserialisertObjekt) // Strengen som Gson sin toJson settes inn i.

        return badestedListeStreng
    }

    fun serialiserBadestrand(deserialisertObjekt: Tseries): String {
        // Lagre responset til Json:
        val minGson = Gson()
        val badeStrandStreng: String = minGson.toJson(deserialisertObjekt) // Strengen som Gson sin toJson settes inn i.

        return badeStrandStreng
    }

    fun deserialiserBadested(serialisertStreng: String?): Tseries {
        val minGson = Gson()

        // Strengen deserialiseres ved hjelp av TypeToken og Gson/fromJson.
        val itemType = object : TypeToken<Tseries>(){}.type
        val deserialisertBadested = minGson.fromJson<Tseries>(serialisertStreng, itemType)

        return deserialisertBadested
    }


    // Lagring og henting av data.

    // Lagre badedata
    fun lagreBadeDataTilDisk(response: MutableList<Tseries>) {
        // Shared preference-variabel opprettes og peker rettes til preferansenavn "mapPrefs"
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.

        val badeDataJsonStreng = serialiserListe(response)

        // Editoren tar saa imot strengen og lagrer den til mapPrefs.
        prefEditor.apply {
            putString("mapPrefs", badeDataJsonStreng)
            putBoolean("mapUpToDate", true)
        }.apply() // Husk aa ta med .apply() for aa lagre.
    }

    fun sjekkFinnesBadeData(): Boolean {
        // Henter strengen fra sharedPreferences->mapPrefs.
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val badeDataHentetStreng: String? = sharedPref.getString("mapPrefs", "")
        if (badeDataHentetStreng == "") {
            return false
        }
        return true
    }

    fun hentBadeDataFraDisk(): MutableList<Tseries> {
        // Shared preference-variabel opprettes og peker rettes til preferansenavn "mapPrefs"
        // Henter strengen fra sharedPreferences->mapPrefs.
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val badeDataHentetStreng: String? = sharedPref.getString("mapPrefs", "")
//        val editor : SharedPreferences.Editor = sharedPref.edit()
//        editor.clear();
//        editor.apply(); // commit changes

        // Strengen deserialiseres ved hjelp av TypeToken og Gson/fromJson.
        val deserialisertListe = deserialiserListe(badeDataHentetStreng)
        return deserialisertListe
    }

    fun lagreBadeStedTilFavorittListe(badested: Tseries) {
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.
        val favorittListeObj = hentFavorittListeFraDisk()
        if(favorittListeObj.contains(badested)==false){
            favorittListeObj.add(badested)

        }

        val favorittListeEtterStreng = serialiserListe(favorittListeObj)

        prefEditor.apply {
            putString("Favorittliste", favorittListeEtterStreng)
        }.apply() // Husk aa ta med .apply() for aa lagre.
    }

    fun overskrivFavorittliste(innListe: MutableList<Tseries>) {
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.
        val favorittListeEtterStreng = serialiserListe(innListe)
        prefEditor.apply {
            putString("Favorittliste", favorittListeEtterStreng)
        }.apply() // Husk aa ta med .apply() for aa lagre.
    }

    // Henter favorittliste fra disk, eller returnerer en tom liste til videre bruk dersom det ikke finnes.
    fun hentFavorittListeFraDisk(): MutableList<Tseries> {

        // Hente liste fra disk
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val favorittListeStreng = sharedPref.getString("Favorittliste", "")

        // Dersom listen ikke finnes fra foer, returner tom liste:
        if (favorittListeStreng == "") {
            val tomListe: MutableList<Tseries> = mutableListOf<Tseries>()
            return tomListe
        }

        val favorittlisteObj = deserialiserListe(favorittListeStreng)
        return favorittlisteObj
    }

    // Setter en strand som "valgt strand", dvs. at den vil aapnes by default naar man gaar paa detaljvisning.
    fun lagreValgtBadestedTilDiskOgSettSomValgt(innBadested: Tseries) {
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.

        val badestedStreng = serialiserBadestrand(innBadested)
        prefEditor.apply {
            putString("ValgtBadested", badestedStreng)
        }.apply() // Husk aa ta med .apply() for aa lagre.
    }

    fun hentValgtBadestedFraDisk(): Tseries {
        // Henter badested fra disk
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val valgtBadestedStreng = sharedPref.getString("ValgtBadested", "")

        return deserialiserBadested(valgtBadestedStreng)
    }

    // Testfunksjon for aa nullstille sist valgt badested. Ikke i bruk utenom en evt. clear all data.
    fun nullstillBadested() {
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit() // Editor-pointer, brukes til aa plassere og hente data paa sharedPref.
        val badestedStreng = ""
        prefEditor.apply {
            putString("ValgtBadested", badestedStreng)
        }.apply() // Husk aa ta med .apply() for aa lagre.
    }

    fun finnesDetValgtBadeSted() : Boolean {
        // Henter badested fra disk
        val sharedPref : SharedPreferences = requireActivity().getSharedPreferences("mapPrefs", Context.MODE_PRIVATE)
        val valgtBadestedStreng = sharedPref.getString("ValgtBadested", "")
        return valgtBadestedStreng != ("")
    }
}