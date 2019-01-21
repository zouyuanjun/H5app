package com.zou.fastlibrary.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

  private List<Fragment> fragmentList;

  public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
      super(fm);
      this.fragmentList = fragmentList;
  }

  @Override
  public int getCount() {
      return fragmentList.size();
  }

  @Override
  public Fragment getItem(int position) {
   //   pagelistener.sendposition(position);

      Fragment fragment = null;
      if (position < fragmentList.size()) {
          fragment = fragmentList.get(position);
      } else {
          fragment = fragmentList.get(0);
      }
      Bundle bundle = new Bundle();
      bundle.putInt("POSITION",position);
      fragment.setArguments(bundle);
      return fragment;
  }
  public interface Viewpageposition{
      void sendposition(int position);
  }
  private Viewpageposition pagelistener;
  public void setpagelistener( Viewpageposition pagelistener){
      this.pagelistener=pagelistener;
  }


}
