package com.ai.subscription.manager

import android.animation.AnimatorListenerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

object AnimManager {

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

    fun startScanLottieAnim(animView: LottieAnimationView?, folder: String?, animJson: String?, isAnim: Boolean, listener: AnimatorListenerAdapter?){
        animView?.let {
            if (it.isAnimating)
                return@let
            folder?.let {
                animView.imageAssetsFolder = folder
            }
            animView.setAnimation(animJson)
//            animView.repeatCount = LottieDrawable.INFINITE
            animView.addAnimatorListener(listener)
            if (isAnim){
                animView.playAnimation()
            }
        }
    }

}