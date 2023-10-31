package com.unshareit.cleantest.clean.db.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appinfo")
data class AppMsgData(
    @PrimaryKey(autoGenerate = true)
    var appId: Int,
    var pkgname: String?,
)
