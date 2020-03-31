package com.tyganeutronics.ussdandsms.request.sms.model;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Sms extends Object implements Parcelable {

    public static final Creator<Sms> CREATOR = new Creator<Sms>() {

        @NotNull
        @Contract("_ -> new")
        public Sms createFromParcel(Parcel in) {
            return new Sms(in);
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        public Sms[] newArray(int size) {
            return new Sms[size];
        }
    };

    private String address;
    private String message;

    private Sms(Parcel in) {
        this();
        if (in != null) {
            setAddress(in.readString());
            setMessage(in.readString());
        }
    }

    private Sms() {
        this("", "");
    }

    public Sms(String address, String message) {
        this.address = address;
        this.message = message;
    }

    public Sms(Bundle bundle) {
        this();

        //---get the SMS message passed in---
        SmsMessage[] msgs;
        if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
            Intent intent = new Intent();
            intent.putExtras(bundle);

            msgs = getSmsMessages(intent);
        } else {

            msgs = getSmsMessages(bundle);
        }

        StringBuilder message = new StringBuilder();
        String from = "";
        for (SmsMessage msg : msgs) {
            if (!TextUtils.isEmpty(msg.getOriginatingAddress())) {
                from = msg.getOriginatingAddress();
            }

            message.append(msg.getMessageBody());
        }

        setAddress(from.trim());
        setMessage(message.toString().trim());

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private SmsMessage[] getSmsMessages(Intent intent) {
        return Telephony.Sms.Intents.getMessagesFromIntent(intent);
    }

    private SmsMessage[] getSmsMessages(@NotNull Bundle bundle) {

        //---get the SMS message passed in---
        SmsMessage[] msgs = new SmsMessage[0];
        //---retrieve the SMS message received---

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus != null) {

            msgs = new SmsMessage[pdus.length];

            for (int j = 0; j < msgs.length; j++) {
                msgs[j] = SmsMessage.createFromPdu((byte[]) pdus[j]);
            }
        }

        return msgs;
    }

    @Contract(pure = true)
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int flags) {
        dest.writeString(getAddress());
        dest.writeString(getMessage());
    }

    @Contract(pure = true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Contract(pure = true)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
