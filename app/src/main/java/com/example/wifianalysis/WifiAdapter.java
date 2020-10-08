package com.example.wifianalysis;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    final int WIFICIPHER_NOPASS = 1;
    final int WIFICIPHER_WEP = 2;
    final int WIFICIPHER_WPA = 3;

    private List<Wifi> mWifiList;
    private WifiManager mWifiManager;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wifiView;
        ImageView wifiImage;
        View wifiLine;

        TextView wifiSSID;
        TextView wifiBSSID;
        TextView wifiLevel;
        TextView wifiChannel;
        TextView wifiChannelWidth;
        TextView wifiFrequency;
        TextView wifiCapabilities;

        public ViewHolder(View view) {
            super(view);

            wifiView = view;
            wifiImage = view.findViewById(R.id.wifi_image);
            wifiLine = view.findViewById(R.id.wifi_line);

            wifiSSID = view.findViewById(R.id.wifi_ssid);
            wifiBSSID = view.findViewById(R.id.wifi_bssid);
            wifiLevel = view.findViewById(R.id.wifi_level);
            wifiChannel = view.findViewById(R.id.wifi_channel);
            wifiChannelWidth = view.findViewById(R.id.wifi_channelWidth);
            wifiFrequency = view.findViewById(R.id.wifi_frequency);
            wifiCapabilities = view.findViewById(R.id.wifi_capabilies);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = holder.getAdapterPosition();
                final Wifi wifi = mWifiList.get(position);

                final EditText editText = new EditText(v.getContext());
                editText.setSingleLine();
                editText.setHint("请输入密码");

                mWifiManager = (WifiManager) v.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                final WifiConfiguration wifiConfiguration = new WifiConfiguration();
                /*WiFi的名称和密码配置需要加引号*/
                wifiConfiguration.SSID = "\"" + wifi.getSsid() + "\"";

                String capabilities = wifi.getCapablities();
                int type = WIFICIPHER_WPA;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        type = WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        type = WIFICIPHER_WEP;
                    } else {
                        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        int netId = mWifiManager.addNetwork(wifiConfiguration);
                        mWifiManager.enableNetwork(netId, true);
                    }
                }

                final int finalType = type;
                new AlertDialog.Builder(v.getContext())
                        .setTitle(wifi.getSsid())
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .setView(editText)
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int netId = mWifiManager.addNetwork(createWifiConfig(wifi.getSsid(), editText.getText().toString(), finalType));
                                mWifiManager.enableNetwork(netId, true);

                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });

        return holder;
    }


    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                Log.e("preSharedKey",config.preSharedKey);
                return config;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wifi wifi = mWifiList.get(position);
        holder.wifiImage.setImageResource(wifi.getImageId());

        holder.wifiSSID.setText(wifi.getSsid());
        holder.wifiBSSID.setText(wifi.getBssid());
        holder.wifiLevel.setText(String.valueOf(wifi.getLevel()));
        holder.wifiChannel.setText(String.valueOf(wifi.getChannel()));
        holder.wifiChannelWidth.setText(String.valueOf(wifi.getChannelWidth()));
        holder.wifiFrequency.setText(String.valueOf(wifi.getFrequency()));
        holder.wifiCapabilities.setText(wifi.getCapablities());
    }

    @Override
    public int getItemCount() {
        return mWifiList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public WifiAdapter(List<Wifi> wifiList) {
        mWifiList = wifiList;
    }

}
