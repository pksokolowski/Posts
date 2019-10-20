package com.github.pksokolowski.posty

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.pksokolowski.posty.di.ViewModelFactory
import com.github.pksokolowski.posty.screens.posts.PostsFragment
import com.github.pksokolowski.posty.utils.Status.*
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Navigator {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        setupObservers()

        if (savedInstanceState == null) openFragment(PostsFragment(), false)
    }

    private fun setupObservers() {
        viewModel.isDownloadInProgress().observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        viewModel.getStatus().observe(this, Observer {
            when (it) {
                is OK -> {
                }
                is ERROR -> {
                    Toast.makeText(this, it.messageResId, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun openFragment(frag: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            replace(R.id.container, frag, frag::class.java.simpleName)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }

}
