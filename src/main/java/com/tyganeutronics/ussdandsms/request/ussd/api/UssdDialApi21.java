package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UssdDialApi21 extends UssdDialApi18 {

    @Override
    protected void setDialogEditText(Context context, AccessibilityNodeInfo nodeInfo, String text) {

        if (nodeInfo != null && canRespond()) {
            nodeInfo.refresh();

            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);

            try {
                //set the text
                getDialogEditText(nodeInfo).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);

            } catch (IllegalStateException ise) {
                ise.printStackTrace();

                nodeInfo.refresh();

                //try from clipboard. it's a bit slower than set text
                super.setDialogEditText(context, nodeInfo, text);
            }
        }
    }
}
