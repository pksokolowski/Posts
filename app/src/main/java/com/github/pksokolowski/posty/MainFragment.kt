package com.github.pksokolowski.posty

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.pksokolowski.posty.di.ViewModelFactory
import com.github.pksokolowski.posty.screens.posts.PostsFragment
import com.github.pksokolowski.posty.utils.Status
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainFragment : Fragment(), Navigator{

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()

        if (savedInstanceState == null) openFragment(PostsFragment(), false)
    }

    private fun setupObservers() {
        viewModel.isDownloadInProgress().observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        viewModel.getStatus().observe(this, Observer {
            when (it) {
                is Status.OK -> {
                }
                is Status.ERROR -> {
                    Toast.makeText(activity, it.messageResId, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun openFragment(frag: Fragment, addToBackStack: Boolean) {
        childFragmentManager.beginTransaction().apply {
            setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            replace(R.id.container, frag, frag::class.java.simpleName)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }
}