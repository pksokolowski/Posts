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
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.active_post_header.*

class CommentsAdapter(private var postData: PostDetails) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var comments: List<Comment> = listOf()

    init {
        setPostData(postData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == HEADER) {
            val headerHolder = HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.active_post_header, parent, false)
            )
            headerHolder.authorTextView.movementMethod = LinkMovementMethod.getInstance()
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
                    holder.bodyTextView.text = body
                    holder.authorTextView.text = name
                }
            }
            is HeaderViewHolder -> {
                holder.titleTextView.text = postData.post.title
                holder.bodyTextView.text = postData.post.body
                postData.author?.let { user ->
                    holder.authorTextView.text = Html.fromHtml(
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

    class CommentsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    class HeaderViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer

    companion object {
        const val HEADER = 0
        const val COMMENT = 1
    }
}