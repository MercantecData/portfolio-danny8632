package com.example.dannyinteractive

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {


        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val darktheme = sharedPreferences.getBoolean("dark_theme", false)

        val profilePicture = sharedPreferences.getString("profile_pic", "")

        if(darktheme)
            setTheme(R.style.Dark_AppTheme)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val hView: View = navView.getHeaderView(0)
        val nav_image = hView.findViewById<ImageView>(R.id.profile_profileImg)

        if(profilePicture != null && profilePicture != "")
        {
            currentPhotoPath = profilePicture
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            nav_image.setImageBitmap(bitmap)
        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
