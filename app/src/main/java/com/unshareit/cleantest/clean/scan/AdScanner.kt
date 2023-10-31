package com.unshareit.cleantest.clean.scan

import android.content.Context
import com.unshareit.cleantest.clean.db.room.AppDataBaseManager
import com.unshareit.cleantest.clean.db.room.entity.AdMsgData

class AdScanner {

    fun scanAd(ctx: Context){
        val adMsg = AppDataBaseManager.getInstance().queryAdData(ctx)
       scanPath(adMsg)
    }

    private fun scanPath(adMsg: List<AdMsgData>){
        for (ad in adMsg){

        }
    }
}