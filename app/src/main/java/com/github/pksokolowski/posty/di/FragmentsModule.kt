package com.github.pksokolowski.posty.di

import com.github.pksokolowski.posty.screens.active.ActivePostFragment
import com.github.pksokolowski.posty.screens.posts.PostsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector
    abstract fun contributesPostsFragment(): PostsFragment

    @ContributesAndroidInjector
    abstract fun contributesActivePostFragment(): ActivePostFragment
}