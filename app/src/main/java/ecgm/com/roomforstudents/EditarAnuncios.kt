package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import ecgm.com.roomforstudents.api.Anuncio
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.OutputEditar
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class EditarAnuncios : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var editMoradaView: EditText
    private lateinit var editNQuartosView: EditText
    private lateinit var shared_preferences: SharedPreferences
    private lateinit var latitude: EditText
    private lateinit var longitude: EditText
    private lateinit var imageView: ImageView
    private lateinit var casaBanho : EditText
    private lateinit var mobilada : EditText
    private lateinit var contacto : EditText
    private lateinit var preco : EditText
    private lateinit var observacao : EditText
    private lateinit var button: Button
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var base64: String? = null
    private var fotografia: String? = null
    private lateinit var decodedByte: Bitmap
    private var isBitMap: Boolean = false
    private lateinit var anuncios: List<Anuncio>
    private var ID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_anuncios)
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
        val isLogin = shared_preferences.getBoolean("login", false)

        setTitle(R.string.edit)

        var id = intent.getStringExtra(DetalhesAnuncioLogado.PARAM_ID)
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call: Call<List<Anuncio>> = request.getAnunciosById2(id)

    call.enqueue(
    object : Callback<List<Anuncio>> {
        override fun onResponse(call: Call<List<Anuncio>>, response: Response<List<Anuncio>>) {
            if (response.isSuccessful) {
                anuncios = response.body()!!
                for (anuncio in anuncios) {

                    editMoradaView.setText(anuncio.morada)
                    editNQuartosView.setText(anuncio.n_quartos.toString())
                    latitude.setText(anuncio.latitude.toString())
                    longitude.setText(anuncio.longitude.toString())
                    casaBanho.setText(anuncio.ncasas_banho.toString())
                    mobilada.setText(anuncio.mobilado)
                    contacto.setText(anuncio.telemovel)
                    preco.setText(anuncio.preco.toString())
                    observacao.setText(anuncio.outros_atributos)
                    ID = anuncio.id

                    val decodedString: ByteArray = Base64.decode(anuncio.fotografia, Base64.DEFAULT)
                    decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)


                    imageView.setImageBitmap(decodedByte)
                    isBitMap = true

                }

            }
        }

        override fun onFailure(call: Call<List<Anuncio>>, t: Throwable) {
            Toast.makeText(this@EditarAnuncios, "${t.message}", Toast.LENGTH_LONG).show()
        }
    })

}

// Passa o Uri da imagem para bitmap e posteriomente para base64
// guarda a imagem em formato base64 (numa string) de modo a ser inserido na BD
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK && requestCode == pickImage) {
        imageUri = data?.data
        // imageView.setImageURI(imageUri)
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        val bitmapReSize = resizeBitmap(bitmap, 1100)

        // If bitmap is not null
        bitmap?.let {
            imageView.setImageBitmap(bitmapReSize)

        }

        val base = bitmapReSize?.let { it1 -> getBase64String(it1) }

        base64 = base.toString()
        isBitMap = false


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

fun editar(view: View) {

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


    if (isBitMap) {
        val base = getBase64String(decodedByte)
        fotografia = base
    } else {
        fotografia = base64
    }

    val call = request.editar(
        id = ID,
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

    call.enqueue(object : Callback<OutputEditar> {
        override fun onResponse(call: Call<OutputEditar>, response: Response<OutputEditar>) {
            if (response.isSuccessful) {
                val c: OutputEditar = response.body()!!
                Toast.makeText(this@EditarAnuncios, c.MSG, Toast.LENGTH_LONG).show()
                val intent = Intent(this@EditarAnuncios, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        override fun onFailure(call: Call<OutputEditar>, t: Throwable) {
            Toast.makeText(this@EditarAnuncios, "${t.message}", Toast.LENGTH_SHORT).show()
        }
    })


}


fun inserir(view: View) {}
}