package com.xinzhu.myapplication.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xinzhu.myapplication.R;
import com.xinzhu.myapplication.utils.SysUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChangeIocFragment extends Fragment {
    @BindView(R.id.button)
    Button button;
    Unbinder unbinder;
    int tag = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.changeioc_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        if (tag == 1) {
            SysUtils.ChangeIoc(getActivity(), 1);
            tag = 2;
        } else {
            SysUtils.ChangeIoc(getActivity(), 2);
            tag = 1;
        }

    }
}
