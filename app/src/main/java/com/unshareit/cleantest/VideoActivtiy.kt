package com.unshareit.cleantest

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.unshareit.cleantest.view.CustomTextureView


class VideoActivtiy: AppCompatActivity() {

    var customTextureView: CustomTextureView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        customTextureView = findViewById(R.id.customTextureView)
//        customTextureView?.setAspectRatio(9,16)
        customTextureView?.startVideo()

        val btn1 = findViewById<Button>(R.id.btn1)

        btn1?.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

//            restartByKillProcess(this,MainActivity::class.java)
            restartByClearTop(this)
        }

        val phoneNumber1 = "+1 555-1234567" // 美国

        val phoneNumber2 = "+44 20 1234 5678" // 英国

        val phoneNumber3 = "+81 3-1234-5678" // 日本


        Log.e("VideoActivity",""+getCountryCodeFromPhoneNumber(phoneNumber1)) // "+1"

        Log.e("VideoActivity",""+getCountryCodeFromPhoneNumber(phoneNumber2)) // "+44"

        Log.e("VideoActivity",""+getCountryCodeFromPhoneNumber(phoneNumber3)) // "+81"
        Log.e("VideoActivity",""+getCountryCodeFromPhoneNumber("+8613439088130"))
        Log.e("VideoActivity","号码是否有效==>"+doValid("312345678","81"))
        Log.e("VideoActivity","号码是否有效==>"+doValid("85816336648","62"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null) {
            val imageNames = data.clipData;
            if (imageNames != null) {
                for (i in 0 until imageNames.getItemCount()){
                    Log.e("VideoActivity","uri==>${imageNames.getItemAt(i).getUri()}")
                }
            } else {
                Log.e("VideoActivity","没有多选==>${data.data}")
            }
        } else {
        }

    }

    fun isValidInternationalPhoneNumber(phoneNumber: String?, countryCode: String?): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        return try {
            val number = phoneNumberUtil.parse(phoneNumber, countryCode)
            phoneNumberUtil.isValidNumber(number)
        } catch (e: NumberParseException) {
            // 号码解析失败，不是有效的国际手机号
            false
        }
    }

    fun getCountryCodeFromPhoneNumber(phoneNumber: String?): String? {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        return try {
            val number = phoneNumberUtil.parse(phoneNumber, null)
            val countryCode = number.countryCode
            "$countryCode"
        } catch (e: NumberParseException) {
            // 号码解析失败，无法获得国家/地区区号
            null
        }
    }

    /**
     * @Author: Jet
     * @Description 手机校验逻辑
     * @param phoneNumber 手机号
     * @param countryCode 手机区号
     * @Date: 2018/5/9 9:21
     */
    fun doValid(phoneNumber: String, countryCode: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val ccode = countryCode.toInt()
        val phone = phoneNumber.toLong()
        val pn = PhoneNumber()
        pn.countryCode = ccode
        pn.nationalNumber = phone
        return phoneNumberUtil.isValidNumber(pn)
    }

//    fun getCountryCodeFromPhoneNumber(phoneNumber: String?): String? {
//        val phoneNumberUtil = PhoneNumberUtil.getInstance()
//        return try {
//            val number = phoneNumberUtil.parse(phoneNumber, null)
//            phoneNumberUtil.getRegionCodeForNumber(number)
//        } catch (e: NumberParseException) {
//            // 号码解析失败，无法获得国家/地区区号
//            null
//        }
//    }

    /**
     * 使用 killProcess 杀死自身，系统会恢复应用
     *
     * @param context
     * @param cls
     */
    fun restartByKillProcess(context: Context, cls: Class<*>?) {
        val intent = Intent(context, cls)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Process.killProcess(Process.myPid())
    }

    /**
     * 使用 AlarmManager 来帮助重启
     *
     * @param context
     * @param cls
     */
    fun restartByAlarm(context: Context, cls: Class<*>?) {
        val mStartActivity = Intent(context, cls)
        val mPendingIntentId = 123456
        val pIntent = PendingIntent.getActivity(
            context,
            mPendingIntentId,
            mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC, System.currentTimeMillis() + 500] = pIntent
        System.exit(0)
    }

    /**
     * 重启应用重启。
     *
     * @param context
     */
    fun restartByClearTop(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        Process.killProcess(Process.myPid())
    }


}