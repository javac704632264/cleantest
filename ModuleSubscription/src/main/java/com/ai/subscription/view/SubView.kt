package com.ai.subscription.view

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ai.subscription.R
import com.ai.subscription.hepler.SubscribeBean
import com.ai.subscription.manager.SubManager
import com.ai.subscription.util.PeroidUtils
import com.ai.subscription.util.SubStats
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import java.text.NumberFormat
import java.util.*

class SubView: FrameLayout {
    private var llWeek: LinearLayout? = null
    private var llMonth: LinearLayout? = null
    private var llYear: LinearLayout? = null
    private var tvWeek: TextView? = null
    private var tvMonth: TextView? = null
    private var tvYear: TextView? = null
    private var tvWeekPrice: TextView? = null
    private var tvMonthPrice: TextView? = null
    private var tvYearPrice: TextView? = null
//    private var tvWeekOriginPrice: TextView? = null
//    private var tvMonthOriginPrice: TextView? = null
//    private var tvYearOriginPrice: TextView? = null
    private var tvWeekUnit: TextView? = null
    private var tvMonthUnit: TextView? = null
    private var tvYearUnit: TextView? = null
    private var tvWeekDiscount: TextView? = null
    private var tvMonthDiscount: TextView? = null
    private var tvYearDiscount: TextView? = null
//    private var flWeekLottie: FrameLayout? = null
//    private var flMonthLottie: FrameLayout? = null
//    private var flYearLottie: FrameLayout? = null
//    private var lottieWeek: LottieAnimationView? = null
//    private var lottieMonth: LottieAnimationView? = null
//    private var lottieYear: LottieAnimationView? = null
//    private var lottieLoad: LottieAnimationView? = null
    private var tvDates: MutableList<TextView> = mutableListOf()
    private var tvPrices: MutableList<TextView> = mutableListOf()
//    private var tvOriginPrices: MutableList<TextView> = mutableListOf()
    private var tvPriceUnits: MutableList<TextView> = mutableListOf()
    private var tvDiscounts: MutableList<TextView> = mutableListOf()
    var onSubscriptListener: OnSubListener? = null
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : super(context,attrs){
        init(context)
        setListener()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs)

    private fun init(context: Context){
        val mView = LayoutInflater.from(context).inflate(R.layout.layout_subscription_price,this,true)
        llWeek = mView.findViewById(R.id.ll_week)
        llMonth = mView.findViewById(R.id.ll_month)
        llYear = mView.findViewById(R.id.ll_year)
        tvWeek = mView.findViewById(R.id.tv_week)
        tvMonth = mView.findViewById(R.id.tv_month)
        tvYear = mView.findViewById(R.id.tv_year)
        tvWeekPrice = mView.findViewById(R.id.tv_week_price)
        tvMonthPrice = mView.findViewById(R.id.tv_month_price)
        tvYearPrice = mView.findViewById(R.id.tv_year_price)
//        tvWeekOriginPrice = mView.findViewById(R.id.tv_origin_week_price)
//        tvMonthOriginPrice = mView.findViewById(R.id.tv_origin_month_price)
//        tvYearOriginPrice = mView.findViewById(R.id.tv_origin_year_price)
        tvWeekUnit = mView.findViewById(R.id.tv_week_unit)
        tvMonthUnit = mView.findViewById(R.id.tv_month_unit)
        tvYearUnit = mView.findViewById(R.id.tv_year_unit)
        tvWeekDiscount = mView.findViewById(R.id.tv_week_discount)
        tvMonthDiscount = mView.findViewById(R.id.tv_month_discount)
        tvYearDiscount = mView.findViewById(R.id.tv_year_discount)

//        flWeekLottie = mView.findViewById(R.id.fl_week_lottie)
//        flMonthLottie = mView.findViewById(R.id.fl_month_lottie)
//        flYearLottie = mView.findViewById(R.id.fl_year_lottie)
//        lottieWeek = mView.findViewById(R.id.lottie_week)
//        lottieMonth = mView.findViewById(R.id.lottie_month)
//        lottieYear = mView.findViewById(R.id.lottie_year)
//
//        lottieLoad = mView.findViewById(R.id.lottie_load)

        tvDiscounts.add(tvWeekDiscount!!)
        tvDiscounts.add(tvMonthDiscount!!)
        tvDiscounts.add(tvYearDiscount!!)

        tvDates.add(tvWeek!!)
        tvDates.add(tvMonth!!)
        tvDates.add(tvYear!!)

        tvPrices.add(tvWeekPrice!!)
        tvPrices.add(tvMonthPrice!!)
        tvPrices.add(tvYearPrice!!)

//        tvOriginPrices.add(tvWeekOriginPrice!!)
//        tvOriginPrices.add(tvMonthOriginPrice!!)
//        tvOriginPrices.add(tvYearOriginPrice!!)

        tvPriceUnits.add(tvWeekUnit!!)
        tvPriceUnits.add(tvMonthUnit!!)
        tvPriceUnits.add(tvYearUnit!!)

//        setTextViewStyles(tvWeek!!)
//        setTextViewStyles(tvMonth!!)
//        setTextViewStyles(tvYear!!)

//        setStrikeLine(tvWeekOriginPrice!!)
//        setStrikeLine(tvMonthOriginPrice!!)
//        setStrikeLine(tvYearOriginPrice!!)
    }

