package ecgm.com.roomforstudents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import ecgm.com.roomforstudents.api.Anuncio
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.OutputApagar
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalhesAnuncioLogado : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var  anuncios: List<Anuncio>
    private lateinit var shared_preferences: SharedPreferences
    private lateinit var moradaView: TextView
    private lateinit var priceView: TextView
    private lateinit var numberView: TextView
    private lateinit var quartosView: TextView
    private lateinit var casaBanhoView: TextView
    private lateinit var movelView: TextView
    private lateinit var observacoesView: TextView
    private lateinit var qrcodeView: ImageView
    private lateinit var fotografiaView: ImageView
    private var ID: Int? = 0
    private var QRCodeVisible: Boolean= false
    private val newActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_anuncio_logado)

        moradaView = findViewById(R.id.localizacao)
        priceView = findViewById(R.id.preco)
        numberView = findViewById(R.id.contacto)
        qrcodeView = findViewById(R.id.qrcodeImage)
        quartosView = findViewById(R.id.quartos)
        casaBanhoView = findViewById(R.id.casaBanho)
        observacoesView = findViewById(R.id.observacao)
        movelView = findViewById(R.id.movel)
        fotografiaView = findViewById(R.id.imagemDetalhe)

        shared_preferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val isLogin = shared_preferences.getBoolean("login", false)



        setTitle(R.string.Details)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        if (isLogin) {
            navView.menu.clear();
            navView.inflateMenu(R.menu.nav_menu);
        } else {
            navView.menu.clear();
            navView.inflateMenu(R.menu.nav_menu_not_logged);
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this, ListaAnuncios::class.java).apply {})
                R.id.nav_mapa -> startActivity(Intent(this, MapsActivity::class.java).apply {})
                R.id.nav_qrcode -> Toast.makeText(applicationContext, "QRcode", Toast.LENGTH_SHORT)
                    .show()
                R.id.nav_inserir -> Toast.makeText(applicationContext, "QRcode", Toast.LENGTH_SHORT)
                    .show() //startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
                R.id.nav_anuncios -> startActivity(Intent(this, MeusAnunciosActivity::class.java).apply {})
                R.id.nav_login -> startActivity(Intent(this, Login::class.java).apply {})
                R.id.nav_registo -> Toast.makeText(
                    applicationContext,
                    "Registar",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_sair -> logout()
            }
            true
        }

        var id = intent.getStringExtra(PARAM_ID)
       val request = ServiceBuilder.buildService(EndPoints::class.java)
       val call : Call<List<Anuncio>> = request.getAnunciosById2(id)

       call.enqueue(object : Callback<List<Anuncio>> {
            override fun onResponse(call: Call<List<Anuncio>>, response: Response<List<Anuncio>>) {
                if (response.isSuccessful) {
                    anuncios = response.body()!!
                    for (anuncio in anuncios) {

                        ID = anuncio.id

                        moradaView.setText(" " +anuncio.morada)
                        priceView.setText(" " +anuncio.preco.toString()+ " " + getText(R.string.money))
                        numberView.setText(" " +anuncio.telemovel)
                        quartosView.setText(" " +anuncio.n_quartos.toString()+ " " +  getText(R.string.Rooms))
                        casaBanhoView.setText(" " +anuncio.ncasas_banho.toString()+ " " + getText(R.string.bathroom))
                        observacoesView.setText(" " +anuncio.outros_atributos)
                        movelView.setText(" " +anuncio.mobilado)

                        val decodedPicture: ByteArray = Base64.decode(anuncio.fotografia, Base64.DEFAULT)
                        val PictureByte = BitmapFactory.decodeByteArray(decodedPicture, 0, decodedPicture.size)
                        fotografiaView.setImageBitmap(PictureByte)



                    }

                }
            }
            override fun onFailure(call: Call<List<Anuncio>>, t: Throwable) {
                Toast.makeText(this@DetalhesAnuncioLogado,"${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun logout(){
        val shared_preferences_edit : SharedPreferences.Editor = shared_preferences.edit()
        shared_preferences_edit.clear()
        shared_preferences_edit.apply()

        val intent = Intent(this@DetalhesAnuncioLogado, Login::class.java)
        startActivity(intent)
        finish()

    }


    companion object {
        const val PARAM_ID = "PARAM_ID"
    }

    fun generateQrCode(view: View) {
        if(QRCodeVisible == false){

            val content = ID.toString()

            val encoder = BarcodeEncoder()
            val bitmap = encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 512, 512)

            QRCodeVisible = true

            qrcodeView.setImageBitmap(bitmap)

        }
        else
            Toast.makeText(this@DetalhesAnuncioLogado,R.string.alreadyQrcode, Toast.LENGTH_SHORT).show()
    }

    fun edit(view: View) {
        var id = intent.getStringExtra(PARAM_ID)

        val intent = Intent(this@DetalhesAnuncioLogado,EditarAnuncios::class.java)
        intent.putExtra(PARAM_ID,  id.toString())
        startActivityForResult(intent, newActivityRequestCode)
    }

    fun delete(view: View) {
        var id = intent.getStringExtra("PARAM_ID")
        var idString = id.toString()
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.apagarAnuncio(id = idString.toInt())

        val x = 1
        call.enqueue(object : Callback<OutputApagar> {
            override fun onResponse(call: Call<OutputApagar>, response: Response<OutputApagar>) {
                if (response.isSuccessful) {
                    val c: OutputApagar = response.body()!!
                    Toast.makeText(this@DetalhesAnuncioLogado, c.MSG, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@DetalhesAnuncioLogado, MapsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<OutputApagar>, t: Throwable) {
                Toast.makeText(this@DetalhesAnuncioLogado, "${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }



}