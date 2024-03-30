package com.example.notesapp.di

import android.content.Context
import androidx.room.Room
import com.example.notesapp.db.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
   fun provideNoteDatabase(appContext: Context): NoteDatabase {
       return Room.databaseBuilder(
           appContext,
           NoteDatabase::class.java,
           "NotesTable"
           ).build()
   }
}