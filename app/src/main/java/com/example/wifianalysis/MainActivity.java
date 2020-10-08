package com.example.wifianalysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup rg_tab_bar;
    private RadioButton rb_1;
    private RadioButton rb_2;
    private RadioButton rb_3;

    //Fragment Object
    private Fragment1 fg1;
    private Fragment2 fg2;
    private Fragment3 fg3;
    private FragmentManager fManager;

    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    final int mRequestCode = 100;//权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化动态权限
        initPermission();

        fManager = getSupportFragmentManager();
        rg_tab_bar = findViewById(R.id.rg_tab_bar);
        rg_tab_bar.setOnCheckedChangeListener(this);

        rb_1 = findViewById(R.id.rbb_1);
        rb_2 = findViewById(R.id.rbb_2);
        rb_3 = findViewById(R.id.rbb_3);

        //获取第一个单选按钮，并设置其为选中状态
        rb_1.setChecked(true);
        initView();


    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (checkedId){
            case R.id.rbb_1:
                if(fg1 == null){
                    fg1 = new Fragment1();
                    fTransaction.add(R.id.ly_content,fg1, "Fragment1");
                }else{
                    fTransaction.show(fg1);
                }
                break;
            case R.id.rbb_2:
                if(fg2 == null){
                    fg2 = new Fragment2();
                    fTransaction.add(R.id.ly_content,fg2, "Fragment2");
                }else{
                    fTransaction.show(fg2);
                }
                break;
            case R.id.rbb_3:
                if(fg3 == null){
                    fg3 = new Fragment3();
                    fTransaction.add(R.id.ly_content,fg3, "Fragment3");
                }else{
                    fTransaction.show(fg3);
                }
                break;
        }
        fTransaction.commit();
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(fg1 != null)fragmentTransaction.hide(fg1);
        if(fg2 != null)fragmentTransaction.hide(fg2);
        if(fg3 != null)fragmentTransaction.hide(fg3);
    }

    private void initView() {
        //定义底部标签图片大小和位置
        Drawable drawable_home = getResources().getDrawable(R.drawable.tab_home);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_home.setBounds(0, 0, 50, 50);
        //设置图片在文字的哪个方向
        rb_1.setCompoundDrawables(null, drawable_home, null, null);


        Drawable drawable_wifi = getResources().getDrawable(R.drawable.tab_wifi);
        drawable_wifi.setBounds(0, 0, 50, 50);
        rb_2.setCompoundDrawables(null, drawable_wifi, null, null);

        Drawable drawable_tuijian = getResources().getDrawable(R.drawable.tab_me);
        drawable_tuijian.setBounds(0, 0, 50, 50);
        rb_3.setCompoundDrawables(null, drawable_tuijian, null, null);

    }

    private void initPermission() {
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.size() > 0) {
            requestPermissions(permissions, mRequestCode);
        }
    }
}