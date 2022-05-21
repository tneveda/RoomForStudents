package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import ecgm.com.roomforstudents.ListaAnuncios.Companion.PARAM_ID
import ecgm.com.roomforstudents.api.Anuncio
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var  anuncios: List<Anuncio>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedpreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val isLogin = sharedpreferences.getBoolean("login",false )

        setTitle(R.string.Map)


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
                R.id.nav_home -> startActivity(Intent(this, ListaAnuncios::class.java).apply{})
                R.id.nav_mapa -> startActivity(Intent(this, MapsActivity::class.java).apply{})
                R.id.nav_qrcode -> Toast.makeText(applicationContext,"QRcode", Toast.LENGTH_SHORT).show()
                R.id.nav_inserir -> Toast.makeText(applicationContext,"Inserir", Toast.LENGTH_SHORT).show() //startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
                R.id.nav_login ->  Toast.makeText(applicationContext,"login", Toast.LENGTH_SHORT).show()//startActivity(Intent(this, Login::class.java).apply{})
                R.id.nav_registo -> Toast.makeText(applicationContext,"Registar", Toast.LENGTH_SHORT).show()
                R.id.nav_sair -> logout()
            }
            true
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val viana = LatLng(41.6943, -8.8469)
        val zoomLevel = 15f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viana,zoomLevel))

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnuncios()
        var position: LatLng
        val utilizador_id = sharedpreferences.getInt("id", 0)

        call.enqueue(object : Callback<List<Anuncio>> {
            override fun onResponse(call: Call<List<Anuncio>>, response: Response<List<Anuncio>>){
                if (response.isSuccessful){
                    anuncios = response.body()!!
                    for (anuncio in anuncios){
                        position = LatLng(anuncio.latitude.toDouble(), anuncio.longitude.toDouble())
                        mMap.addMarker(MarkerOptions().position(position).title(anuncio.id.toString() ).snippet(anuncio.preco.toString() + " " + getText(R.string.money) +"-" + anuncio.n_quartos.toString() +" "+ getText(R.string.Rooms)+ "-" + anuncio.morada))
                    }
                }
            }
            override fun onFailure(call: Call<List<Anuncio>>, t: Throwable){
                Toast.makeText(this@MapsActivity,"${t.message}", Toast.LENGTH_LONG).show()
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
        val shared_preferences_edit : SharedPreferences.Editor = sharedpreferences.edit()
        shared_preferences_edit.clear()
        shared_preferences_edit.apply()

        val intent = Intent(this@MapsActivity, Login::class.java)
        startActivity(intent)
        finish()

    }

    override fun onInfoWindowClick(p0: Marker) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call :Call <List<Anuncio>> = request.getAnunciosById2(p0!!.title)
        val utilizador_id = sharedpreferences.getInt("id", 0)

        call.enqueue(object : Callback<List<Anuncio>> {
            override fun onResponse(call: Call<List<Anuncio>>, response: Response<List<Anuncio>>) {
                if (response.isSuccessful) {
                    anuncios = response.body()!!
                    for (anuncio in anuncios) {
                        val intent = Intent(this@MapsActivity, ListaAnuncios::class.java)
                        intent.putExtra(PARAM_ID, anuncio.id.toString())
                        intent.putExtra(PARAM_MORADA, anuncio.morada)
                        startActivity(intent)
                    }

                }
            }
            override fun onFailure(call: Call<List<Anuncio>>, t: Throwable) {
                Toast.makeText(this@MapsActivity,"${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    companion object {
        const val STATUS = ""
        const val DELETE_ID = "DELETE_ID"
        const val PARAM_ID = "PARAM_ID"
        const val PARAM_MORADA = "PARAM_MORADA"
        const val PARAM_PRECO = "PARAM_PRECO"
        const val PARAM_NUMERO = "PARAM_NUMERO"

    }
}
