package ecgm.com.roomforstudents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.navigation.NavigationView
import ecgm.com.roomforstudents.databinding.ActivityQrCodeScannerBinding
import java.io.IOException

class QrCodeScanner : AppCompatActivity() {


    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var sharedpreferences: SharedPreferences
    private var scannedValue = ""
    private lateinit var binding: ActivityQrCodeScannerBinding
    private val QRCodeRequestCode1 = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedpreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)

        val isLogin = sharedpreferences.getBoolean("login",false )

        setTitle(R.string.qrcode)


        if (ContextCompat.checkSelfPermission(
                this@QrCodeScanner, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this@QrCodeScanner, R.anim.scanning_animation)
        binding.barcodeLine.startAnimation(aniSlide)

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
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this, ListaAnuncios::class.java).apply {})
                R.id.nav_mapa -> startActivity(Intent(this, MapsActivity::class.java).apply {})
                R.id.nav_qrcode -> startActivity(Intent(this, QrCodeScanner::class.java).apply {})
                R.id.nav_inserir -> startActivity(Intent(this, InserirAnunciosActivity::class.java).apply{})
                R.id.nav_anuncios -> startActivity(Intent(this, MeusAnunciosActivity::class.java).apply {})
                R.id.nav_login -> startActivity(Intent(this, Login::class.java).apply {})
                R.id.nav_registo -> startActivity(Intent(this, Registo::class.java).apply {})
                R.id.nav_sair -> logout()
            }
            true
        }



    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }
       // No caso de ser detetado um QrCode e caso o seu valor associado por um inteiro, esse valor é
            // guardado em PARAM_ID e muda a actividade para Detalhes Anuncio
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue


                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {

                        if(isNumber(scannedValue)) {
                            cameraSource.stop()
                            val intent = Intent(this@QrCodeScanner, DetalhesAnuncio::class.java)
                            intent.putExtra(PARAM_ID, scannedValue)
                            startActivityForResult(intent, QRCodeRequestCode1)
                        }
                        //Toast.makeText(this@QrCodeScanner, "value- $scannedValue", Toast.LENGTH_SHORT).show()

                    }
                }else
                {
                    Toast.makeText(this@QrCodeScanner, "value- else", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    // Pede permissão para o tlm usar a camera
    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@QrCodeScanner,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    // No caso de haver permissão de uso de camera é despoletada a função setupControls, caso
    //contrário é disparado no ecrã um avisão que a permissão foi recusada
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }

// Verifica se valor da String é um numero
    fun isNumber(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }

    companion object {

        const val PARAM_ID = "PARAM_ID"
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

        val intent = Intent(this@QrCodeScanner, Login::class.java)
        startActivity(intent)
        finish()

    }

}