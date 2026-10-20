package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms ORDER BY roomNumber ASC")
    fun getAllRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE status = :status ORDER BY roomNumber ASC")
    fun getRoomsByStatus(status: RoomStatus): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE id = :id LIMIT 1")
    suspend fun getRoomById(id: Long): RoomEntity?

    @Query("SELECT * FROM rooms WHERE roomNumber = :number LIMIT 1")
    suspend fun getRoomByNumber(number: String): RoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRooms(rooms: List<RoomEntity>)

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Query("UPDATE rooms SET status = :status WHERE id = :roomId")
    suspend fun updateRoomStatus(roomId: Long, status: RoomStatus)

    @Query("UPDATE rooms SET status = :status WHERE roomNumber = :roomNumber")
    suspend fun updateRoomStatusByNumber(roomNumber: String, status: RoomStatus)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Query("SELECT COUNT(*) FROM rooms")
    suspend fun getRoomCount(): Int
}
