package com.dev.core.di

import com.dev.core.domain.repository.INoteSpaceRepository
import com.dev.core.data.NoteSpaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [DatabaseModule::class, NetworkModule::class, DataStoreModule::class, FirebaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideRepository(noteSpaceRepository: NoteSpaceRepository): INoteSpaceRepository
}