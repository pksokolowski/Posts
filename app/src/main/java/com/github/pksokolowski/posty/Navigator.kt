package com.github.pksokolowski.posty

import androidx.fragment.app.Fragment

interface Navigator{
    fun openFragment(frag: Fragment, addToBackStack: Boolean = true)
}