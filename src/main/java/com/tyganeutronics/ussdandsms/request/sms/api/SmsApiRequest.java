package com.tyganeutronics.ussdandsms.request.sms.api;

import android.app.Activity;
import android.content.Context;

import com.tyganeutronics.ussdandsms.request.sms.model.Sms;

public interface SmsApiRequest {

    Boolean isReady(Context context);

    void sendSms(Context context, Sms sms);

    void rectifyIssue(Activity activity);
}
