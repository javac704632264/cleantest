package com.unshareit.cleantest.clean.utils

import android.os.Environment
import java.io.File

object StorageManager {

    fun getRootPath(): String?{
        return Environment.getExternalStorageDirectory().absolutePath + File.separator
    }


}