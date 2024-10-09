package com.abrebo.tabletennishub.di

import com.abrebo.tabletennishub.data.datasource.DataSource
import com.abrebo.tabletennishub.data.repo.Repository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UsersCollection


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UserFriendsCollection

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MatchesCollection

    @Provides
    @Singleton
    fun provideDataSource(
        @UsersCollection usersCollection: CollectionReference,
        @UserFriendsCollection userFriendsCollection: CollectionReference,
        @MatchesCollection matchesCollection: CollectionReference
    ): DataSource {
        return DataSource(usersCollection, userFriendsCollection,matchesCollection)
    }

    @Provides
    @Singleton
    fun provideRepository(dataSource: DataSource): Repository {
        return Repository(dataSource)
    }

    @Provides
    @Singleton
    @UsersCollection
    fun provideUsersCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("Kullanıcılar")
    }

    @Provides
    @Singleton
    @UserFriendsCollection
    fun provideUserFriendsCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("KullanıcıArkadaşları")
    }

    @Provides
    @Singleton
    @MatchesCollection
    fun provideMatchesCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("Maçlar")
    }
}
