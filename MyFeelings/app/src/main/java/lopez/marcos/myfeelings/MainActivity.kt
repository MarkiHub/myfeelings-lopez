package lopez.marcos.myfeelings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.widget.ConstraintLayout
import lopez.marcos.myfeelings.ui.theme.MyFeelingsTheme
import lopez.marcos.myfeelings.utilities.CustomBarDrawable
import lopez.marcos.myfeelings.utilities.CustomCircleDrawable
import lopez.marcos.myfeelings.utilities.Emociones
import lopez.marcos.myfeelings.utilities.JSONFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    var jsonFile: JSONFile? = null
    var veryhappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verysad = 0.0F
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val graphVH:View = findViewById(R.id.graphVeryHappy)
        val graphH: View = findViewById(R.id.graphHappy)
        val graphN: View = findViewById(R.id.graphNeutral)
        val graphS: View = findViewById(R.id.graphSad)
        val graphVS: View = findViewById(R.id.graphVerySad)
        val graph:ConstraintLayout = findViewById(R.id.graph)

        val guardarButton: Button = findViewById(R.id.guardarButton)
        val veryHappyButton: ImageButton = findViewById(R.id.veryHappyButton)
        val happyButton: ImageButton = findViewById(R.id.happyButton)
        val neutralButton: ImageButton = findViewById(R.id.neutralButton)
        val sadButton: ImageButton = findViewById(R.id.sadButton)
        val verySadButton: ImageButton = findViewById(R.id.verySadButton)

        jsonFile = JSONFile()
        fetchingData()

        if(!data) {
            var emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            graph.background = fondo
            graphVH.background = CustomBarDrawable(this, Emociones ("Muy feliz", 0.0F, R.color.mustard, veryhappy))
            graphH.background = CustomBarDrawable( this, Emociones ( "Feliz",  0.0F, R.color.orange, happy))
            graphN.background = CustomBarDrawable( this, Emociones ( "Neutral", 0.0F, R.color.greenie, neutral))
            graphS.background = CustomBarDrawable(this, Emociones ("Triste", 0.0F, R.color.blue, sad))
            graphVS.background = CustomBarDrawable( this, Emociones ("Muy triste", 0.0F, R.color.deepblue, verysad))
        } else{
            actualizarGrafica()
            iconoMayoria()
        }

        guardarButton.setOnClickListener {
            guardar()
        }
        veryHappyButton.setOnClickListener {
            veryhappy++
            iconoMayoria()
            actualizarGrafica()
        }
        happyButton.setOnClickListener {
            happy++
            iconoMayoria()
            actualizarGrafica()
        }

        neutralButton.setOnClickListener {
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }
        sadButton.setOnClickListener {
            sad++
            iconoMayoria()
            actualizarGrafica()
        }
        verySadButton.setOnClickListener {
            verysad++
            iconoMayoria()
            actualizarGrafica()
        }

    }

    fun fetchingData(){
        try{
            var json: String = jsonFile?.getData(this) ?: ""
            if (json != ""){
                this.data = true
                var jsonArray: JSONArray = JSONArray(json)

                this.lista = parseJson(jsonArray)

                for(i in lista){
                    when(i.nombre){
                        "Muy feliz" -> veryhappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> i.total
                    }
                }

            }else{
                this.data = false
            }
        }catch (exception: JSONException){
            exception.printStackTrace()
        }
    }
    fun parseJson(jsonArray: JSONArray):ArrayList<Emociones>{
        var lista = ArrayList<Emociones>()
        for(i in 0..jsonArray.length()){
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre,porcentaje,color,total)
                lista.add(emocion)
            }catch(exception: JSONException){
                exception.printStackTrace()
            }
        }
        return lista
    }

    fun actualizarGrafica(){
        val total = veryhappy+happy+neutral+verysad+sad
        var pVH:Float = (veryhappy*100 / total).toFloat()
        var pH: Float = (happy * 100 / total).toFloat()
        var pN: Float = (neutral * 100 / total).toFloat()
        var pS: Float = (sad * 100 /  total).toFloat()
        var pVS: Float = (verysad * 100 / total).toFloat()

        Log.d("porcentajes","Very happy"+pVH)
        Log.d("porcentajes"," happy "+pH)
        Log.d("porcentajes","neutral "+pN)
        Log.d("porcentajes","sad "+pS)
        Log.d("porcentajes","very sad "+pVS)

        lista.clear()
        lista.add(Emociones("Muy feliz",pVH,R.color.mustard,veryhappy))
        lista.add(Emociones("Feliz",pH,R.color.orange,happy))
        lista.add(Emociones("Neutral",pN,R.color.greenie,neutral))
        lista.add(Emociones("Triste",pS,R.color.blue,sad))
        lista.add(Emociones("Muy triste",pVS,R.color.deepblue,verysad))

        val fondo = CustomCircleDrawable(this, lista)
        val graphVH:View = findViewById(R.id.graphVeryHappy)
        val graphH: View = findViewById(R.id.graphHappy)
        val graphN: View = findViewById(R.id.graphNeutral)
        val graphS: View = findViewById(R.id.graphSad)
        val graphVS: View = findViewById(R.id.graphVerySad)
        val graph:ConstraintLayout = findViewById(R.id.graph)
        graphVH.background = CustomBarDrawable(this, Emociones ( "Muy feliz", pVH, R.color.mustard, veryhappy))
        graphH.background = CustomBarDrawable(this, Emociones ( "Feliz", pH, R.color.orange, happy))
        graphN.background = CustomBarDrawable(this,Emociones("Neutral", pN, R.color.greenie, neutral))
        graphS.background = CustomBarDrawable(this, Emociones ("Triste", pS, R.color.blue, sad))
        graphVS.background = CustomBarDrawable(this, Emociones ("Muy riste", pVS, R.color.deepblue, verysad))

        graph.background = fondo
    }
    fun iconoMayoria(){
    val icon: ImageView = findViewById(R.id.icon)

    if (happy>veryhappy && happy>neutral && happy>sad && happy>verysad){
        }
        icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        if (veryhappy>happy && veryhappy>neutral && veryhappy>sad && veryhappy>verysad){
        }
        icon.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        if (neutral>veryhappy && neutral >happy && neutral >sad && neutral>verysad){
        }
        icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        if(sad>happy && sad>neutral && sad>veryhappy && sad>verysad){
        }
        icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        if (verysad>happy && verysad>neutral && verysad>sad && veryhappy<verysad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }
    }


    fun guardar(){

    var jsonArray = JSONArray()
    var o: Int = 0
    for (i in lista) {

        Log.d( "objetos", i.toString())
        var j: JSONObject = JSONObject()
        j.put("nombre", i.nombre)
        j.put("porcentaje", i.porcentaje)
        j.put("color", i.color)
        j.put("total", i.total)
        jsonArray.put(o, j)
        o++
    }
    jsonFile?.saveData(this, jsonArray.toString())
    Toast.makeText(this,"Datos guardados", Toast.LENGTH_SHORT).show()
    }
}
