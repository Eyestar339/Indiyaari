package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PeerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PeerDao {
    @Query("SELECT * FROM peers ORDER BY matchedTimestamp DESC")
    fun getAllPeers(): Flow<List<PeerProfile>>

    @Query("SELECT * FROM peers WHERE id = :peerId LIMIT 1")
    suspend fun getPeerById(peerId: String): PeerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeer(peer: PeerProfile)

    @Query("DELETE FROM peers WHERE id = :peerId")
    suspend fun deletePeerById(peerId: String)
}
