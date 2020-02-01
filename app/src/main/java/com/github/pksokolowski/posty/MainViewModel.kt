package com.github.pksokolowski.posty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pksokolowski.posty.api.JsonPlaceholderService
import com.github.pksokolowski.posty.api.models.Comment
import com.github.pksokolowski.posty.api.models.Post
import com.github.pksokolowski.posty.api.models.User
import com.github.pksokolowski.posty.model.PostDetails
import com.github.pksokolowski.posty.utils.RequestRunner
import com.github.pksokolowski.posty.utils.Status
import com.github.pksokolowski.posty.utils.Status.ERROR
import com.github.pksokolowski.posty.utils.Status.OK
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val service: JsonPlaceholderService,
    private val requestRunner: RequestRunner
) : ViewModel() {

    private val posts = MutableLiveData<List<Post>>().apply { value = listOf() }
    fun getPosts() = posts as LiveData<List<Post>>

    private val detailedActivePost = MutableLiveData<PostDetails?>().apply { value = null }
    fun getActivePost() = detailedActivePost as LiveData<PostDetails?>

    fun isDownloadInProgress() = requestRunner.areThereAnyOngoingRequests()

    private val status = MutableLiveData<Status>()
    fun getStatus() = status as LiveData<Status>


    fun refreshPosts() = requestRunner.run(
        { service.getPosts() },
        {
            status.value = ERROR(R.string.status_offline)
        },
        {
            posts.value = it
            status.value = OK
        },
        scope = viewModelScope
    )

    private suspend fun getAuthorAndComments(post: Post) = AuthorAndCommentsResponse(
        service.getUserById(post.userId),
        service.getComments(post.id)
    )

    private class AuthorAndCommentsResponse(val author: User, val comments: List<Comment>)

    fun setActivePost(post: Post) {
        detailedActivePost.value = PostDetails(post, null, null)

        requestRunner.run(
            { getAuthorAndComments(post) },
            {
                status.value = ERROR(R.string.status_offline)
            },
            {
                detailedActivePost.value = PostDetails(post, it.author, it.comments)
                status.value = OK
            }
        )
    }

}