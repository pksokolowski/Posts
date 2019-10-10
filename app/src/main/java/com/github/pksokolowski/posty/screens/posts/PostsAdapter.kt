package com.github.pksokolowski.posty.screens.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pksokolowski.posty.R
import com.github.pksokolowski.posty.api.models.Post
import kotlinx.android.synthetic.main.post_item.view.*

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {
    private var posts: List<Post> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.post_item, parent, false)
        )

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        with(posts[position]) {
            holder.title.text = title
            holder.body.text = body
            holder.itemLayout.setOnClickListener {
                postSelected?.invoke(this)
            }
        }
    }

    fun setItems(posts: List<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.titleTextView
        val body = itemView.bodyTextView
        val itemLayout = itemView.itemLayout
    }

    var postSelected: ((post: Post) -> Unit)? = null
}