package com.tyganeutronics.ussdandsms.request.ussd.services;

import android.app.AlertDialog;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.tyganeutronics.ussdandsms.UssdAndSmsApplication;
import com.tyganeutronics.ussdandsms.request.RequestApi;

import org.jetbrains.annotations.NotNull;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    public static String TAG = "AccessibilityService";

    @Override
    public void onAccessibilityEvent(@NotNull AccessibilityEvent event) {

        if (UssdAndSmsApplication.Companion.getActive() && UssdAndSmsApplication.Companion.getExpectingUssd()) {

            Log.d(TAG, "onAccessibilityEvent");

            if (!(String.valueOf(event.getClassName()).equals(AlertDialog.class.getName()) || String.valueOf(event.getClassName()).equals(androidx.appcompat.app.AlertDialog.class.getName()))) {
                return;
            }

            AccessibilityNodeInfo nodeInfo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? getRootInActiveWindow() : event.getSource();
            if (nodeInfo == null) {
                return;
            }

            UssdAndSmsApplication.Companion.setNodeInfo(nodeInfo);

            //if not empty
            if (!RequestApi.getUSSD().getResponseText().isEmpty()) {

                getApplicationContext().startService(UssdAndSmsApplication.Companion.getIntent());
            }

        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: ");
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: ");
    }
}
