package com.ai.subscription.util

import com.doodlecamera.base.core.stats.Stats
import com.doodlecamera.base.core.utils.lang.ObjectStore

object SubStats {
    private const val PARAM_PVE_CUR = "pve_cur"
    private const val UF_MAIN_TAB_SWITCH = "UF_MainTabSwitch"
    private const val VE_SHOW = "VE_Show"
    private const val VE_CLICK = "VE_Click"
    private const val PARAM_STATE = "state"
    fun veShow(pve: String?){
        val params: HashMap<String, String> = LinkedHashMap()
        pve?.let {
            params[PARAM_PVE_CUR] = pve
        }
        Stats.onEvent(ObjectStore.getContext(), VE_SHOW, params)
    }

    fun veClick(pve: String?){
        val params: HashMap<String, String> = LinkedHashMap()
        pve?.let {
            params[PARAM_PVE_CUR] = pve
        }
        Stats.onEvent(ObjectStore.getContext(), VE_CLICK, params)
    }
}