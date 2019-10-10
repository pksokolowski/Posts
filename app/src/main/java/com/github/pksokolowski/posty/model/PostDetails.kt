package com.github.pksokolowski.posty.model

import com.github.pksokolowski.posty.api.models.Comment
import com.github.pksokolowski.posty.api.models.Post
import com.github.pksokolowski.posty.api.models.User

class PostDetails(val post: Post, val author: User?, val comments: List<Comment>?)