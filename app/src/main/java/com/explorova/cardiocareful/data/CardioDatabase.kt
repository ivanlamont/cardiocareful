package com.explorova.cardiocareful.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for Cardio Careful application.
 */
@Database(
    entities = [HeartRateReading::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CardioDatabase : RoomDatabase() {

    abstract fun heartRateReadingDao(): HeartRateReadingDao

    companion object {
        @Volatile
        private var instance: CardioDatabase? = null

        fun getInstance(context: Context): CardioDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): CardioDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CardioDatabase::class.java,
                "cardio_database"
            ).build()
        }
    }
}
