package com.github.pksokolowski.posty.api

import com.github.pksokolowski.posty.api.models.Comment
import com.github.pksokolowski.posty.api.models.Post
import com.github.pksokolowski.posty.api.models.User
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonPlaceholderService{

    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): User

    @GET("post/{id}/comments")
    suspend fun getComments(@Path("id")id: Long) : List<Comment>

}