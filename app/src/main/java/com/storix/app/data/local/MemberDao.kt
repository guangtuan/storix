package com.storix.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY isDefault DESC, updatedAt DESC, id DESC")
    fun observeMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members ORDER BY isDefault DESC, updatedAt DESC, id DESC")
    suspend fun getAllMembers(): List<Member>

    @Query("SELECT * FROM members WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultMember(): Member?

    @Query("SELECT * FROM members ORDER BY id ASC LIMIT 1")
    suspend fun getFirstMember(): Member?

    @Upsert
    suspend fun upsert(member: Member)

    @Delete
    suspend fun delete(member: Member)

    @Query("DELETE FROM members")
    suspend fun clearAll()

    @Upsert
    suspend fun upsertAll(members: List<Member>)

    @Query("UPDATE members SET isDefault = 0")
    suspend fun clearDefaultFlag()

    @Query("UPDATE members SET isDefault = 1 WHERE id = :memberId")
    suspend fun setDefaultById(memberId: Long)
}
