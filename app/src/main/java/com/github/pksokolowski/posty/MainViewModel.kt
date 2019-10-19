package com.github.pksokolowski.posty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.pksokolowski.posty.api.JsonPlaceholderService
import com.github.pksokolowski.posty.api.models.Comment
import com.github.pksokolowski.posty.api.models.Post
import com.github.pksokolowski.posty.api.models.User
import com.github.pksokolowski.posty.model.PostDetails
import com.github.pksokolowski.posty.utils.OngoingTasksTracker
import com.github.pksokolowski.posty.utils.Status
import com.github.pksokolowski.posty.utils.Status.ERROR
import com.github.pksokolowski.posty.utils.Status.OK
import com.github.pksokolowski.posty.utils.runRequest
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val service: JsonPlaceholderService,
    private val ongoingTasksTracker: OngoingTasksTracker
) : ViewModel() {

    private val posts = MutableLiveData<List<Post>>().apply { value = listOf() }
    fun getPosts() = posts as LiveData<List<Post>>

    private val detailedActivePost = MutableLiveData<PostDetails?>().apply { value = null }
    fun getActivePost() = detailedActivePost as LiveData<PostDetails?>

    fun isDownloadInProgress() = ongoingTasksTracker.areThereOngoingTasks()

    private val status = MutableLiveData<Status>()
    fun getStatus() = status as LiveData<Status>


    fun refreshPosts() {
        ongoingTasksTracker.startOne()
        suspend {
            service.getPosts()
        }.runRequest({
            ongoingTasksTracker.endOne()
            status.value = ERROR(R.string.status_offline)
        }) {
            ongoingTasksTracker.endOne()
            posts.value = it
            status.value = OK
        }
    }

    private class AuthorAndCommentsResponse(val author: User, val comments: List<Comment>)

    fun setActivePost(post: Post) {
        detailedActivePost.value = PostDetails(post, null, null)
        ongoingTasksTracker.startOne()
        suspend {
            val author = service.getUserById(post.userId)
            val comments = service.getComments(post.id)
            AuthorAndCommentsResponse(author, comments)
        }.runRequest({
            ongoingTasksTracker.endOne()
            status.value = ERROR(R.string.status_offline)
        }) {
            ongoingTasksTracker.endOne()
            detailedActivePost.value = PostDetails(post, it.author, it.comments)
            status.value = OK
        }
    }

}