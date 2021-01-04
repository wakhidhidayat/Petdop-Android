package com.wahidhidayat.petdop.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.ui.account.AccountFragment
import com.wahidhidayat.petdop.ui.bookmark.BookmarkFragment
import com.wahidhidayat.petdop.ui.explore.ExploreFragment
import com.wahidhidayat.petdop.ui.home.HomeFragment
import com.wahidhidayat.petdop.ui.upload.UploadFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(HomeFragment())
        bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (item.itemId) {
            R.id.menu_home -> {
                fragment = HomeFragment()
            }
            R.id.menu_account -> {
                fragment = AccountFragment()
            }
            R.id.menu_bookmark -> {
                fragment = BookmarkFragment()
            }
            R.id.menu_explore -> {
                fragment = ExploreFragment()
            }
            R.id.menu_upload -> {
                fragment = UploadFragment()
            }
        }

        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }
}