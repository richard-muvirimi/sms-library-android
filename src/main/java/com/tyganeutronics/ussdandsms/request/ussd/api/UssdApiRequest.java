package com.tyganeutronics.ussdandsms.request.ussd.api;

import android.app.Activity;
import android.content.Context;

public interface UssdApiRequest {

    Boolean isReady(Context context);

    void dial(Context context, String code);

    void rectifyIssue(Activity activity);

    String getResponseText();

    void onDestroy();

    void sendResponse(Context context, String text, Long delay);

    Boolean canRespond();

}
