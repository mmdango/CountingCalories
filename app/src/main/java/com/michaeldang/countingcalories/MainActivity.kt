package com.michaeldang.countingcalories

import android.os.Bundle
import android.view.Menu
import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.michaeldang.countingcalories.feat.dashboard.CaloriesDashboardFragment
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesFragment
import com.michaeldang.countingcalories.feat.measurements.MeasurementsFragment
import com.michaeldang.countingcalories.feat.settings.CaloriesSettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    CaloriesEntriesFragment()
                ).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    CaloriesDashboardFragment()
                ).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_measurements -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    MeasurementsFragment()
                ).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.selectedItemId = R.id.navigation_home
        frameLayout = findViewById(R.id.frame_layout)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}
