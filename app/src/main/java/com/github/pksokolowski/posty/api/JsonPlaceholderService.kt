package com.github.pksokolowski.posty.api

import com.github.pksokolowski.posty.api.models.Comment
import com.github.pksokolowski.posty.api.models.Post
import com.github.pksokolowski.posty.api.models.User
import retrofit2.http.GET
import retrofit2.http.Path
import io.reactivex.Observable

interface JsonPlaceholderService{

    @GET("posts")
    fun getPosts():  Observable<List<Post>>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Long): Observable<User>

    @GET("post/{id}/comments")
    fun getComments(@Path("id")id: Long) : Observable<List<Comment>>

}