package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.OutputAnuncio
import ecgm.com.roomforstudents.api.OutputRegisto
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Registo : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var editUsernameView: EditText
    private lateinit var editEmailView: EditText
    private lateinit var editPasswordView: EditText
    private lateinit var confirmPasswordView: EditText

    private lateinit var shared_preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registo)
        editUsernameView = findViewById(R.id.username)
        editEmailView = findViewById(R.id.email)
        editPasswordView = findViewById(R.id.password)
        confirmPasswordView = findViewById(R.id.confirm_password)


        shared_preferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val isLogin = shared_preferences.getBoolean("login", false)

        setTitle(R.string.register)

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
                R.id.nav_qrcode -> startActivity(Intent(this, QrCodeScanner::class.java).apply{})
                R.id.nav_inserir ->startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
                R.id.nav_anuncios -> startActivity(Intent(this, MeusAnunciosActivity::class.java).apply {})
                R.id.nav_login -> startActivity(Intent(this, Login::class.java).apply {})
                R.id.nav_registo -> startActivity(Intent(this, Registo::class.java).apply{})
                R.id.nav_sair -> logout()
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun logout() {
        val shared_preferences_edit: SharedPreferences.Editor = shared_preferences.edit()
        shared_preferences_edit.clear()
        shared_preferences_edit.apply()

        val intent = Intent(this@Registo, Login::class.java)
        startActivity(intent)
        finish()

    }

    fun registo(view: View) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val username = editUsernameView.text.toString()
        val email = editEmailView.text.toString()
        val password= editPasswordView.text.toString()
        val confirm_password = confirmPasswordView.text.toString()

        if(confirm_password == password){

            val call = request.registar(
                username= username,
                email = email,
                password = password,
            )

            call.enqueue(object : Callback<OutputRegisto> {
                override fun onResponse(call: Call<OutputRegisto>, response: Response<OutputRegisto>){
                    if (response.isSuccessful){
                        val c: OutputRegisto = response.body()!!
                        Toast.makeText(this@Registo, c.MSG, Toast.LENGTH_LONG).show()
                        val intent = Intent(this@Registo, Login::class.java)
                        startActivity(intent);
                        finish()

                    }
                }
                override fun onFailure(call: Call<OutputRegisto>, t: Throwable){
                    Toast.makeText(this@Registo,"${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }else
            Toast.makeText(this@Registo, R.string.diff_password, Toast.LENGTH_LONG).show()


    }
}


