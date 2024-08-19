package com.gribanskij.predictor.di

import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.LocalDataSource
import com.gribanskij.predictor.data.source.remote.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Qualifier


@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {





    @Qualifier
    annotation class LocalData

    @Qualifier
    annotation class RemoteData


    @Binds
    @LocalData
    @ViewModelScoped
    abstract fun bindLocalDataSource(imp: LocalDataSource): DataSource

    //@Binds
    //@RemoteData
    //@ViewModelScoped
    //abstract fun bindRemoteDataSource(imp: RemoteDataSource): DataSource




}