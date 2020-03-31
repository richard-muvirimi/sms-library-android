package com.tyganeutronics.ussdandsms.request;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.tyganeutronics.ussdandsms.request.sms.api.SmsApi23;
import com.tyganeutronics.ussdandsms.request.sms.api.SmsApiBase;
import com.tyganeutronics.ussdandsms.request.sms.api.SmsApiRequest;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdApiBase;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdApiRequest;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdDialApi16;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdDialApi18;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdDialApi21;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdDialApi23;
import com.tyganeutronics.ussdandsms.request.ussd.api.UssdDialApi26;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public final class RequestApi {

    public static final Integer REQUEST_PERMISSION = 699;

    @NotNull
    public static Boolean isReady(Context context) {
        return isPermissionsReady(context) && getUSSD().isReady(context);
    }

    @NotNull
    public static Boolean isPermissionsReady(Context context) {

        Boolean ready = true;

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            for (String permission : getPermissions()) {
                ready &= context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        return ready;
    }

    @NotNull
    @Contract(" -> new")
    public static SmsApiRequest getSms() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            return new SmsApi23();
        } else {
            return new SmsApiBase();
        }
    }

    @NotNull
    public static String[] getPermissions() {
        ArrayList<String> permissions = new ArrayList<>();

        Collections.addAll(permissions, ((ApiRequest) getUSSD()).getPermissions());
        Collections.addAll(permissions, ((ApiRequest) getSms()).getPermissions());

        return permissions.toArray(new String[]{});
    }

    @Contract(" -> new")
    @NotNull
    public static UssdApiRequest getUSSD() {
        return getUSSD(false);
    }

    @Contract("_ -> new")
    @NotNull
    public static UssdApiRequest getUSSD(Boolean canRespond) {
        if (!canRespond && Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            return new UssdDialApi26();
        } else if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            return new UssdDialApi23();
        } else if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            return new UssdDialApi21();
        } else if (Build.VERSION_CODES.JELLY_BEAN_MR2 <= Build.VERSION.SDK_INT) {
            return new UssdDialApi18();
        } else if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
            return new UssdDialApi16();
        } else {
            return new UssdApiBase();
        }
    }

    public static void rectifyIssue(Activity activity) {
        rectifyPermissionsIssue(activity);
        getUSSD().rectifyIssue(activity);

    }

    public static void rectifyPermissionsIssue(Activity activity) {

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {

            ArrayList<String> permissions = new ArrayList<>();

            for (String permission : getPermissions()) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(permission);
                }
            }

            if (!permissions.isEmpty()) {
                activity.requestPermissions(permissions.toArray(new String[]{}), REQUEST_PERMISSION);
            }
        }

    }

}
