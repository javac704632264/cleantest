package cleaner.phoneclean.booster.antivirus.room.converter

import android.app.PendingIntent
import android.os.Parcelable
import android.util.Base64
import androidx.room.TypeConverter
import java.io.*

class ObjConverters {

    @TypeConverter
    fun objectToString(pendingIntent: Parcelable?) : String{
        val byteArrayOutputStream : ByteArrayOutputStream?= ByteArrayOutputStream()
        val objectOutputStream : ObjectOutputStream? = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream?.writeObject(pendingIntent)
        objectOutputStream?.flush()
        val objStr =  Base64.encodeToString(byteArrayOutputStream?.toByteArray(),Base64.DEFAULT)
//        var bytes = byteArrayOutputStream?.toByteArray()
        if (objectOutputStream != null) {
            try {
                objectOutputStream.close()
            } catch (e: IOException) {
            }
        }
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.close()
            } catch (e: IOException) {
            }
        }
        return objStr!!
    }

    @TypeConverter
    fun stringToObject(objStr: String) : Parcelable{
        val objArray = Base64.decode(objStr,Base64.DEFAULT)

        val byteArrayInputStream : ByteArrayInputStream? = ByteArrayInputStream(objArray)
        val objectInputStream : ObjectInputStream? = ObjectInputStream(byteArrayInputStream)
        val obj : PendingIntent? = objectInputStream?.readObject() as PendingIntent?
        if (byteArrayInputStream != null) {
            try {
                byteArrayInputStream.close()
            } catch (e: IOException) {
            }
        }
        if (objectInputStream != null) {
            try {
                objectInputStream.close()
            } catch (e: IOException) {
            }
        }
        return obj!!
    }
}