package com.ai.subscription.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ai.subscription.R
import com.ai.subscription.config.BasicsKeys
import com.ai.subscription.hepler.IAPManager
import com.ai.subscription.hepler.SubscribeHelper
import com.ai.subscription.manager.SubManager
import com.ai.subscription.purchase.BuyCallback
import com.ai.subscription.purchase.IQueryResult
import com.ai.subscription.purchase.PurchaseManager
import com.ai.subscription.ui.SubscriptionActivity
import com.ai.subscription.ui.dialog.GiveUpDialog
import com.ai.subscription.ui.dialog.LoadPayDialog
import com.ai.subscription.ui.dialog.PayDialog
import com.ai.subscription.util.SubStats
import com.ai.subscription.util.SubscribeSettings
import com.ai.subscription.util.TextViewUtils
import com.ai.subscription.util.UserAgreementUtil
import com.ai.subscription.view.SubView
import com.android.billingclient.api.Purchase
import com.doodlecamera.base.core.utils.lang.ObjectStore
import com.doodlecamera.tools.core.change.ChangeListenerManager
import com.doodlecamera.tools.core.utils.ui.SafeToast
import com.doodlecamera.tools.core.utils.ui.ViewUtils

class SubDetailFragment: BaseFragment() {
    var imgBack: ImageView? = null
    var tvRestore: TextView? = null
    var subView: SubView? = null
    var tvContinue: TextView? = null
    var flSub: FrameLayout? = null
    var imgSubBlur: ImageView? = null
    var tvSubDesc: TextView? = null
    var tvGenerate: TextView? = null
    var tvUnlockTemp: TextView? = null
    var tvMore: TextView? = null
    var tvVipDesc: TextView? = null
    var subId : String?= ""
    var loadDialog: LoadPayDialog? = null
    override fun featureId(): String? {
        return "SubDetailFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.layout_sub_detail,container,false)
        initView(mView)
        setListener()
        initData()
        return mView
    }

    private fun initView(view: View?){
        imgBack = view?.findViewById(R.id.img_back)
        tvRestore = view?.findViewById(R.id.tv_restore)
        subView = view?.findViewById(R.id.subView)
        tvContinue = view?.findViewById(R.id.tv_continue)
        flSub = view?.findViewById(R.id.fl_sub)
        imgSubBlur = view?.findViewById(R.id.img_sub_blur)
        tvSubDesc = view?.findViewById(R.id.tv_sub_desc)
        tvGenerate = view?.findViewById(R.id.tv_generate)
        tvUnlockTemp = view?.findViewById(R.id.tv_unlock_temp)
        tvMore = view?.findViewById(R.id.tv_more)
        tvVipDesc = view?.findViewById(R.id.tv_vip_desc)
    }

    private fun initData(){
        updateData()
        SubStats.veShow("/subscription/page/show")

        UserAgreementUtil.setUserAgreements(
            activity,
            tvVipDesc,
            resources.getColor(R.color.color_a2a4bd)
        )

    }

    private fun setListener(){
        imgBack?.setOnClickListener {
            if (!IAPManager.getInstance().isVIP && (activity as SubscriptionActivity).canShowQuitIntercept()){
                SubscribeSettings.setQuitInterceptTime(System.currentTimeMillis())
                GiveUpDialog.Builder(activity)
                    .setMoney(SubManager.instance.exitSubPrice)
                    .setPercent(SubManager.instance.exitSavePercent)
                    .addOnEventListener(object : GiveUpDialog.OnEventListener{
                        override fun onCancel() {
                            activity?.let {
                                (activity as SubscriptionActivity).finish()
                            }
                        }

                        override fun onContinue() {
                            //跳转到订阅支付页
                            subId = SubManager.instance.subId
                            handleBuyClick(subId)
                        }
                    }).show()
            }else{
                activity?.finish()
            }
        }

        tvRestore?.setOnClickListener {
            handleRestoreClick()
        }

        tvContinue?.setOnClickListener {
            if (ViewUtils.isClickTooFrequently(it, 1500)) {
                return@setOnClickListener
            }
            subId = SubManager.instance.subId
            handleBuyClick(subId)
            if (IAPManager.getInstance().isVIP){
                SubStats.veClick("/subscription/page/renew")
            }else{
                SubStats.veClick("/subscription/page/continue")
            }
        }
    }

    private fun getSubscribeHelper(): SubscribeHelper {
        return (requireActivity() as SubscriptionActivity).getSubscribeHelper()
    }

