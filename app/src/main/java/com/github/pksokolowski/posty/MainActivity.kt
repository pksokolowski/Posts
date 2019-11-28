package com.github.pksokolowski.posty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) openFragment(MainFragment(), false)
    }

    fun openFragment(frag: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            replace(R.id.container, frag, frag::class.java.simpleName)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }

    /**
     * This is a work-around for the lack of back stack support for nested/child fragments
     * With this in the activity, you have can back-press your way back through
     * child fragments, just like you would with regular ones.
     */
    override fun onBackPressed() {
        val fm: FragmentManager = supportFragmentManager
        for (frag in fm.fragments) {
            if (frag.isVisible) {
                val childFm: FragmentManager = frag.childFragmentManager
                if (childFm.backStackEntryCount > 0) {
                    childFm.popBackStack()
                    return
                }
            }
        }
        super.onBackPressed()
    }
}
