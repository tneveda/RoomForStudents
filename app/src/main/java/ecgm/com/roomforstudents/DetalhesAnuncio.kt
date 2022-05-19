package ecgm.com.roomforstudents

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import ecgm.com.roomforstudents.api.Anuncio

class DetalhesAnuncio : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var  anuncios: List<Anuncio>
    private lateinit var shared_preferences: SharedPreferences
    private lateinit var moradaView: TextView
    private lateinit var priceView: TextView
    private lateinit var numberView: TextView
    private lateinit var quartosView: TextView
    private lateinit var casaBanhoView: TextView
    private lateinit var movelView: TextView
    private lateinit var contactoView: TextView
    private lateinit var observacoesView: TextView
    private lateinit var qrcodeView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_anuncio)
    }
}