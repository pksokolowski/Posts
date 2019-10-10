package com.github.pksokolowski.posty.screens.posts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pksokolowski.posty.MainViewModel
import com.github.pksokolowski.posty.Navigator
import com.github.pksokolowski.posty.R
import com.github.pksokolowski.posty.di.ViewModelFactory
import com.github.pksokolowski.posty.screens.active.ActivePostFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_posts.*
import javax.inject.Inject

/**
 * This is the main screen with a list of all posts.
 */
class PostsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    var postsAdapter: PostsAdapter =
        PostsAdapter()

    lateinit var navigator: Navigator

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
            .get(MainViewModel::class.java)

        try {
            navigator = context as Navigator
        }catch (e: ClassCastException){
            throw RuntimeException("Activity must implement Navigator interface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = postsAdapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        setupObservers()
        setupInteraction()
        viewModel.refreshPosts()
    }

    private fun setupObservers() {
        viewModel.getPosts().observe(viewLifecycleOwner, Observer {
            postsAdapter.setItems(it)
            recyclerView.scheduleLayoutAnimation()
        })
    }

    private fun setupInteraction() {
        postsAdapter.postSelected = {
            viewModel.setActivePost(it)
            navigator.openFragment(ActivePostFragment())
        }
    }

}