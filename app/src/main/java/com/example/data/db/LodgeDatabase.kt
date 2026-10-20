package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.GuestBookingEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.RoomEntity

@Database(
    entities = [
        RoomEntity::class,
        GuestBookingEntity::class,
        PaymentRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LodgeDatabase : RoomDatabase() {
    abstract fun roomDao(): RoomDao
    abstract fun bookingDao(): BookingDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: LodgeDatabase? = null

        fun getDatabase(context: Context): LodgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LodgeDatabase::class.java,
                    "lodge_manager_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
