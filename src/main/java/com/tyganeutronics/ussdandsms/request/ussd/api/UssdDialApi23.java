package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.M)
public class UssdDialApi23 extends UssdDialApi21 {

    @Override
    public String[] getPermissions() {
        return new String[]{Manifest.permission.CALL_PHONE};
    }
}
