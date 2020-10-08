package com.example.wifianalysis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment2 extends Fragment {

    private TextView tvWifiSwitch;
    private Switch mWifiSwitch;

    private WifiManager wifiManager = null;

    private List<Wifi> wifiList = new ArrayList<>();
    private WifiAdapter adapter;
    private RecyclerView recyclerView;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

                if (wifiManager.isWifiEnabled()) {
                    mWifiSwitch.setChecked(true);
                    tvWifiSwitch.setText("已打开");
                    wifiManager.startScan();
                    initView();
                } else {
                    mWifiSwitch.setChecked(false);
                    tvWifiSwitch.setText("已关闭");
                    //recyclerView.removeAllViewsInLayout();
                    wifiList.clear();
                }

            } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                System.out.println("broadcast");

                //每隔2秒扫描一次
                Handler handler=new Handler();
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        wifiManager.startScan();
                    }
                };
                handler.postDelayed(runnable, 2000);

                initView();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_wifi, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWifiSwitch = view.findViewById(R.id.tvWifiSwitch);
        mWifiSwitch = view.findViewById(R.id.wifiSwitch);

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    wifiManager.startScan();
                    initView();
                } else {
                    wifiManager.setWifiEnabled(false);
                    wifiList.clear();
                }
            }
        });
    }

    private void initView() {
        //wifiManager.startScan();// 搜索周围wifi信号
        List<ScanResult> scanResults = wifiManager.getScanResults();
        //System.out.println(scanResults);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            initWifis(scanResults);
        }

        recyclerView = getActivity().findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        WifiAdapter adapter = new WifiAdapter(wifiList);
        recyclerView.setAdapter(adapter);
    }

    private void initWifis(List<ScanResult> scanResults) {
        wifiList.clear();
        for (int i = 0; i < scanResults.size(); i++) {
            ScanResult scanWifi = scanResults.get(i);
            int channel = getChannelByFrequency(scanResults.get(i).frequency);

            Wifi wifi = new Wifi(scanWifi.SSID, scanWifi.BSSID, scanWifi.level, scanWifi.frequency, channel,
                    scanWifi.channelWidth, scanWifi.capabilities, R.drawable.wifi_item);

            wifiList.add(wifi);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(receiver);
    }

    /*根据频率获得信道*/
    public static int getChannelByFrequency(int frequency) {
        int channel = -1;
        switch (frequency) {
            case 2412:
                channel = 1;
                break;
            case 2417:
                channel = 2;
                break;
            case 2422:
                channel = 3;
                break;
            case 2427:
                channel = 4;
                break;
            case 2432:
                channel = 5;
                break;
            case 2437:
                channel = 6;
                break;
            case 2442:
                channel = 7;
                break;
            case 2447:
                channel = 8;
                break;
            case 2452:
                channel = 9;
                break;
            case 2457:
                channel = 10;
                break;
            case 2462:
                channel = 11;
                break;
            case 2467:
                channel = 12;
                break;
            case 2472:
                channel = 13;
                break;
            case 2484:
                channel = 14;
                break;
            case 5745:
                channel = 149;
                break;
            case 5765:
                channel = 153;
                break;
            case 5785:
                channel = 157;
                break;
            case 5805:
                channel = 161;
                break;
            case 5825:
                channel = 165;
                break;
        }
        return channel;
    }

}
