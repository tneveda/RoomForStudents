package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import ecgm.com.roomforstudents.adapter.AnuncioAdapter
import ecgm.com.roomforstudents.api.Anuncio
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeusAnunciosActivity : AppCompatActivity() /*, CellClickListener*/ {


    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var shared_preferences: SharedPreferences
    private val newAnuncioActivityRequestCode1 = 2
    lateinit var anuncioAdapter: AnuncioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_anuncios)

        shared_preferences = getSharedPreferences ("shared_preferences", Context.MODE_PRIVATE)

        setTitle(R.string.myrooms)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        anuncioAdapter = AnuncioAdapter(this/*, this */)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = anuncioAdapter
        val isLogin = shared_preferences.getBoolean("login", false)
        val user_id = shared_preferences.getInt("id", 0)

        // Toast.makeText(applicationContext,teste, Toast.LENGTH_SHORT).show()


        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAnunciosByUser(user_id)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (isLogin) {
            navView.menu.clear();
            navView.inflateMenu(R.menu.nav_menu);
        } else {
            navView.menu.clear();
            navView.inflateMenu(R.menu.nav_menu_not_logged);
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this, ListaAnuncios::class.java).apply {})
                R.id.nav_mapa -> startActivity(Intent(this, MapsActivity::class.java).apply {})
                R.id.nav_qrcode -> Toast.makeText(applicationContext, "QRcode", Toast.LENGTH_SHORT)
                    .show()
                R.id.nav_inserir -> Toast.makeText(applicationContext, "QRcode", Toast.LENGTH_SHORT)
                    .show()//startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
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

        call.enqueue(object : Callback<List<Anuncio>> {
            override fun onResponse(call: Call<List<Anuncio>>, response: Response<List<Anuncio>>) {
                if (response.isSuccessful) {

                    anuncioAdapter.Anuncios(response.body()!!);

                }
            }

            override fun onFailure(call: Call<List<Anuncio>>, t: Throwable) {
                Toast.makeText(this@MeusAnunciosActivity, "${t.message}", Toast.LENGTH_LONG).show()
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

        val intent = Intent(this@MeusAnunciosActivity, Login::class.java)
        startActivity(intent)
        finish()

    }

   /* override fun onCellClickListener(data: Anuncio) {
        val intent = Intent(this@MeusAnunciosActivity, DetalhesLogaAnuncio::class.java)
        intent.putExtra(PARAM_ID, data.id.toString())
        // intent.putExtra(PARAM_MORADA, data.morada)
        //  intent.putExtra(PARAM_TELEMOVEL, data.telemovel)
        startActivityForResult(intent, newAnuncioActivityRequestCode1)
        Log.e("***ID", data.id.toString())
    }*/
    companion object {

        const val PARAM_ID = "PARAM_ID"

    }



}