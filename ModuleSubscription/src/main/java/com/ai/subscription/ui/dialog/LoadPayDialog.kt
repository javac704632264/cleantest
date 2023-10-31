package com.ai.subscription.ui.dialog

import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.view.*
import android.widget.ImageView
import com.ai.subscription.R
import com.ai.subscription.manager.AnimManager
import com.ai.subscription.util.NavigationBarUtil
import com.airbnb.lottie.LottieAnimationView
import com.doodlecamera.base.core.utils.lang.ObjectStore

class LoadPayDialog: Dialog {
    var imgPlanLoad: LottieAnimationView? = null
    constructor(context: Context): this(context, R.style.load_pay_dialog_dark)

    @SuppressLint("InflateParams")
    constructor(context: Context, themeId: Int): super(context, themeId){
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_load, null)
        setContentView(view)
        initDialog(context)
        initView(view)
    }

    private fun initView(view: View){
        imgPlanLoad = view.findViewById(R.id.lottie_anim)
    }

    private fun initDialog(context: Context){
        val window = window ?: return
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val wmlp = window.attributes
        wmlp.width = context.resources.getDimension(R.dimen.common_dimens_320dp).toInt()
        wmlp.gravity = Gravity.CENTER
        window.attributes = wmlp
        window.setWindowAnimations(android.R.style.Animation_Dialog)
        window.setBackgroundDrawableResource(R.color.transparent)
    }

    private fun applyDialog(param: Params?) {
//        start()
        AnimManager.startLottieAnim(imgPlanLoad,"loading","loading/data.json",true,object : AnimatorListenerAdapter(){})
    }

//    private fun start(){
//        imgPlanLoad?.setImageDrawable(null)
//        val ad =  AnimationDrawable()
//        val ar = ObjectStore.getContext().resources.obtainTypedArray(R.array.pay_load_icon)
//        for (i in 0 until ar?.length()) {
//            val drawable = ObjectStore.getContext().resources.getDrawable(ar.getResourceId(i,0))
//            ad?.addFrame(drawable, 63)
//        }
//        ad?.isOneShot = false
//        imgPlanLoad?.setImageDrawable(ad)
//        ad?.start()
//    }

    interface OnEventListener{
        fun onClose()
    }

    class Params{
        var onEventListener: OnEventListener? = null
        var activity: Context? = null
    }

    class Builder constructor(activity: Activity?){
        private var params = Params()
        init {
            params.activity = activity
        }

        fun addOnEventListener(onEventListener: OnEventListener): Builder {
            params.onEventListener = onEventListener
            return this
        }

        fun show(): LoadPayDialog? {
            if (params.activity is Activity && !(params.activity as Activity).isFinishing){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !(params.activity as Activity).isDestroyed){
                    val dialog = LoadPayDialog(params.activity!!)
                    dialog.applyDialog(params)
                    NavigationBarUtil.focusNotAle(dialog.window)
                    dialog.show()
                    NavigationBarUtil.hideNavigationBar(dialog.window)
                    NavigationBarUtil.clearFocusNotAle(dialog.window)
                    return dialog
                }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
                    val dialog = LoadPayDialog(params.activity!!)
                    dialog.applyDialog(params)
                    NavigationBarUtil.focusNotAle(dialog.window)
                    dialog.show()
                    NavigationBarUtil.hideNavigationBar(dialog.window)
                    NavigationBarUtil.clearFocusNotAle(dialog.window)
                    return dialog
                }
            }
            return null
        }
    }

    override fun onStart() {
        super.onStart()
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        this.window!!.decorView.systemUiVisibility = uiOptions
    }
}