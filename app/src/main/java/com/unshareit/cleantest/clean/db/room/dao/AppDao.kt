package com.unshareit.cleantest.clean.db.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unshareit.cleantest.clean.db.room.entity.AdMsgData
import com.unshareit.cleantest.clean.db.room.entity.AppMsgData

@Dao
interface AppDao {
    @Query("SELECT * FROM appinfo")
    fun getAllApp():List<AppMsgData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApp(vararg appData: AppMsgData)

    @Query("SELECT * FROM adinfo")
    fun getAdRule():List<AdMsgData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAd(vararg adData: AdMsgData)
}