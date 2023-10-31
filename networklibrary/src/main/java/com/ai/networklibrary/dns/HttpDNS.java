package com.ai.networklibrary.dns;

import android.text.TextUtils;

import com.ai.networklibrary.utils.NetLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

public class HttpDNS implements Dns {
    private static final Dns SYSTEM = Dns.SYSTEM;
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        NetLogger.e("hostname:"+hostname);
        try {
            return SYSTEM.lookup(hostname);
        }catch (Exception e){
            String ip = DNSHelper.getDns(hostname);
            if (!TextUtils.isEmpty(ip)){
                List<InetAddress> inetAddresses = Arrays.asList(InetAddress.getAllByName(ip));
                NetLogger.e("inetAddresses:"+inetAddresses);
                return inetAddresses;
            }
        }
        return SYSTEM.lookup(hostname);
    }
}
