package com.ai.subscription.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ai.subscription.R
import com.ai.subscription.util.NavigationBarUtil
import com.ai.subscription.util.SubStats

class PayDialog: Dialog {
    private var imgPayStar: ImageView? = null
    private var imgPayState: ImageView? = null
    private var tvPayState: TextView? = null
    private var tvPayDesc: TextView? = null
    private var llSuccessItem: LinearLayout? = null
    private var tvRemoveItem: TextView? = null
    private var tvGenerateItem: TextView? = null
    private var tvOk: TextView? = null
    private var llFailItem: LinearLayout? = null
    private var tvGiveUp: TextView? = null
    private var tvContinue: TextView? = null
    constructor(context: Context): this(context, R.style.load_pay_dialog_dark)

    @SuppressLint("InflateParams")
    constructor(context: Context, themeId: Int): super(context, themeId){
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_result, null)
        setContentView(view)
        initDialog(context)
        initView(view)
    }

    private fun initView(view: View){
        imgPayStar = view.findViewById(R.id.img_pay_success_star)
        imgPayState = view.findViewById(R.id.img_pay_state)
        tvPayState = view.findViewById(R.id.tv_pay_result_state)
        tvPayDesc = view.findViewById(R.id.tv_pay_result_desc)
        llSuccessItem = view.findViewById(R.id.ll_success_item)
        tvRemoveItem = view.findViewById(R.id.tv_remove_item)
        tvGenerateItem = view.findViewById(R.id.tv_generate_item)
        tvOk = view.findViewById(R.id.tv_ok)
        llFailItem = view.findViewById(R.id.ll_fail_item)
        tvGiveUp = view.findViewById(R.id.tv_give_up)
        tvContinue = view.findViewById(R.id.tv_continue)
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

    interface OnEventListener{
        fun onOk()
        fun onGiveUp()
        fun onContinue()
    }

    private fun applyDialog(param: Params?) {
        param?.let {
            if (param?.isSuccess == true){
                //成功
                tvPayState?.text = param.activity?.getString(R.string.string_successfully)
//                setTextViewStyles(tvRemoveItem)
                setTextViewStyles(tvGenerateItem)
                llSuccessItem?.visibility = View.VISIBLE
                llFailItem?.visibility = View.GONE
                tvOk?.visibility = View.VISIBLE
                tvGiveUp?.visibility = View.GONE
                tvContinue?.visibility = View.GONE
                imgPayStar?.visibility = View.VISIBLE
                imgPayState?.setBackgroundResource(R.drawable.pay_success)
                tvPayDesc?.text = param.activity?.getString(R.string.string_premium_success)
                SubStats.veShow("/subscription/page/success")
            }else{
                //失败
                tvPayState?.text = param.activity?.getString(R.string.string_upgrade_failed)
                llSuccessItem?.visibility = View.GONE
                llFailItem?.visibility = View.VISIBLE
                tvOk?.visibility = View.GONE
                tvGiveUp?.visibility = View.VISIBLE
                tvContinue?.visibility = View.VISIBLE
                imgPayStar?.visibility = View.GONE
                imgPayState?.setBackgroundResource(R.drawable.pay_fail)
                tvPayDesc?.text = param.activity?.getString(R.string.string_failed_premium)
                SubStats.veShow("/subscription/page/fail")
            }
        }
//        setTextViewStyles(tvPayState)

        tvOk?.setOnClickListener {
            dismiss()
            param?.onEventListener?.onOk()
            SubStats.veClick("/subscription/success/ok")
        }

        tvGiveUp?.setOnClickListener {
            dismiss()
            param?.onEventListener?.onGiveUp()
            SubStats.veClick("/subscription/fail/giveup")
        }

        tvContinue?.setOnClickListener {
            dismiss()
            param?.onEventListener?.onContinue()
            SubStats.veClick("/subscription/fail/continue")
        }

    }

    private fun setTextViewStyles(textView: TextView?) {
        val mLinearGradient = LinearGradient(0f, 0f, textView?.paint?.textSize!! * textView?.text?.length!! * 1.0f, 0f, Color.parseColor("#AE69FF"), Color.parseColor("#3AA1FF"), Shader.TileMode.CLAMP)
        textView?.paint?.shader = mLinearGradient
        textView?.invalidate()
    }

    class Params{
        var onEventListener: OnEventListener? = null
        var activity: Context? = null
        var isSuccess: Boolean? = true
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

        fun setIsSuccess(isSuccess: Boolean): Builder{
            params.isSuccess = isSuccess
            return this
        }

        fun show(): PayDialog? {
            if (params.activity is Activity && !(params.activity as Activity).isFinishing){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !(params.activity as Activity).isDestroyed){
                    val dialog = PayDialog(params.activity!!)
                    dialog.applyDialog(params)
                    NavigationBarUtil.focusNotAle(dialog.getWindow())
                    dialog.show()
                    NavigationBarUtil.hideNavigationBar(dialog.getWindow())
                    NavigationBarUtil.clearFocusNotAle(dialog.getWindow())
                    return dialog
                }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
                    val dialog = PayDialog(params.activity!!)
                    dialog.applyDialog(params)
                    NavigationBarUtil.focusNotAle(dialog.getWindow())
                    dialog.show()
                    NavigationBarUtil.hideNavigationBar(dialog.getWindow())
                    NavigationBarUtil.clearFocusNotAle(dialog.getWindow())
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