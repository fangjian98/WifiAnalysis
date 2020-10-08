package com.example.wifianalysis;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.wifianalysis.socket.ReceiveSocket;

/**
 * description: 服务端,用来监听发送过来的文件信息
 */

public class Wifip2pService extends IntentService {

    private static final String TAG = "Wifip2pService";
    private ReceiveSocket mReceiveSocket;

    public Wifip2pService() {
        super("Wifip2pService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {

        public MyBinder() {
            super();
        }
        public void initListener(ReceiveSocket.ProgressReceiveListener listener){
            mReceiveSocket.setOnProgressReceiveListener(listener);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "服务启动了");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mReceiveSocket = new ReceiveSocket();
        mReceiveSocket.createServerSocket();
        Log.e(TAG, "传输完毕");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReceiveSocket.clean();
    }
}
