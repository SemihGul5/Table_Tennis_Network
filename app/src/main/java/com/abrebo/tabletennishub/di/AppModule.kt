package com.abrebo.tabletennishub.di

import com.abrebo.tabletennishub.data.datasource.DataSource
import com.abrebo.tabletennishub.data.repo.Repository
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDataSource(collectionReference: CollectionReference):DataSource{
        return DataSource(collectionReference)
    }

    @Provides
    @Singleton
    fun provideRepository(dataSource: DataSource):Repository{
        return Repository(dataSource)
    }

    @Provides
    @Singleton
    fun provideCollectionReference():CollectionReference{
        return Firebase.firestore.collection("Kullanıcılar")
    }

}