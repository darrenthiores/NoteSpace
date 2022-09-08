package com.dev.notespace.di

import com.dev.core.domain.useCase.NoteSpaceInteractor
import com.dev.core.domain.useCase.NoteSpaceUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    @ViewModelScoped
    abstract fun provideUseCase(noteCaseInteractor: NoteSpaceInteractor): NoteSpaceUseCase
}