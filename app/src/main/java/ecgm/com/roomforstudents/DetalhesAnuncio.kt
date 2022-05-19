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
    private lateinit var fotografiaView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_anuncio)
        moradaView = findViewById(R.id.localizacao)
        priceView = findViewById(R.id.price)
        numberView = findViewById(R.id.contacto)
        qrcodeView = findViewById(R.id.qrcodeImage)
        quartosView = findViewById(R.id.quartos)
        casaBanhoView = findViewById(R.id.casaBanho)
        contactoView = findViewById(R.id.contacto)
        observacoesView = findViewById(R.id.observacao)
        movelView = findViewById(R.id.movel)
        qrcodeView = findViewById(R.id.qrcodeImage)
        fotografiaView = findViewById(R.id.imagemDetalhe)





    }
}