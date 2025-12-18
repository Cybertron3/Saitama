package com.ankitesh.saitama.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WeightEntry::class, CigEntry::class, CigTarget::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class WeightDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao
    abstract fun cigDao(): CigDao

    companion object {
        @Volatile
        private var INSTANCE: WeightDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create cig_entries table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cig_entries (
                        date INTEGER NOT NULL PRIMARY KEY,
                        count INTEGER NOT NULL,
                        lastUpdated INTEGER NOT NULL
                    )
                """)

                // Create cig_target table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cig_target (
                        id INTEGER NOT NULL PRIMARY KEY,
                        target INTEGER NOT NULL,
                        lastUpdated INTEGER NOT NULL
                    )
                """)
            }
        }

        fun getDatabase(context: Context): WeightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeightDatabase::class.java,
                    "weight_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}