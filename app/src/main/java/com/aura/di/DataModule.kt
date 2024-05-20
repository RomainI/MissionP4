package com.aura.di

import com.aura.data.network.AccountClient
import com.aura.data.repository.AccountRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {


    @Singleton
    @Provides
    fun provideAccountRepository (dataClient: AccountClient): AccountRepository{
        return AccountRepository(dataClient)
    }


}