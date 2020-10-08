package com.example.wifianalysis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.wifianalysis.activity.ReceiveFileActivity;
import com.example.wifianalysis.activity.SendFileActivity;

import java.util.ArrayList;
import java.util.List;
import com.example.wifianalysis.R;

/**
 * Wifi P2P 技术并不会访问网络，但会使用到 Java socket 技术
 *
 * 总结：
 * 1、声明权限
 * 1、清单文件注册权限
 * 2、注册Wifi P2P相关广播
 * 3、创建客户端socket，把选择的文件解析成IO流，发送信息
 * 4、创建服务端server，在server内创建服务端socket，监听客户端socket端口，获取信息
 * 5、服务端创建连接的组群信息提供给客户端连接
 * 7、客户端连接信息组群和服务端建立WiFip2p连接
 * 8、客户端通过socket发送文件到服务端serversocket服务端监听到端口后就会获取信息，写入文件。
 */

public class WifiDirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);

    }

    public void sendFile(View v){
        startActivity(new Intent(this, SendFileActivity.class));
    }

    public void receiveFile(View v){
        startActivity(new Intent(this, ReceiveFileActivity.class));
    }
}
