package com.unshareit.cleantest.clean.db.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cleaner.phoneclean.booster.antivirus.room.converter.Converters
import cleaner.phoneclean.booster.antivirus.room.converter.ObjConverters
import com.unshareit.cleantest.clean.db.room.dao.AppDao
import com.unshareit.cleantest.clean.db.room.entity.AdMsgData
import com.unshareit.cleantest.clean.db.room.entity.AppMsgData

@Database(entities = arrayOf(AppMsgData::class,AdMsgData::class),version = 1)
@TypeConverters(Converters::class, ObjConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun appDao(): AppDao
}