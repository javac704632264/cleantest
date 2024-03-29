package com.unshareit.cleantest.view

import android.graphics.Color
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class CommonBgStyleConfig(jsonObject: JsonObject) {
    @SerializedName("bg_colors")
    var bgColors: IntArray? = null
        private set
    val bgGradientAngle: Int = 0
    @SerializedName("gradient_position")
    var gradientPosition: FloatArray? = null
    @SerializedName("stroke_colors")
    var strokeColors: IntArray? = null
        private set
    val strokeGradientAngle: Int = 0
    val strokeColorsPosition: Array<Float>? = null
    @SerializedName("radii")
    var radii: FloatArray? = null
        private set
    @SerializedName("round_corner")
    var roundCorner: BooleanArray? = null
        private set


    init {
        try {
            var jsonArray = jsonObject.getAsJsonArray("bg_colors")
            if (jsonArray != null && !jsonArray.isEmpty) {
                val list = mutableListOf<Int>()
                for (jsonElement in jsonArray) {
                    list.add(Color.parseColor(jsonElement.asString))
                }
                bgColors = list.toIntArray()
            }

            jsonArray = jsonObject.getAsJsonArray("gradient_position")
            if (jsonArray != null && !jsonArray.isEmpty) {
                val list = mutableListOf<Float>()
                for (jsonElement in jsonArray) {
                    list.add(jsonElement.asFloat)
                }
                gradientPosition = list.toFloatArray()
            }

            jsonArray = jsonObject.getAsJsonArray("stroke_colors")
            if (jsonArray != null && !jsonArray.isEmpty) {
                val list = mutableListOf<Int>()
                for (jsonElement in jsonArray) {
                    list.add(Color.parseColor(jsonElement.asString))
                }
                strokeColors = list.toIntArray()
            }

            jsonArray = jsonObject.getAsJsonArray("radii")
            if (jsonArray != null && !jsonArray.isEmpty) {
                val list = mutableListOf<Float>()
                for (jsonElement in jsonArray) {
                    list.add(jsonElement.asFloat)
                }
                radii = list.toFloatArray()
            }

            jsonArray = jsonObject.getAsJsonArray("round_corner")
            if (jsonArray != null && !jsonArray.isEmpty) {
                val list = mutableListOf<Boolean>()
                for (jsonElement in jsonArray) {
                    list.add(jsonElement.asBoolean)
                }
                roundCorner = list.toBooleanArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}