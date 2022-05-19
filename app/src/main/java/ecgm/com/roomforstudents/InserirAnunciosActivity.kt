package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.OutputAnuncio
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class InserirAnunciosActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var editMoradaView: EditText
    private lateinit var editNQuartosView: EditText
    private lateinit var shared_preferences: SharedPreferences
    private lateinit var latitude : EditText
    private lateinit var longitude : EditText
    private lateinit var casaBanho : EditText
    private lateinit var mobilada : EditText
    private lateinit var contacto : EditText
    private lateinit var preco : EditText
    private lateinit var observacao : EditText
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var base64:String? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inserir_anuncios)

        imageView = findViewById(R.id.preview)
        button = findViewById(R.id.inserirFoto)
        editMoradaView = findViewById(R.id.morada)
        editNQuartosView = findViewById(R.id.quartos)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        casaBanho = findViewById(R.id.casaBanho)
        mobilada = findViewById(R.id.mobilada)
        contacto = findViewById(R.id.contacto)
        preco = findViewById(R.id.preco)
        observacao = findViewById(R.id.observacao)
        button.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)

        }

        shared_preferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val isLogin = shared_preferences.getBoolean("login",false )

        setTitle(R.string.insert)

       // val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
      //  val navView: NavigationView = findViewById(R.id.nav_view)

    //    toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close)

    //    drawerLayout.addDrawerListener(toggle)
   //     toggle.syncState()

    /*    if(isLogin)
        {
            navView.menu.clear();
            navView.inflateMenu(R.menu.nav_menu);
        } else
        {
            navView.menu.clear();
            navView.inflateMenu(R.menu.nav_menu_not_logged);
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home ->startActivity(Intent(this, ListaAnuncios::class.java).apply{})
                R.id.nav_mapa -> startActivity(Intent(this, MapsActivity::class.java).apply{})
                R.id.nav_qrcode -> Toast.makeText(applicationContext,"QRcode", Toast.LENGTH_SHORT).show()
                R.id.nav_inserir -> startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
                R.id.nav_anuncios -> startActivity(Intent(this, MeusAnunciosActivity::class.java).apply{})
                R.id.nav_login -> startActivity(Intent(this, LoginActivity::class.java).apply{})
                R.id.nav_registo -> Toast.makeText(applicationContext,"Registar", Toast.LENGTH_SHORT).show()
                R.id.nav_sair -> logout()
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Método para fazer logout
    // Apaga os dados gravados no shared preferences
    fun logout(){
        val shared_preferences_edit : SharedPreferences.Editor = shared_preferences.edit()
        shared_preferences_edit.clear()
        shared_preferences_edit.apply()

        val intent = Intent(this@InserirAnunciosActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()

   */ }

    // Passa o Uri da imagem para bitmap e posteriomente para base64
    // guarda a imagem em formato base64 (numa string) de modo a ser inserido na BD
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            // imageView.setImageURI(imageUri)
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            val bitmapReSize = resizeBitmap(bitmap,1100)

            // If bitmap is not null
            bitmap?.let {
                imageView.setImageBitmap(bitmapReSize)

            }

            val base= bitmapReSize?.let { it1 -> getBase64String(it1) }

            base64 = base.toString()


        }
    }

    // Reduz o tamanho do tamanho do bitmap
    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap? {
        return try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                if (result != source) {
                }
                result
            } else {
                if (source.width <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                if (result != source) {
                }
                result
            }
        } catch (e: Exception) {
            source
        }
    }

    // faz a conversão da imagem de bitmap para base64
     private fun getBase64String(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    // método para inserir todos os dados na Base de dados
    fun inserir(view: View) {

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val latitude = latitude.text.toString().toDouble()
        val longitude = longitude.text.toString().toDouble()
        val morada= editMoradaView.text.toString()
        val n_quartos = editNQuartosView.text.toString().toInt()
        val casaBanho = casaBanho.text.toString().toInt()
        val contacto = contacto.text.toString()
        val mobilada = mobilada.text.toString()
        val preco = preco.text.toString().toDouble()
        val observacao = observacao.text.toString()


        val utilizador_id = shared_preferences.getInt("id", 0)
        val fotografia =  base64


        val call = request.anunciar(
            users_id = utilizador_id,
            morada = morada,
            n_quartos = n_quartos,
            latitude = latitude.toString().toDouble(),
            longitude = longitude.toString().toDouble(),
            fotografia = fotografia,
            preco = preco,
            ncasas_banho = casaBanho,
            telemovel= contacto,
            mobilado = mobilada,
            outros_atributos = observacao
        )

        call.enqueue(object : Callback<OutputAnuncio> {
            override fun onResponse(call: Call<OutputAnuncio>, response: Response<OutputAnuncio>){
                if (response.isSuccessful){
                    val c: OutputAnuncio = response.body()!!
                    Toast.makeText(this@InserirAnunciosActivity, c.MSG, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@InserirAnunciosActivity, MapsActivity::class.java)
                    startActivity(intent);
                    finish()

                }
            }
            override fun onFailure(call: Call<OutputAnuncio>, t: Throwable){
                Toast.makeText(this@InserirAnunciosActivity,"${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }


}