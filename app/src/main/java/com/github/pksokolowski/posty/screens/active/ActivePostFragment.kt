package com.github.pksokolowski.posty.screens.active

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pksokolowski.posty.MainViewModel
import com.github.pksokolowski.posty.R
import com.github.pksokolowski.posty.di.ViewModelFactory
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_active_post.*
import javax.inject.Inject

class ActivePostFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    var commentsAdapter: CommentsAdapter? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
            .get(MainViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentsRecyclerView.layoutManager = LinearLayoutManager(activity)

        setupObservers()
        setupInteraction()
    }

    private fun setupObservers() {
        viewModel.getActivePost().observe(viewLifecycleOwner, Observer {
            it?.let {
                if (commentsAdapter == null) {
                    commentsAdapter = CommentsAdapter(it)
                    commentsRecyclerView.adapter = commentsAdapter
                } else
                    commentsAdapter?.setPostData(it)
            }
        })
    }

    private fun setupInteraction() {

    }
}