package cleaner.phoneclean.booster.antivirus.room.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun convertIconToString(bitmap: Bitmap?) : String?{
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG,100,baos)
        val appicon = baos.toByteArray()
        val iconStr = Base64.encodeToString(appicon,Base64.DEFAULT)
        return iconStr
    }

    @TypeConverter
    fun convertStringToIcon(iconStr: String?): Bitmap?{
        val bitmapArray = Base64.decode(iconStr,Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.size)
        return bitmap
    }
}