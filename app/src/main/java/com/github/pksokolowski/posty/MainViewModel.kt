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
import com.github.pksokolowski.posty.utils.Status.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
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


    fun refreshPosts() = GlobalScope.launch(Dispatchers.Main) {
        ongoingTasksTracker.startOne()
        var result: List<Post>? = null
        withContext(Dispatchers.IO) {
            try {
                result = service.getPosts()
            } catch (ignored: Exception) {
            }
        }
        ongoingTasksTracker.endOne()

        if (result == null) {
            status.value = ERROR(R.string.status_offline)
            return@launch
        }

        posts.value = result
        status.value = OK
    }

    fun setActivePost(post: Post) = GlobalScope.launch(Dispatchers.Main) {
        detailedActivePost.value = PostDetails(post, null, null)
        ongoingTasksTracker.startOne()
        var author: User? = null
        var comments: List<Comment>? = null
        withContext(Dispatchers.IO) {
            try {
                author = service.getUserById(post.userId)
                comments = service.getComments(post.id)
            } catch (ignored: Exception) {
            }
        }
        ongoingTasksTracker.endOne()

        if (author == null || comments == null) {
            status.value = ERROR(R.string.status_offline)
            return@launch
        }

        detailedActivePost.value = PostDetails(post, author, comments)
        status.value = OK
    }

}