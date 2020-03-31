package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class UssdDialApi16 extends UssdApiBase {

    @Override
    protected AccessibilityNodeInfo getDialogEditText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            try {
                return nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            } catch (IllegalStateException ise) {
                ise.printStackTrace();

                return super.getDialogEditText(nodeInfo);
            }
        }
        return null;
    }

    @Override
    protected void performActionClick(@NonNull AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
