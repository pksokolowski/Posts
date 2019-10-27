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
import com.github.pksokolowski.posty.utils.RequestRunner
import com.github.pksokolowski.posty.utils.Status
import com.github.pksokolowski.posty.utils.Status.ERROR
import com.github.pksokolowski.posty.utils.Status.OK
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val service: JsonPlaceholderService,
    private val ongoingTasksTracker: OngoingTasksTracker
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val posts = MutableLiveData<List<Post>>().apply { value = listOf() }
    fun getPosts() = posts as LiveData<List<Post>>

    private val detailedActivePost = MutableLiveData<PostDetails?>().apply { value = null }
    fun getActivePost() = detailedActivePost as LiveData<PostDetails?>

    fun isDownloadInProgress() = ongoingTasksTracker.areThereOngoingTasks()

    private val status = MutableLiveData<Status>()
    fun getStatus() = status as LiveData<Status>

    fun refreshPosts() {
        ongoingTasksTracker.startOne()
        service.getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    ongoingTasksTracker.endOne()
                    posts.value = it
                },
                onError = {
                    ongoingTasksTracker.endOne()
                    status.value = ERROR(R.string.status_offline)
                },
                onComplete = {}
            )
            .addTo(disposables)
    }

    private class AuthorAndCommentsResponse(val author: User, val comments: List<Comment>)

    fun setActivePost(post: Post) {
        detailedActivePost.value = PostDetails(post, null, null)
        ongoingTasksTracker.startOne()

        Observable.zip(
            service.getUserById(post.userId),
            service.getComments(post.id),
            BiFunction { user: User, comments: List<Comment> ->
                AuthorAndCommentsResponse(user, comments)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    ongoingTasksTracker.endOne()
                    detailedActivePost.value = PostDetails(post, it.author, it.comments)
                    status.value = OK
                },
                onError = {
                    ongoingTasksTracker.endOne()
                    status.value = ERROR(R.string.status_offline)
                },
                onComplete = {}
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}