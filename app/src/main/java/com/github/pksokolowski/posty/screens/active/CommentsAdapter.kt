package com.github.pksokolowski.posty.screens.active

import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.pksokolowski.posty.R
import com.github.pksokolowski.posty.api.models.Comment
import com.github.pksokolowski.posty.model.PostDetails
import kotlinx.android.synthetic.main.active_post_header.view.*
import kotlinx.android.synthetic.main.comment_item.view.authorTextView
import kotlinx.android.synthetic.main.comment_item.view.bodyTextView

class CommentsAdapter(private var postData: PostDetails) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var comments: List<Comment> = listOf()

    init {
        setPostData(postData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == HEADER) {
            val headerHolder = HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.active_post_header, parent, false)
            )
            headerHolder.author.movementMethod = LinkMovementMethod.getInstance()
            headerHolder
        } else CommentsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_item, parent, false)
        )

    override fun getItemCount() = comments.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) HEADER else COMMENT

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is CommentsViewHolder -> {
                with(comments[position - 1]) {
                    holder.body.text = body
                    holder.author.text = name
                }
            }
            is HeaderViewHolder -> {
                holder.title.text = postData.post.title
                holder.body.text = postData.post.body
                postData.author?.let { user ->
                    holder.author.text = Html.fromHtml(
                        "<a href=\"mailto:${user.email}\">${user.name}</a>",
                        Html.FROM_HTML_MODE_COMPACT
                    )
                }
            }
        }

    }

    fun setPostData(data: PostDetails) {
        postData = data
        data.comments?.let { this.comments = it }

        notifyDataSetChanged()
    }

    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val body = itemView.bodyTextView
        val author = itemView.authorTextView
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.titleTextView
        val author = itemView.authorTextView
        val body = itemView.bodyTextView
    }

    companion object {
        const val HEADER = 0
        const val COMMENT = 1
    }
}