package com.tyganeutronics.ussdandsms.request.sms.api;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@TargetApi(Build.VERSION_CODES.M)
public final class SmsApi23 extends SmsApiBase {

    @NotNull
    @Override
    @Contract(value = " -> new", pure = true)
    public String[] getPermissions() {
        return new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS};
    }
}
