package com.ai.subscription.util

import android.graphics.drawable.Drawable
import android.widget.TextView

object TextViewUtils {
    fun setCompoundDrawables(textView: TextView?,left: Int,top: Int,right: Int,bottom: Int,leftDrawable: Drawable?,topDrawable: Drawable?,rightDrawable: Drawable?,bottomDrawable: Drawable?){
        leftDrawable?.setBounds(left,top,right,bottom)
        topDrawable?.setBounds(left,top,right,bottom)
        rightDrawable?.setBounds(left,top,right,bottom)
        bottomDrawable?.setBounds(left,top,right,bottom)
        textView?.setCompoundDrawables(leftDrawable,topDrawable,rightDrawable,bottomDrawable)
    }
}