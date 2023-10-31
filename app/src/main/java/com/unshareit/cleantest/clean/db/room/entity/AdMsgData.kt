package com.unshareit.cleantest.clean.db.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adinfo")
data class AdMsgData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var adPath: String?,
    var adName: String?,
    var desc: String?
)
