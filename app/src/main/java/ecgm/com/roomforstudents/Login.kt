package ecgm.com.roomforstudents

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import ecgm.com.roomforstudents.api.EndPoints
import ecgm.com.roomforstudents.api.OutputLogin
import ecgm.com.roomforstudents.api.ServiceBuilder
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call

class Login : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var editUsernameView: EditText
    private lateinit var editPasswordView: EditText

    private lateinit var shared_preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editUsernameView = findViewById(R.id.username)
        editPasswordView = findViewById(R.id.password)

        shared_preferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        val isLogin = shared_preferences.getBoolean("login", false)

        setTitle(R.string.login)

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
                R.id.nav_home ->startActivity(Intent(this, ListaAnuncios::class.java).apply{})
                R.id.nav_mapa -> Toast.makeText(applicationContext,"QRcode", Toast.LENGTH_SHORT).show()// startActivity(Intent(this, MapsActivity::class.java).apply{})
                R.id.nav_qrcode -> Toast.makeText(applicationContext,"QRcode", Toast.LENGTH_SHORT).show()
                R.id.nav_inserir -> Toast.makeText(applicationContext,"QRcode", Toast.LENGTH_SHORT).show()//startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
                R.id.nav_anuncios -> Toast.makeText(applicationContext,"QRcode", Toast.LENGTH_SHORT).show()// startActivity(Intent(this, MeusAnunciosActivity::class.java).apply{})
                R.id.nav_login -> startActivity(Intent(this, Login::class.java).apply{})
                R.id.nav_registo -> Toast.makeText(applicationContext,"Registar", Toast.LENGTH_SHORT).show()
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

    fun login(view: View) {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val username = editUsernameView.text.toString()
        val password = editPasswordView.text.toString()

        val call = request.login(username = username, password = password)


        call.enqueue(object : Callback<OutputLogin> {
            override fun onResponse(call: Call<OutputLogin>, response: Response<OutputLogin>){
                if (response.isSuccessful){

                    val c: OutputLogin = response.body()!!

                    if(TextUtils.isEmpty(editUsernameView.text) || TextUtils.isEmpty(editPasswordView.text)) {
                        Toast.makeText(this@Login, R.string.login, Toast.LENGTH_LONG).show()
                    }else{
                        if(c.status =="false"){
                            Toast.makeText(this@Login, c.MSG, Toast.LENGTH_LONG).show()
                        }else{

                            val isLogin = true
                            val shared_preferences_edit : SharedPreferences.Editor = shared_preferences.edit()
                            shared_preferences_edit.putString("username", username)
                            shared_preferences_edit.putString("password", password)
                            shared_preferences_edit.putInt("id", c.id)
                            shared_preferences_edit.putBoolean("login", isLogin)
                            shared_preferences_edit.apply()



                            val intent = Intent(this@Login, ListaAnuncios::class.java)

                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<OutputLogin>, t: Throwable){
                Toast.makeText(this@Login,"${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun logout() {
        val shared_preferences_edit: SharedPreferences.Editor = shared_preferences.edit()
        shared_preferences_edit.clear()
        shared_preferences_edit.apply()

        val intent = Intent(this@Login, Login::class.java)
        startActivity(intent)
        finish()

    }


}