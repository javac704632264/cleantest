package com.ai.subscription.util

import com.doodlecamera.base.core.utils.lang.ObjectStore
import java.io.BufferedReader
import java.io.InputStreamReader

object AssetsUtils {
    fun getFromAssets(fileName: String?): String? {
        try {
            val inputReader = InputStreamReader(ObjectStore.getContext().getResources().getAssets().open(fileName!!))
            val bufReader = BufferedReader(inputReader)
            var line: String? = ""
            var Result: String? = ""
            while (bufReader.readLine().also { line = it } != null) Result += line
            return Result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}