package com.waystone.saitama.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [WeightEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class WeightDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao

    companion object {
        @Volatile
        private var INSTANCE: WeightDatabase? = null

        fun getDatabase(context: Context): WeightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeightDatabase::class.java,
                    "weight_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}