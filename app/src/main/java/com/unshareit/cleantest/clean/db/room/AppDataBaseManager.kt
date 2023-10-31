package com.unshareit.cleantest.clean.db.room

import android.content.Context
import androidx.room.Room
import com.unshareit.cleantest.clean.db.room.bean.AdData
import com.unshareit.cleantest.clean.db.room.bean.AppData
import com.unshareit.cleantest.clean.db.room.db.AppDatabase
import com.unshareit.cleantest.clean.db.room.entity.AdMsgData
import com.unshareit.cleantest.clean.db.room.entity.AppMsgData

class AppDataBaseManager private constructor(){
    private var appDatabase: AppDatabase? = null

    companion object{
        @Volatile
        private var instances : AppDataBaseManager? = null
        fun getInstance() : AppDataBaseManager {
            return instances ?: synchronized(this){
                instances ?: AppDataBaseManager().also { instances = it }
            }
        }
    }


    fun getAppDataBase(ctx: Context): AppDatabase {
        if (appDatabase == null){
            appDatabase = Room.databaseBuilder(ctx, AppDatabase::class.java,"clean.db")
//                .addMigrations(MIGRATION_1_2)
                .enableMultiInstanceInvalidation()  //多进程
                .allowMainThreadQueries()
                .build()
        }
        return appDatabase as AppDatabase
    }

//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            // 创建临时表
//            database.execSQL(
//                "CREATE TABLE notify_new (sbn_key TEXT, title TEXT, text TEXT, pkg_name TEXT,id INTEGER, receive_time Long, PRIMARY KEY(receive_time))")
//            // 拷贝数据
//            database.execSQL(
//                "INSERT INTO notify_new (id, sbn_key, title,text,pkg_name,receive_time) SELECT id, sbn_key, title,text,pkg_name,receive_time FROM notify")
//            // 删除老的表
//            database.execSQL("DROP TABLE notify")
//            // 改名
//            database.execSQL("ALTER TABLE notify_new RENAME TO notify")
//        }
//    }

    fun getAppData(appData: AppData): AppMsgData {
        return AppMsgData(appData.appId,appData.pkgname)
    }

    fun getAdData(adData: AdData): AdMsgData{
        return AdMsgData(adData.id,adData.adPath,adData.adName,adData.desc)
    }

    fun insertAppData(ctx: Context, appData: AppData){
        val appDao = getAppDataBase(ctx).appDao()
        val appData = getAppData(appData)
        appDao.insertApp(appData)
    }

    fun queryAppData(ctx: Context):List<AppMsgData>{
        val appDao = getAppDataBase(ctx).appDao()
        return appDao.getAllApp()
    }

    fun insertAdData(ctx: Context,adData: AdData){
        val appDao = getAppDataBase(ctx).appDao()
        val appData = getAdData(adData)
        appDao.insertAd(appData)
    }

