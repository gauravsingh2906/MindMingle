package com.futurion.apps.mindmingle.di

import com.futurion.apps.mindmingle.domain.DefaultPuzzleGenerator
import com.futurion.apps.mindmingle.domain.repository.PuzzleGenerator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindPuzzleGenerator(
        default: DefaultPuzzleGenerator
    ): PuzzleGenerator





}