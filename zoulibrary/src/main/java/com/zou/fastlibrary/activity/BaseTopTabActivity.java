package com.zou.fastlibrary.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zou.fastlibrary.R;
import com.zou.fastlibrary.adapter.ListViewPageAdapter;
import com.zou.fastlibrary.adapter.ViewPagnorAdapter;
import com.zou.fastlibrary.ui.CustomNavigatorBar;

import java.util.ArrayList;

public class BaseTopTabActivity extends BaseActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    String tablist[] = null;
    CustomNavigatorBar customNavigatorBar;
    ArrayList<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basetoptabactivity);
        tabLayout = findViewById(R.id.tb_basetablayout);
        viewPager = findViewById(R.id.vp_baseviewpage);
        customNavigatorBar = findViewById(R.id.appbar);
        customNavigatorBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        context = this;
        tabLayout = findViewById(R.id.tb_basetablayout);
        viewPager = findViewById(R.id.vp_baseviewpage);

    }

    public void inittab(String... tab) {
        this.tablist = tab;
    }

    public void initfragment(Fragment... fragment) {
        for (Fragment fragment1 : fragment) {
            fragmentList.add(fragment1);
        }
    }

    public void bingview() {
        ViewPagnorAdapter listViewPageAdapter = new ViewPagnorAdapter(getSupportFragmentManager(), fragmentList, tablist);
        viewPager.setAdapter(listViewPageAdapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
    }
    public void bingview(int TYPE) {
        ListViewPageAdapter listViewPageAdapter = new ListViewPageAdapter(getSupportFragmentManager(), fragmentList, tablist,TYPE);
        viewPager.setAdapter(listViewPageAdapter);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);
    }

    public TabLayout gettablayout(){
        return  tabLayout;
    }
}
