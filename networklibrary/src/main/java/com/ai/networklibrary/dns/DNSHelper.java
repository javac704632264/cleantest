package com.ai.networklibrary.dns;

import java.util.HashMap;

public class DNSHelper {
    private static HashMap<String, String> mDnsMap = new HashMap<>();

    /**
     * 更新host映射关系
     *
     * @param hosts
     */
    public static void updateDns(HashMap<String, String> hosts) {
        if (hosts == null) {
            return;
        }
        mDnsMap = hosts;
    }

    /**
     * 获取对应hostName的dns
     *
     * @param hostName
     */
    public static String getDns(String hostName) {
        if (mDnsMap != null &&!mDnsMap.isEmpty()) {
            return mDnsMap.get(hostName);
        }
        return "";
    }


    /**
     * 获取host对应的ip
     * @param hostName
     * @return
     */
    public static String getIpByHost(String hostName){
        HashMap<String,String> ipMap = new HashMap<>();
        ipMap.put("qa-api-sso.limixuexi.com","47.95.99.133");
        ipMap.put("qa-api-live.limiketang.com","47.95.99.251");
        ipMap.put("qa-api-poll.limiketang.com","47.95.99.251");
        ipMap.put("qa-api-teacher.limiketang.com","47.95.99.251");
        ipMap.put("qa-api-config.limiketang.com","47.95.99.251");
        ipMap.put("qa-live-qs-player.limiketang.com","47.95.99.251");
        return ipMap.get(hostName);
    }
}