    private fun setListener(){
        llWeek?.setOnClickListener {
            refreshSizeView(isOne = true, isThree = false, isFour = false)
            SubManager.instance.subId = SubManager.instance.subIdList!![0].subId
            SubManager.instance.exitSavePercent = SubManager.instance.subIdList!![0].priceDiscount.toString()
            SubManager.instance.exitSubPrice = "$${tvPrices[0].text}"
            SubStats.veClick("/subscription/page/week")
        }

        llMonth?.setOnClickListener {
            refreshSizeView(isOne = false, isThree = true, isFour = false)
            SubManager.instance.subId = SubManager.instance.subIdList!![1].subId
            SubManager.instance.exitSavePercent = SubManager.instance.subIdList!![1].priceDiscount.toString()
            SubManager.instance.exitSubPrice = "$${tvPrices[1].text}"
            SubStats.veClick("/subscription/page/month")
        }

        llYear?.setOnClickListener {
            refreshSizeView(isOne = false, isThree = false, isFour = true)
            SubManager.instance.subId = SubManager.instance.subIdList!![2].subId
            SubManager.instance.exitSavePercent = SubManager.instance.subIdList!![2].priceDiscount.toString()
            SubManager.instance.exitSubPrice = "$${tvPrices[2].text}"
            SubStats.veClick("/subscription/page/year")
        }
    }

    private fun refreshSizeView(isOne: Boolean, isThree: Boolean, isFour: Boolean){
        llWeek?.setBackgroundResource(if (isOne){R.drawable.layer_subscription_gradient}else{R.drawable.shape_no_subscript_bg})
        llMonth?.setBackgroundResource(if (isThree){R.drawable.layer_subscription_gradient}else{R.drawable.shape_no_subscript_bg})
        llYear?.setBackgroundResource(if (isFour){R.drawable.layer_subscription_gradient}else{R.drawable.shape_no_subscript_bg})
    }

    private fun setTextViewStyles(textView: TextView) {
        val mLinearGradient =  LinearGradient(0f, 0f, textView.paint.textSize * textView.text.length * 1.0f, 0f, Color.parseColor("#AE69FF"), Color.parseColor("#3AA1FF"), Shader.TileMode.CLAMP);
        textView.paint.shader = mLinearGradient;
        textView.invalidate();
    }

