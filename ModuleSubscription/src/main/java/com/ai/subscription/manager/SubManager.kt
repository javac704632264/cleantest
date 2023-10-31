package com.ai.subscription.manager

import android.text.TextUtils
import com.ai.subscription.config.CloudConfigUtils
import com.ai.subscription.config.SubInfo
import org.json.JSONArray
import org.json.JSONObject

class SubManager {
    var subId: String? = null
    var subIdList:MutableList<SubInfo>? = mutableListOf()
    var originPrices: MutableList<String>? = mutableListOf()
    var priceDiscountMap: MutableMap<String,String>? = mutableMapOf()
    var exitSwitch = true
    var exitSubId = ""
    var exitSubPrice = ""
    var exitSavePercent = ""

    private constructor(){
        getSubIds()
        getOriginPrice()
        initExitConfig()
    }
    companion object{
        val instance: SubManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            SubManager()
        }
    }

    fun getSubIds():MutableList<SubInfo>?{
        val subConfig = CloudConfigUtils.getConfigSubId()
        if (TextUtils.isEmpty(subConfig)){
            return null
        }
        val jsonArray = JSONArray(subConfig)
        for (i in 0 until jsonArray.length()){
            val json = jsonArray.optJSONObject(i)
            val subInfo = SubInfo()
            subInfo.subId = json.optString("subId")
            subInfo.priceDiscount = json.optInt("price_discount")
            subInfo.selStatus = json.optBoolean("select_status")
            subInfo.subPrice = json.optString("subPrice")
            subIdList?.add(subInfo)
            priceDiscountMap?.put(subInfo.subId!!, subInfo.subPrice!!)
        }
        return subIdList
    }

    fun getOriginPrice():MutableList<String>?{
        val originConfig = CloudConfigUtils.getOriginPrice()
        if (TextUtils.isEmpty(originConfig)){
            return null
        }
        val jsonArray = JSONArray(originConfig)
        for (i in 0 until jsonArray.length()){
            val json = jsonArray.optJSONObject(i)
            val originPrice = json.optString("origin_price")
            originPrices?.add(originPrice)
        }
        return originPrices
    }

    fun getPriceDiscount():MutableMap<String,String>?{
        val priceDiscount = CloudConfigUtils.getPriceDiscount()
        if (TextUtils.isEmpty(priceDiscount)){
            return null
        }
        val jsonArray = JSONArray(priceDiscount)
        for (i in 0 until jsonArray.length()){
            val json = jsonArray.optJSONObject(i)
            val discount = json.optString("price_discount")
            val subId = json.optString("subId")
            priceDiscountMap?.put(subId,discount)
        }
        return priceDiscountMap
    }

    fun initExitConfig(){
        val exitConfig = CloudConfigUtils.getExitConfig()
        val jsonObj = JSONObject(exitConfig)
        exitSwitch = jsonObj.optBoolean("exitSwitch")
//        exitSubId = jsonObj.optString("subId")
//        exitSubPrice = jsonObj.optString("subPrice")
//        exitSavePercent = jsonObj.optString("savePercent")
    }

}