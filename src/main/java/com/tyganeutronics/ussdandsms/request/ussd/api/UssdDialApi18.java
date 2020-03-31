package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class UssdDialApi18 extends UssdDialApi16 {

    @NotNull
    @Override
    protected String getResponseText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {

            nodeInfo.refresh();

            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo node = obtainAccessibilityNodeInfo(nodeInfo.getChild(i));
                if (node != null) {
                    node.refresh();

                    if (node.getChildCount() <= 0) {
                        if (node.getClassName().toString().equals(TextView.class.getName())) {
                            return node.getText().toString().trim();
                        }

                    } else {
                        String text = getResponseText(node);

                        //if found return node info
                        if (!text.isEmpty()) {
                            return text.trim();
                        }
                    }

                    node.recycle();
                }
            }
        }
        return "";
    }

    @Override
    protected void clickSendButton(AccessibilityNodeInfo nodeInfo) {
        if (!clickAccessibilityButtonWithId(nodeInfo, Resources.getSystem().getResourceName(android.R.id.button1))) {
            super.clickSendButton(nodeInfo);
        }
    }

    @Override
    protected void clickCancelButton(AccessibilityNodeInfo nodeInfo) {
        if (!clickAccessibilityButtonWithId(nodeInfo, Resources.getSystem().getResourceName(android.R.id.button2))) {
            super.clickCancelButton(nodeInfo);
        }
    }

    @Override
    protected void performActionPaste(@NotNull AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }

    @NotNull
    private Boolean clickAccessibilityButtonWithIdManually(@NotNull AccessibilityNodeInfo nodeInfo, String id) {

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = obtainAccessibilityNodeInfo(nodeInfo.getChild(i));
            if (node != null) {

                node.refresh();

                if (node.getChildCount() <= 0) {
                    if (node.getClassName().toString().equals(Button.class.getName()) && node.getViewIdResourceName().equals(id)) {
                        performActionClick(node);
                        return true;
                    }
                } else {
                    if (clickAccessibilityButtonWithIdManually(node, id)) {
                        return true;
                    }
                }

                node.recycle();
            }
        }
        return false;
    }

    @NotNull
    private Boolean clickAccessibilityButtonWithId(AccessibilityNodeInfo nodeInfo, String id) {
        if (nodeInfo != null) {

            nodeInfo.refresh();

            try {

                for (AccessibilityNodeInfo node : nodeInfo.findAccessibilityNodeInfosByViewId(id)) {
                    node.refresh();

                    if (node.getClassName().toString().equals(Button.class.getName())) {
                        performActionClick(node);
                        return true;
                    }

                    node.recycle();
                }

            } catch (IllegalStateException ise) {
                ise.printStackTrace();

                return clickAccessibilityButtonWithIdManually(nodeInfo, id);
            }
        }
        return false;
    }

    @Override
    protected AccessibilityNodeInfo getDialogEditText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            try {

                nodeInfo.refresh();

                return nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);

            } catch (IllegalStateException ise) {
                ise.printStackTrace();

                nodeInfo.refresh();

                for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                    AccessibilityNodeInfo node = obtainAccessibilityNodeInfo(nodeInfo.getChild(i));
                    if (node != null) {
                        node.refresh();

                        if (node.getChildCount() <= 0) {
                            if (node.getClassName().toString().equals(EditText.class.getName())) {
                                return node;
                            }

                        } else {
                            AccessibilityNodeInfo info = getDialogEditText(node);

                            //if found return node info
                            if (info != null) {
                                return info;
                            }
                        }

                        node.recycle();
                    }
                }
            }
        }
        return null;
    }
}
