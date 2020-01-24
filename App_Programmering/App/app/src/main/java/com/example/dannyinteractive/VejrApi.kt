package com.example.dannyinteractive

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.vejr_content.*

class VejrApi : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val darktheme = sharedPreferences.getBoolean("dark_theme", false)

        val profilePicture = sharedPreferences.getString("profile_pic", "")
        val profileName = sharedPreferences.getString("profile_name", "")

        if(darktheme)
            setTheme(R.style.Dark_AppTheme)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vejr_api)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val hView: View = navView.getHeaderView(0)

        if(profilePicture != null && profilePicture != "")
            hView.findViewById<ImageView>(R.id.profile_profileImg).setImageBitmap(BitmapFactory.decodeFile(profilePicture))


        if(profileName != "")
            hView.findViewById<TextView>(R.id.profileNameTxt).text = profileName
        else
            hView.findViewById<TextView>(R.id.profileNameTxt).text = "Danny Interactive"


        val queue = Volley.newRequestQueue(this)
        val vejrUrl = "https://vejr.eu/api.php?location=Silkeborg&degree=C"

        getWeather(queue, vejrUrl)
    }


    @SuppressLint("SetTextI18n")
    fun getWeather(queue: RequestQueue, url: String) {

        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                val parser: Parser = Parser.default()
                val stringBuilder: StringBuilder = StringBuilder(response)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                val weatherData: JsonObject? = json.obj("CurrentData") as JsonObject

                VejrtextView.text = "Tempture: " + weatherData?.get("temperature") + "°C\n " + weatherData?.get("skyText") + "\n humidity: " + weatherData?.get("humidity") + "\n Wind speed: " + weatherData?.get("windText")

            },
            Response.ErrorListener { println("That didn't work!") })

        queue.add(stringRequest)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, Profile::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
