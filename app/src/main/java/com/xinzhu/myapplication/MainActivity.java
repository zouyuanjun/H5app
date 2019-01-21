package com.xinzhu.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xinzhu.myapplication.fragment.CameraFragment;
import com.xinzhu.myapplication.fragment.ChangeIocFragment;
import com.xinzhu.myapplication.fragment.H5Fragment;
import com.zhihu.matisse.Matisse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    //隐藏所有fragment
                    hideFragment(transaction);
                    //显示需要显示的fragment
                    transaction.show(h5Fragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    //隐藏所有fragment
                    hideFragment(transaction2);
                    //显示需要显示的fragment
                    transaction2.show(cameraFragment);
                    transaction2.commit();
                    return true;
                case R.id.navigation_notifications:
                    FragmentTransaction transaction3= getSupportFragmentManager().beginTransaction();
                    hideFragment(transaction3);
                    transaction3.show(changeIocFragment);
                    transaction3.commit();
                    return true;
                case R.id.navigation_notifications2:
                    return true;
                case R.id.navigation_notifications3:
                    return true;
            }
            return false;
        }
    };
    H5Fragment h5Fragment;
    CameraFragment cameraFragment;
    ChangeIocFragment changeIocFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
initFragment1();

    }
    //显示第一个fragment
    private void initFragment1(){
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
        if(h5Fragment == null){
            h5Fragment = new H5Fragment();
            transaction.add(R.id.framelayout, h5Fragment);
        }
        if (cameraFragment==null){
            cameraFragment=new CameraFragment();
            transaction.add(R.id.framelayout,cameraFragment);
        }
        if (changeIocFragment==null){
            changeIocFragment=new ChangeIocFragment();
            transaction.add(R.id.framelayout,changeIocFragment);
        }
        hideFragment(transaction);
        transaction.show(h5Fragment);
        transaction.commit();

    }
    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction){
        if(h5Fragment != null){
            transaction.hide(h5Fragment);
        }
        if(cameraFragment != null){
            transaction.hide(cameraFragment);
        }
        if(changeIocFragment != null){
            transaction.hide(changeIocFragment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);   //this
        Log.d("返回结果"+requestCode+resultCode);
        if (requestCode==1){
            cameraFragment.onActivityResult(requestCode, 1, data);
        }else if (requestCode==100){
            cameraFragment.onActivityResult(requestCode, 2, data);
        }else{

        }


    }
}