    fun queryAdData(ctx: Context):List<AdMsgData>{
        val appDao = getAppDataBase(ctx).appDao()
        return appDao.getAdRule()
    }
//
//    fun delClipDataById(ctx: Context, dataId: Int){
//        val clipDao = getAppDataBase(ctx).clipBoardDao()
//        clipDao.delClipBoardById(dataId)
//    }
//
//    fun delClipData(ctx: Context, clipData: ClipBoardData){
//        val clipDao = getAppDataBase(ctx).clipBoardDao()
////        val clipBoard = getClipBoardData(clipData)
//        clipDao.delClipBoard(clipData)
//    }
//
//    fun queryClipData(ctx: Context): List<ClipBoardData>{
//        val clipDao = getAppDataBase(ctx).clipBoardDao()
//        return clipDao.getClipBoardDataByAsc()
//    }
//
//    fun queryClipDataFromId(ctx: Context):List<ClipBoardData>{
//        val clipDao = getAppDataBase(ctx).clipBoardDao()
//        return clipDao.getClipBoardDataByDesc()
//    }
//
//    fun queryClipDataById(ctx: Context, id: Int): ClipBoardData{
//        val clipDao = getAppDataBase(ctx).clipBoardDao()
//        return clipDao.getClipBoardDataById(id)
//    }
//
//    fun queryAllNotify(ctx: Context): List<NotifyData>{
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        return notifyDao.getAllNotify()
//    }
//
//    /**
//     * 根据通知id删除通知
//     */
//    fun delNotifyById(ctx: Context, notifyId: Int){
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        notifyDao.delNotifyById(notifyId)
//    }
//
//    fun insertNotifyData(context: Context, notify: NotifyLockItem) {
//        val notifyDao = getAppDataBase(context).notifyDao()
//        val notifyData = getNotifyData(notify);
//
//        notifyDao.insertNotify(notifyData)
//    }
//
//    private fun getNotifyData(notify: NotifyLockItem): NotifyData {
//        return NotifyData(notify.id, notify.key, notify.title, notify.content, notify.packageName, notify.postTime)
//    }


//    private fun getNotifyData(notify: Notify): NotifyData {
//        return NotifyData(notify.id,
//                notify.key,
//                notify.title,
//                notify.text,
//                notify.pkg_name,
//                notify.receive_time,
//                notify.icon,
//                notify.state)
//    }

    /**
     * 保存通知
     */
//    fun saveNotify(ctx: Context, notify: Notify){
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        val notifyData = getNotifyData(notify)
//        notifyDao.insertNotify(notifyData)
//    }


    /**
     * 删除超时通知
     */
//    fun delTimeOutNotify(ctx: Context){
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        notifyDao.delNotifyByTimeout(System.currentTimeMillis(),CloudAttrProp.getNotificationTimeOut())
//    }

    /**
     * 修改通知读取状态
     */
//    fun modifyNotifyByState(ctx: Context, notifyId: Int){
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        val notifyDataList = notifyDao.getNotifyById(notifyId)
//        notifyDataList?.let {
//            for (notification: NotifyData in it){
//                val notify = notification.copy(state = 0)
//                notifyDao.updateNotify(notify)
//            }
//        }
//        ClipBoardData(0,"",0)
//    }

    /**
     * 获取未读通知
     */
//    fun queryUnReadNotify(ctx: Context): ArrayList<HashMap<Int,List<NotifyData>>>{
//        //过滤满足条件所有通知>>根据title降序获取通知
//        val notifyList = ArrayList<HashMap<Int,List<NotifyData>>>()
//        val time = System.currentTimeMillis()
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        val notifications = notifyDao.getNotifyUniqueByStateOrTime(1,time,CloudAttrProp.getNotificationTimeOut())
//        notifications?.let {
//            for (notifyData: NotifyData in it){
//                val notifyMap = HashMap<Int,List<NotifyData>>()
//                val notificationList = notifyDao.getNotifyByIdOrTime(notifyData.id,time,CloudAttrProp.getNotificationTimeOut())
//                notifyMap.put(notifyData.id,notificationList)
//                notifyList.add(notifyMap)
//            }
//        }
//        return notifyList
//    }

    /**
     * 将通知改为已读
     */
//    fun updateReadNotify(ctx: Context){
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        val notifyList = queryUnReadNotify(ctx)
//        for (notifyMap: HashMap<Int,List<NotifyData>> in notifyList){
//            notifyMap.forEach{
//                (_,value)->
//                for (notifyData: NotifyData in value){
//                    val notify = notifyData.copy(state = 0)
//                    notifyDao.updateNotify(notify)
//                }
//            }
//        }
//    }

    /**
     * 根据通知id查询通知
     */
//    fun queryNotify(ctx: Context, notifyId: Int): List<NotifyData>{
//        val notifyDao = getAppDataBase(ctx).notifyDao()
//        return notifyDao.getNotifyById(notifyId)
//    }
}