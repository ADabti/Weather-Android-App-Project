package com.example.team29v2.ui
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.team29v2.R

class PopupVindu {
    //PopupWindow display method
    fun showPopupWindow(view: View) {

        //Create a View object yourself through inflater
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.om_oss, null)

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        //Initialize the elements of our window, install the handler
        val beskrivelse = popupView.findViewById<TextView>(R.id.OmOss)
        //test2.setText(R.string.app_name)
        val dismissButton = popupView.findViewById<Button>(R.id.knapp)
        dismissButton.setOnClickListener { //As an example, display the message
            popupWindow.dismiss()
        }
    }
}