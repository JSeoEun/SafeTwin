package com.example.safetwin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)

        // 처음 화면
        replaceFragment(DashFragment())

        // 메뉴 클릭 이벤트
        findViewById<TextView>(R.id.menuDash).setOnClickListener {
            replaceFragment(DashFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        findViewById<TextView>(R.id.menuAnalysis).setOnClickListener {
            replaceFragment(AnalysisFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        val menuDash = findViewById<TextView>(R.id.menuDash)
        val menuAnalysis = findViewById<TextView>(R.id.menuAnalysis)
        val menuDigitalTwin = findViewById<TextView>(R.id.menuDigitalTwin)
        val menuAfterCare = findViewById<TextView>(R.id.menuAfterCare)
        val menuLegal = findViewById<TextView>(R.id.menuLegal)

        menuDash.setOnClickListener {
            replaceFragment(DashFragment())
        }

        menuAnalysis.setOnClickListener {
            replaceFragment(AnalysisFragment())
        }

        menuDigitalTwin.setOnClickListener {
            replaceFragment(DigitalTwinFragment())
        }

        menuAfterCare.setOnClickListener {
            replaceFragment(AfterCareFragment())
        }

        menuLegal.setOnClickListener {
            replaceFragment(LegalFragment())
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.drawerLayout, fragment)
            .commit()
    }
}