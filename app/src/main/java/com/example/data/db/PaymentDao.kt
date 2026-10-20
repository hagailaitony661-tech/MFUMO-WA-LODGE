package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payments WHERE paymentDate >= :startTimestamp AND paymentDate <= :endTimestamp ORDER BY paymentDate DESC")
    fun getPaymentsBetween(startTimestamp: Long, endTimestamp: Long): Flow<List<PaymentRecordEntity>>

    @Query("SELECT SUM(amount) FROM payments")
    fun getTotalRevenue(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM payments WHERE paymentDate >= :startTimestamp")
    fun getRevenueSince(startTimestamp: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(payments: List<PaymentRecordEntity>)
}
