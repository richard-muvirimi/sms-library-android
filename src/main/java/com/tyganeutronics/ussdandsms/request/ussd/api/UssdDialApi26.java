package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;

import com.tyganeutronics.ussdandsms.UssdAndSmsApplication;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@TargetApi(Build.VERSION_CODES.O)
public class UssdDialApi26 extends UssdDialApi23 {
    private static final String TAG = "UssdDialApi26";

    @Override
    public void dial(@NotNull final Context context, String code) {
        try {
            UssdAndSmsApplication.Companion.setExpectingUssd(true);
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                UssdAndSmsApplication.Companion.setMessage("");

                manager.sendUssdRequest(code, new TelephonyManager.UssdResponseCallback() {

                    @Override
                    public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, final CharSequence response) {
                        super.onReceiveUssdResponse(telephonyManager, request, response);
                        Log.e(TAG, "Success with response : " + response);

                        sendResponseIntent(response.toString());

                    }

                    @Override
                    public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, final int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                        Log.e(TAG, "failed with code " + failureCode);

                        TypedValue value = new TypedValue();

                        Resources.getSystem().getValue("android:string/mmiError", value, true);

                        sendResponseIntent(value.coerceToString().toString());
                    }

                    private void sendResponseIntent(@NotNull String text) {
                        UssdAndSmsApplication.Companion.setMessage(text.trim());
                        UssdAndSmsApplication.Companion.setExpectingUssd(false);

                        getContext().startService(UssdAndSmsApplication.Companion.getIntent());
                    }

                    @Contract(pure = true)
                    private Context getContext() {
                        return context;
                    }
                }, new Handler(Looper.getMainLooper()) {
                });

            } else {
                super.dial(context, code);
            }
        } catch (SecurityException e) {
            e.printStackTrace();

            super.dial(context, code);
        }

    }

    @Override
    public String getResponseText() {
        if (UssdAndSmsApplication.Companion.getNodeInfo() != null) {
            return super.getResponseText();
        }
        return UssdAndSmsApplication.Companion.getMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UssdAndSmsApplication.Companion.setMessage("");
    }
}
