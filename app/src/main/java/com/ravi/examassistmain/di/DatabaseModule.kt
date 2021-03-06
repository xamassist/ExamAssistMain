package com.ravi.examassistmain.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ravi.examassistmain.data.database.DocumentDatabase
import com.ravi.examassistmain.data.remote.DocumentFirebaseCall
import com.ravi.examassistmain.utils.Constants
import com.ravi.examassistmain.utils.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        DocumentDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: DocumentDatabase) = database.documentDao()

    @Singleton
    @Provides
    fun provideUserDao(database: DocumentDatabase) = database.userDao()

    @Singleton
    @Provides
    fun providesFirestoreInstance(): FirebaseFirestore {

        return FirebaseFirestore.getInstance()
    }
//    @Singleton
//    @Provides
//    fun providesCollectionReference(
//        fireStore: FirebaseFirestore
//    ): CollectionReference {
//        return fireStore.collection(Constants.DOCUMENT_COLLECTION)
//    }
//}
}