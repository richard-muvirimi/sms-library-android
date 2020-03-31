package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.tyganeutronics.ussdandsms.R;
import com.tyganeutronics.ussdandsms.UssdAndSmsApplication;
import com.tyganeutronics.ussdandsms.request.ApiRequest;
import com.tyganeutronics.ussdandsms.request.ussd.services.AccessibilityService;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class UssdApiBase implements UssdApiRequest, ApiRequest {

    private static final String TAG = "UssdApiBase";

    protected AccessibilityNodeInfo obtainAccessibilityNodeInfo(AccessibilityNodeInfo nodeInfo) {
        try {
            nodeInfo = AccessibilityNodeInfo.obtain(nodeInfo);
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
        return nodeInfo;
    }

    private static Boolean isAccessibilityServiceEnabled(@NotNull Context context, Class<? extends android.accessibilityservice.AccessibilityService> service) {

        boolean accessibilityFound = false;
        int accessibilityEnabled = 0;

        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "Error finding setting, default accessibility not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.d(TAG, "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(TAG, "Setting: " + settingValue);
            if (settingValue != null) {
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    Log.d(TAG, "Setting: " + accessibilityService);
                    Log.d(TAG, "Name: " + context.getPackageName() + File.pathSeparator + service.getName());
                    if (accessibilityService.equalsIgnoreCase(context.getPackageName() + "/" + service.getName())) {
                        Log.d(TAG, "accessibility is switched on!");
                        accessibilityFound = true;
                        break;
                    }
                }
            }

            Log.d(TAG, "***END***");
        } else {
            Log.d(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    private void clickCancelButton() {
        clickCancelButton(UssdAndSmsApplication.Companion.getNodeInfo());

        UssdAndSmsApplication.Companion.setNodeInfo(null);
        UssdAndSmsApplication.Companion.setExpectingUssd(false);
    }

    @NotNull
    private static Boolean isAccessibilityServiceRunning(@NotNull Context context, Class<? extends android.accessibilityservice.AccessibilityService> service) {

        boolean accessibilityFound = false;

        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am != null) {
            List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

            for (AccessibilityServiceInfo enabledService : enabledServices) {
                ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
                if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName())) {
                    accessibilityFound = true;
                    break;
                }
            }
        }

        return accessibilityFound;

    }

    @NotNull
    protected String getResponseText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {

            for (int i = 0; i < nodeInfo.getChildCount(); i++) {

                AccessibilityNodeInfo node = obtainAccessibilityNodeInfo(nodeInfo.getChild(i));
                if (node != null) {

                    if (node.getChildCount() <= 0) {
                        if (node.getClassName().toString().equals(TextView.class.getName())) {
                            return node.getText().toString().trim();
                        }

                    } else {
                        String text = getResponseText(node);

                        //if found return text
                        if (!text.isEmpty()) {

                            return text.trim();
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public Boolean isReady(Context context) {
        return isAccessibilityServiceRunning(context, AccessibilityService.class);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void dial(@NotNull Context context, String code) {
        UssdAndSmsApplication.Companion.setExpectingUssd(true);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(code)));

        Log.d("TAG", Uri.parse("tel:" + Uri.encode(code)).toString());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void rectifyIssue(Activity activity) {

        if (!isReady(activity)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.enable_accessibility_Title);

            if (isAccessibilityServiceEnabled(activity, AccessibilityService.class)) {
                builder.setMessage(R.string.renable_accessibility);
            } else {
                builder.setMessage(R.string.enable_accessibility);
            }

            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(R.string.enable_accessibility_action, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                activity.startActivityForResult(intent, 78);
            });

            AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }
    }

    protected void clickSendButton(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            TypedValue value = new TypedValue();

            Resources.getSystem().getValue("android:string/sms_short_code_confirm_allow", value, true);

            clickAccessibilityButtonWithText(nodeInfo, value.coerceToString().toString());
        }
    }

    @Override
    public String getResponseText() {
        String response = getResponseText(UssdAndSmsApplication.Companion.getNodeInfo());

        return response;
    }

    @NotNull
    private Boolean hasDialogEditText(AccessibilityNodeInfo nodeInfo) {
        return getDialogEditText(nodeInfo) != null;
    }

    @Contract("null -> null")
    protected AccessibilityNodeInfo getDialogEditText(AccessibilityNodeInfo nodeInfo) {

        if (nodeInfo != null) {

            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo node = obtainAccessibilityNodeInfo(nodeInfo.getChild(i));
                if (node != null) {

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
                }
            }
        }
        return null;
    }

    @Override
    public void sendResponse(Context context, String text, Long delay) {
        setDialogEditText(context, UssdAndSmsApplication.Companion.getNodeInfo(), text);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clickSendButton();
        }

    }

    protected void clickCancelButton(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            TypedValue value = new TypedValue();
            if (hasDialogEditText(nodeInfo)) {
                Resources.getSystem().getValue("android:string/cancel", value, true);
            } else {
                Resources.getSystem().getValue("android:string/ok", value, true);
            }
            clickAccessibilityButtonWithText(nodeInfo, value.coerceToString().toString());
        }
    }

    @Override
    public void onDestroy() {
        clickCancelButton();
    }

    private void clickSendButton() {
        clickSendButton(UssdAndSmsApplication.Companion.getNodeInfo());

        UssdAndSmsApplication.Companion.setNodeInfo(null);
        UssdAndSmsApplication.Companion.setExpectingUssd(true);
    }

    protected void setDialogEditText(Context context, AccessibilityNodeInfo nodeInfo, String text) {
        if (nodeInfo != null && hasDialogEditText(nodeInfo)) {

            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager != null) {

                String lastClip = "";
                if (clipboardManager.hasPrimaryClip()) {
                    try {
                        ClipData clipData = clipboardManager.getPrimaryClip();
                        if (clipData != null) {

                            lastClip = clipData.getItemAt(0).coerceToText(context).toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                clipboardManager.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.requestItemMessage, text), text));


                performActionPaste(getDialogEditText(nodeInfo));

                clipboardManager.setPrimaryClip(ClipData.newPlainText(lastClip, lastClip));
            }
        }

    }

    protected void performActionPaste(@NotNull AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE);
    }

    private void clickAccessibilityButtonWithText(AccessibilityNodeInfo nodeInfo, String text) {
        if (nodeInfo != null) {
            try {

                for (AccessibilityNodeInfo node : nodeInfo.findAccessibilityNodeInfosByText(text)) {

                    if (node.getClassName().toString().equals(Button.class.getName())) {
                        performActionClick(node);
                    }
                }

            } catch (IllegalStateException ise) {
                ise.printStackTrace();

                clickAccessibilityButtonWithTextManually(nodeInfo, text);
            }
        }
    }

    private void clickAccessibilityButtonWithTextManually(@NotNull AccessibilityNodeInfo nodeInfo, String text) {

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = obtainAccessibilityNodeInfo(nodeInfo.getChild(i));
            if (node != null) {
                if (node.getChildCount() <= 0) {
                    if (node.getClassName().toString().equals(Button.class.getName()) && node.getText().toString().equals(text)) {
                        performActionClick(node);
                    }
                } else {
                    clickAccessibilityButtonWithTextManually(node, text);
                }
            }
        }
    }

    protected void performActionClick(@NotNull AccessibilityNodeInfo node) {
        node.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
    }

    @Override
    public Boolean canRespond() {
        return hasDialogEditText(UssdAndSmsApplication.Companion.getNodeInfo());
    }

    @Override
    public String[] getPermissions() {
        return new String[0];
    }
}
