package com.example.storyappfix.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.storyappfix.entity.StoryEntity
import com.example.storyappfix.entity.StoryKeys

@Database(
    entities = [StoryEntity::class, StoryKeys::class],
    version = 1,
    exportSchema = false
)
abstract class DatabaseStories : RoomDatabase(){
    abstract fun dao(): StoryDao
}