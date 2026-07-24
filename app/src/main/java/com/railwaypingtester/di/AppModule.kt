package com.railwaypingtester.di

import android.content.Context
import com.railwaypingtester.data.local.ServerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideServerRepository(@ApplicationContext context: Context): ServerRepository {
        return ServerRepository(context)
    }
}