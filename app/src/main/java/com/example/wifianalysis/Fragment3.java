package com.example.wifianalysis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class Fragment3 extends Fragment {

    private TextView tvDownload;
    private TextView wifiName, wifiIp, wifiMac, wifiLevel, wifiScore, wifiSpeed;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    private String mac;
    private String score;

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDownload = view.findViewById(R.id.download);
        wifiName = view.findViewById(R.id.wifi_name);
        wifiIp = view.findViewById(R.id.wifi_ip);
        wifiMac = view.findViewById(R.id.wifi_mac);
        wifiLevel = view.findViewById(R.id.wifi_level);
        wifiScore = view.findViewById(R.id.wifi_score);
        wifiSpeed = view.findViewById(R.id.wifi_speed);

        mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();

        Log.i("fangjian", mWifiInfo.toString());

        updateView();
    }

    /*更新视图*/
    private void updateView() {
        wifiName.setText(mWifiInfo.getSSID());
        wifiIp.setText(intToIp(mWifiInfo.getIpAddress()));
        wifiMac.setText(getWlanMac());
        wifiLevel.setText(mWifiInfo.getRssi() + "dBm");
        wifiScore.setText(getScore());
        wifiSpeed.setText(mWifiInfo.getLinkSpeed() + "Mbps");
    }

    @Override
    public void onResume() {
        super.onResume();

        new NetSpeedThread().start();
        new WifiThread().start();
    }

    class NetSpeedThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(2000);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    class WifiThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    /*消息处理，更新数据*/
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tvDownload.setText(getNetSpeed(getContext()));
                    break;
                case 2:
                    mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    mWifiInfo = mWifiManager.getConnectionInfo();
                    updateView();
                default:
                    break;
            }
        }
    };

    /*获取当前网速*/
    public String getNetSpeed(Context context) {

        String netSpeed = "1 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) ==
                TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = speed + " kb/s";
        return netSpeed;
    }

    /*获取Wlan Mac地址*/
    private String getWlanMac() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (interfaces.hasMoreElements()) {
            NetworkInterface iF = interfaces.nextElement();

            byte[] addr = new byte[0];
            try {
                addr = iF.getHardwareAddress();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            if (addr == null || addr.length == 0) {
                continue;
            }

            StringBuilder buf = new StringBuilder();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            mac = buf.toString();
            //Log.d("---mac", "interfaceName=" + iF.getName() + ", mac=" + mac);
        }
        return mac;
    }

    /*获得score*/
    private String getScore() {
        String substring = mWifiInfo.toString().substring(mWifiInfo.toString().indexOf("score: "));
        score = substring.replace("score: ", "");
        return score;
    }

    /*将int型转为IP地址格式*/
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

}
