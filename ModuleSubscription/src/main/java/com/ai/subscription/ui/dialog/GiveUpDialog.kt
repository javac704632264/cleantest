package com.ai.subscription.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.*
import android.widget.TextView
import com.ai.subscription.R
import com.ai.subscription.util.NavigationBarUtil
import com.ai.subscription.util.SubStats
import com.doodlecamera.base.core.utils.lang.ObjectStore

class GiveUpDialog: Dialog {
    private var tvSave: TextView? = null
    private var tvDesc: TextView? = null
    private var tvCancel: TextView? = null
    private var tvContinue: TextView? = null
    constructor(context: Context): this(context, R.style.load_pay_dialog_dark)

    @SuppressLint("InflateParams")
    constructor(context: Context, themeId: Int): super(context, themeId){
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_give_up, null)
        setContentView(view)
        initDialog(context)
        initView(view)
    }

    private fun initView(view: View){
        tvSave = view.findViewById(R.id.tv_save)
        tvDesc = view.findViewById(R.id.tv_give_up_desc)
        tvCancel = view.findViewById(R.id.tv_cancel)
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

    private fun applyDialog(param: Params?){
        tvCancel?.setOnClickListener {
            dismiss()
            param?.onEventListener?.onCancel()
            SubStats.veClick("/subscription/retain/giveup")
        }

        tvContinue?.setOnClickListener {
            dismiss()
            param?.onEventListener?.onContinue()
            SubStats.veClick("/subscription/retain/continue")
        }

        if (param?.percent.equals("0")){
            param?.percent = "80"
        }
        tvSave?.text = param?.activity?.getString(R.string.string_save,param?.percent+"%")
        setTextViewStyles(tvSave)
        setUserAgreements((param?.activity as Activity),tvDesc!!,param?.saveMoney,Color.parseColor("#7fffffff"))

        SubStats.veShow("/subscription/retain/page")

    }

    private fun setTextViewStyles(textView: TextView?) {
        val mLinearGradient = LinearGradient(0f, 0f, textView?.paint?.textSize!! * textView?.text?.length!! * 1.0f, 0f, Color.parseColor("#FF3A64"), Color.parseColor("#FF3963"), Shader.TileMode.CLAMP)
        textView?.paint?.shader = mLinearGradient
        textView?.invalidate()
    }

    fun setUserAgreements(
        activity: Activity?,
        userAgreementView: TextView,
        priceProid: String?,
        agrremntColor: Int
    ) {
        val policy = ObjectStore.getContext().getResources().getString(R.string.string_user_agreement);
        var contentStr = ObjectStore.getContext().resources.getString(
            R.string.sub_trial_description_new,
            priceProid
        )
//        val policyIndex = contentStr.indexOf(policy)
//        contentStr = contentStr.replace("%1\$s", "---").replace("%2\$s", "---")
//        val spannableString = SpannableString(contentStr)
//        spannableString.setSpan(
//            ClickSpanOpen(activity!!, "", agrremntColor),
//            policyIndex,
//            policyIndex + policy.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
        userAgreementView.highlightColor = Color.TRANSPARENT
        userAgreementView.text = contentStr//spannableString
        userAgreementView.movementMethod = LinkMovementMethod.getInstance()
    }

    private class ClickSpanOpen(private val context: Activity, url: String?, color: Int) :
        URLSpan(url) {
        private var color = Color.parseColor("#7fffffff")
        override fun onClick(widget: View) {
            try {
                if (TextUtils.isEmpty(url)) return

                //todo testused
                // IAPManager.getInstance().showRetryBuyDialog(context, Client.PRODUCT_ID_MONTH);

//                HybridConfig.ActivityConfig config = new HybridConfig.ActivityConfig();
//                config.setPortal("sub_user_agreement");
//                config.setStyle(HybridConfig.STYLE_NO_TITLE);
//                config.setUrl(getURL());
//                HybridManager.startRemoteActivity(context, config);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            //            ds.setColor(Color.parseColor("#A2A4BD"));
            ds.color = color
            //ds.setColor(Color.WHITE);
            ds.strokeWidth = ds.strokeWidth * 2
            ds.style = Paint.Style.FILL_AND_STROKE
            ds.isUnderlineText = true
        }

        init {
            this.color = color
        }
    }

    interface OnEventListener{
        fun onCancel()
        fun onContinue()
    }

    class Params{
        var onEventListener: OnEventListener? = null
        var activity: Context? = null
        var percent: String? = "80%"
        var saveMoney: String? = null
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

        fun setPercent(percent: String?):Builder{
            params.percent = percent
            return this
        }

        fun setMoney(money: String?):Builder{
            params.saveMoney = money
            return this
        }

        fun show(): GiveUpDialog? {
            if (params.activity is Activity && !(params.activity as Activity).isFinishing){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !(params.activity as Activity).isDestroyed){
                    val dialog = GiveUpDialog(params.activity!!)
                    dialog.applyDialog(params)
                    NavigationBarUtil.focusNotAle(dialog.getWindow())
                    dialog.show()
                    NavigationBarUtil.hideNavigationBar(dialog.getWindow())
                    NavigationBarUtil.clearFocusNotAle(dialog.getWindow())
                    return dialog
                }else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
                    val dialog = GiveUpDialog(params.activity!!)
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