    fun setSubText(bean: SubscribeBean?){
        val subIds = SubManager.instance.subIdList
        for (i in 0 until subIds?.size!!){
            val subId = subIds[i]

            val billingPeriod = if (bean == null) "" else bean.getPeriodModeWrapper(subId.subId)

            var monthPeriod: String = PeroidUtils.getPeriod(billingPeriod)
            if (TextUtils.isEmpty(monthPeriod)) {
                monthPeriod = PeroidUtils.getPeriodForId(subId.subId)
            }
            val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val priceCurrency = bean!!.getPriceCurrency(subId.subId)
            priceCurrency?.let {
                numberFormat.currency = priceCurrency
            }
            tvPriceUnits[i]?.text = numberFormat.currency.getSymbol(Locale.getDefault())
            tvDates[i]?.text = monthPeriod
            val price = if (bean == null) "--" else bean.getPriceDollarWrapper(subId.subId)
            tvPrices[i]?.text = if (TextUtils.isEmpty(price)) "--" else price
            if(subId.selStatus){
                SubManager.instance.exitSavePercent = subId.priceDiscount.toString()
                SubManager.instance.exitSubPrice = "$${price}"
            }
//            tvOriginPrices[i]?.text = numberFormat.format(SubManager.instance.originPrices!![i])
            if (subId.priceDiscount!! > 0){
                tvDiscounts[i].text = context.getString(R.string.string_off,"${subId.priceDiscount}%")
                tvDiscounts[i].visibility = VISIBLE
            }else{
                tvDiscounts[i].visibility = GONE
            }

            if (subId.selStatus){
                SubManager.instance.subId = subId.subId
                when(i){
                    0 -> {
                        refreshSizeView(isOne = true, isThree = false, isFour = false)
                    }
                    1 -> {
                        refreshSizeView(isOne = false, isThree = true, isFour = false)
                    }
                    2 -> {
                        refreshSizeView(isOne = false, isThree = false, isFour = true)
                    }
                }
            }

        }

        if (tvPrices[0]?.text.equals("--")){
            onSubscriptListener?.onSub(false)
        }else{
            onSubscriptListener?.onSub(true)
        }
    }

    private fun setStrikeLine(textView: TextView){
        textView.paint.isAntiAlias = true;//抗锯齿
        textView.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG; //中划线
    }

    fun showLottieView(isShow: Boolean){
        if (isShow){
//            flWeekLottie?.visibility = VISIBLE
//            flMonthLottie?.visibility = VISIBLE
//            flYearLottie?.visibility = VISIBLE
            //显示动画
//            startLottieAnim(lottieLoad,"sub_detail_load","sub_detail_load/data.json",true,object : AnimatorListenerAdapter(){})
//            startLottieAnim(lottieMonth,"sub_detail_load","sub_detail_load/data.json",true,object : AnimatorListenerAdapter(){})
//            startLottieAnim(lottieYear,"sub_detail_load","sub_detail_load/data.json",true,object : AnimatorListenerAdapter(){})
        }else{
//            flWeekLottie?.visibility = GONE
//            flMonthLottie?.visibility = GONE
//            flYearLottie?.visibility = GONE
//            lottieWeek?.cancelAnimation()
//            lottieMonth?.cancelAnimation()
//            lottieYear?.cancelAnimation()
        }
    }

    fun startLottieAnim(animView: LottieAnimationView?, folder: String?, animJson: String?, isAnim: Boolean, listener: AnimatorListenerAdapter?){
        animView?.let {
            if (it.isAnimating)
                return@let
            folder?.let {
                animView.imageAssetsFolder = folder
            }
            animView.setAnimation(animJson)
            animView.repeatCount = LottieDrawable.INFINITE
            animView.addAnimatorListener(listener)
            if (isAnim){
                animView.playAnimation()
            }
        }
    }

    fun setOnSubListener(onSubListener: OnSubListener){
        this.onSubscriptListener = onSubListener
    }

    interface OnSubListener{
        fun onSub(isSuccess: Boolean)
    }

}