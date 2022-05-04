package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

lateinit var toggle : ActionBarDrawerToggle
private lateinit var editMoradaView: EditText
private lateinit var editNQuartosView: EditText
private lateinit var shared_preferences: SharedPreferences
private lateinit var latitude : EditText
private lateinit var longitude : EditText
private lateinit var imageView: ImageView
private lateinit var button: Button
private val pickImage = 100
private var imageUri: Uri? = null
private var base64:String? =null



override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_inserir_anuncios)

    imageView = findViewById(R.id.preview)
    button = findViewById(R.id.upload)
    editMoradaView = findViewById(R.id.morada)
    editNQuartosView = findViewById(R.id.nquartos)
    latitude = findViewById(R.id.latitude)
    longitude = findViewById(R.id.longitude)
    button.setOnClickListener {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)

    }
    shared_preferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
    val isLogin = shared_preferences.getBoolean("login",false )

    setTitle(R.string.insert)

    val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
    val navView: NavigationView = findViewById(R.id.nav_view)

    toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open,R.string.close)

    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    if(isLogin)
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