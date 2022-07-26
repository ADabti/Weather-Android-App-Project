import android.app.Activity
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.example.team29v2.MainActivity
import com.example.team29v2.data.Tseries
import com.example.team29v2.ui.DataFragment
import com.google.maps.android.SphericalUtil.interpolate


class MarkerColour() {
    val colorStart = Color.GREEN
    val colorEnd = Color.RED
    //val activity = Activity()
//    fun interpolate(a: Float, b: Float, proportion: Float): Float {
//        return a + (b - a) * proportion
//        //return (Math.min(colorStart, colorEnd) * (100 - percent) + Math.max(colorStart,colorEnd) * percent) / 100
//    }

    /**
     * Returns an interpolated color, between `a` and `b`
     * proportion = 0, results in color a
     * proportion = 1, results in color b
     */
    fun interpolateColor(temperature: Float, preference: Float, badeData: MutableList<Tseries>): Int {
        val proportion = calculateProportion(temperature,preference,badeData)
        require(!(proportion > 1 || proportion < 0)) { "proportion must be [0 - 1]" }
        return ColorUtils.blendARGB(colorStart, colorEnd, proportion)
    }

    fun calculateProportion(temperature: Float, preference: Float, badeData: MutableList<Tseries>) : Float {
        var min = badeData[0].observations?.get(0)?.body?.value
        var max = badeData[0].observations?.get(0)?.body?.value
        var current : String?
        val itr = badeData.listIterator()
        while (itr.hasNext()){
            current = itr.next().observations?.get(0)?.body?.value
            if (current!!.toFloat() < min!!.toFloat()){
                min = current
            } else if (current!!.toFloat() > max!!.toFloat()){
                max = current
            }
        }
        //set min as 0 and max as 100
        if (temperature < preference){
            return ((preference - temperature) / (preference - min!!.toFloat()) * 0.5).toFloat()
        } else if (preference < temperature) {
            return ((temperature - preference) / (max!!.toFloat() - preference) * 0.5 + 0.5).toFloat()
        } else {
            return 0.5F
        }

    }

    fun RGBtoHSV (argb :Int) : Float{
        val hsv_output = FloatArray(3)
        Color.colorToHSV(argb, hsv_output)
        return hsv_output[0]
    }
}