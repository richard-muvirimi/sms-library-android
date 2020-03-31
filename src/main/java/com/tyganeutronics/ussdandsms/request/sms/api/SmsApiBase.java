package com.tyganeutronics.ussdandsms.request.sms.api;import android.app.Activity;import android.app.PendingIntent;import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;import android.content.IntentFilter;import android.telephony.SmsManager;import androidx.annotation.NonNull;import com.tyganeutronics.ussdandsms.UssdAndSmsApplication;import com.tyganeutronics.ussdandsms.request.ApiRequest;import com.tyganeutronics.ussdandsms.request.sms.model.Sms;import java.util.ArrayList;import java.util.Arrays;import java.util.Collections;public class SmsApiBase implements SmsApiRequest, ApiRequest {    public static final String EXTRA_SMS = "com.tyganeutronics.sms";    private static final int RequestCodeSmsSent = 145;    @Override    public Boolean isReady(Context context) {        return true;    }    @Override    public void sendSms(Context context, Sms sms) {        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCodeSmsSent, new Intent(EXTRA_SMS), PendingIntent.FLAG_UPDATE_CURRENT);        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {            @Override            public void onReceive(Context context, Intent i) {                int resultCode = getResultCode();                Integer[] resultCodes = {Activity.RESULT_OK, SmsManager.RESULT_ERROR_GENERIC_FAILURE, SmsManager.RESULT_ERROR_NO_SERVICE, SmsManager.RESULT_ERROR_NULL_PDU, SmsManager.RESULT_ERROR_RADIO_OFF};                if (Arrays.asList(resultCodes).contains(resultCode)) {                    context.getApplicationContext().startService(UssdAndSmsApplication.Companion.getIntent());                    context.getApplicationContext().unregisterReceiver(this);                }            }        }, new IntentFilter(EXTRA_SMS));        sendSms(sms, pendingIntent);    }    private void sendSms(@NonNull Sms sms, PendingIntent pendingIntent) {        SmsManager smsManager = SmsManager.getDefault();        ArrayList<String> parts = smsManager.divideMessage(sms.getMessage());        ArrayList<PendingIntent> pendingIntents = new ArrayList<>(Collections.nCopies(parts.size(), pendingIntent));        smsManager.sendMultipartTextMessage(sms.getAddress(), null, parts, pendingIntents, null);    }    @Override    public void rectifyIssue(Activity activity) {        //do nothing    }    @Override    public String[] getPermissions() {        return new String[0];    }}