    private fun updateData() {
//        val bean: SubscribeBean = getSubscribeHelper().liveData.value!!
//        subView?.setSubText(bean)
        showLoadDialog()
        getSubscribeHelper()?.liveData?.observe(viewLifecycleOwner) { bean1 ->
            subView?.setSubText(bean1)
        }

        subView?.setOnSubListener(object : SubView.OnSubListener{
            override fun onSub(isSuccess: Boolean) {
                if (!isSuccess){
                    subView?.postDelayed(object : Runnable{
                        override fun run() {
                            loadDialog?.dismiss()
                            context?.let {
                                Toast.makeText(context,context?.getString(R.string.string_sub_plan_error),Toast.LENGTH_LONG).show()
                            }
                        }
                    },3000)
                }else{
                    loadDialog?.dismiss()
                }
            }
        })

        if (IAPManager.getInstance().isVIP){
            tvSubDesc?.text = getString(R.string.string_pro_experience)
            tvContinue?.text = getString(R.string.string_renew)
            SubStats.veShow("/dream_art/subscription/renew")
            TextViewUtils.setCompoundDrawables(tvGenerate,0,0,resources.getDimension(R.dimen.common_dimens_24dp).toInt(),resources.getDimension(R.dimen.common_dimens_24dp).toInt(),ContextCompat.getDrawable(
                ObjectStore.getContext(),R.drawable.icon_open_vip),null,null,null)
            TextViewUtils.setCompoundDrawables(tvUnlockTemp,0,0,resources.getDimension(R.dimen.common_dimens_24dp).toInt(),resources.getDimension(R.dimen.common_dimens_24dp).toInt(),ContextCompat.getDrawable(
                ObjectStore.getContext(),R.drawable.icon_open_vip),null,null,null)
            TextViewUtils.setCompoundDrawables(tvMore,0,0,resources.getDimension(R.dimen.common_dimens_24dp).toInt(),resources.getDimension(R.dimen.common_dimens_24dp).toInt(),ContextCompat.getDrawable(
                ObjectStore.getContext(),R.drawable.icon_open_vip),null,null,null)
        }else{
            tvSubDesc?.text = getString(R.string.string_pro_experience)
            tvContinue?.text = getString(R.string.string_continue)
            SubStats.veShow("/dream_art/subscription/continue")
        }
    }

    private fun handleBuyClick(subId: String?) {
        val manager: PurchaseManager = IAPManager.getInstance().purchaseManager ?: return
        if (!manager?.icConnectionSuccess()) {
            manager?.reConnect()
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT)
            return
        }
        IAPManager.getInstance().buy(
            context as FragmentActivity?,
            subId,
            "multi_btn",
            object : BuyCallback {
                override fun onBuySuccess(productId: String?, purchase: Purchase) {
                    //订阅成功
                    ChangeListenerManager.getInstance().notifyChange(BasicsKeys.KEY_PRODUCT_SUB_VIP,subId)
                    PayDialog.Builder(activity)
                        .setIsSuccess(true)
                        .addOnEventListener(object : PayDialog.OnEventListener{
                            override fun onOk() {
                                //更新页面支付状态
                                tvSubDesc?.text = getString(R.string.string_pro_experience)
                                tvContinue?.text = getString(R.string.string_renew)
                                TextViewUtils.setCompoundDrawables(tvGenerate,0,0,resources.getDimension(R.dimen.common_dimens_24dp).toInt(),resources.getDimension(R.dimen.common_dimens_24dp).toInt(),ContextCompat.getDrawable(ObjectStore.getContext(),R.drawable.icon_open_vip),null,null,null)
                                TextViewUtils.setCompoundDrawables(tvUnlockTemp,0,0,resources.getDimension(R.dimen.common_dimens_24dp).toInt(),resources.getDimension(R.dimen.common_dimens_24dp).toInt(),ContextCompat.getDrawable(ObjectStore.getContext(),R.drawable.icon_open_vip),null,null,null)
                                TextViewUtils.setCompoundDrawables(tvMore,0,0,resources.getDimension(R.dimen.common_dimens_24dp).toInt(),resources.getDimension(R.dimen.common_dimens_24dp).toInt(),ContextCompat.getDrawable(ObjectStore.getContext(),R.drawable.icon_open_vip),null,null,null)

                            }

                            override fun onGiveUp() {
                            }

                            override fun onContinue() {
                            }
                        }).show()
                }

                override fun onBuyFail(productId: String?, errorCode: Int, reason: String?) {
                    //订阅失败
                    PayDialog.Builder(activity)
                        .setIsSuccess(false)
                        .addOnEventListener(object : PayDialog.OnEventListener{
                            override fun onOk() {
                            }

                            override fun onGiveUp() {
                            }

                            override fun onContinue() {
                                //发起订阅支付
                                handleBuyClick(subId)
                            }
                        }).show()
                }
            })
    }

    private fun handleRestoreClick() {
        showLoadDialog()
        val manager: PurchaseManager = IAPManager.getInstance().purchaseManager ?: return
        if (!manager.icConnectionSuccess()) {
            manager.reConnect()
            loadDialog?.dismiss()
            SafeToast.showToast(R.string.sub_no_gp_service_hint, Toast.LENGTH_SHORT)
            return
        }
        IAPManager.getInstance().updateVipState(object : IQueryResult {
            override fun querySuccess() {
                loadDialog?.dismiss()
                SafeToast.showToast(R.string.sub_restore_status_success, Toast.LENGTH_SHORT)
            }

            override fun queryFail() {
                loadDialog?.dismiss()
                SafeToast.showToast(R.string.sub_restore_status_fail, Toast.LENGTH_SHORT)
            }
        })
    }

    private fun showLoadDialog(){
        activity?.let {
            if (loadDialog != null && !loadDialog?.isShowing!!){
                loadDialog = LoadPayDialog.Builder(activity).show()
            }else if (loadDialog == null){
                loadDialog = LoadPayDialog.Builder(activity).show()
            }
        }
    }

}