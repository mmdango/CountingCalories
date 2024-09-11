package com.michaeldang.countingcalories

import android.os.Bundle
import android.view.Menu
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.michaeldang.countingcalories.feat.dashboard.CaloriesDashboardFragment
import com.michaeldang.countingcalories.feat.entries.CaloriesEntriesFragment
import com.michaeldang.countingcalories.feat.measurements.MeasurementsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var frameLayout: FrameLayout
    private val onNavigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    CaloriesEntriesFragment()
                ).commit()
                return@OnItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    CaloriesDashboardFragment()
                ).commit()
                return@OnItemSelectedListener true
            }
            R.id.navigation_measurements -> {
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,
                    MeasurementsFragment()
                ).commit()
                return@OnItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: NavigationBarView = findViewById(R.id.nav_view)

        navView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        navView.selectedItemId = R.id.navigation_home
        frameLayout = findViewById(R.id.frame_layout)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}
