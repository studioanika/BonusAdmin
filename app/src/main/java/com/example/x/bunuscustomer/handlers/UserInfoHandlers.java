package com.example.x.bunuscustomer.handlers;

import android.view.View;

import com.example.x.bunuscustomer.fragments.FragmentUserInfo;

/**
 * Created by mobi app on 23.10.2017.
 */

public class UserInfoHandlers {

    FragmentUserInfo fragmentUserInfo;

    public UserInfoHandlers(FragmentUserInfo fragmentUserInfo) {
        this.fragmentUserInfo = fragmentUserInfo;
    }

    public void onClickCall(View v){
        fragmentUserInfo.call();
    }
}
