package com.example.ivolunteerschedscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

public class OTP_Receiver extends BroadcastReceiver {


    private  static EditText editText;

    public void setEditText(EditText editText)
    {
        com.example.ivolunteerschedscanner.OTP_Receiver.editText=editText;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        //message will be holding complete sms that is received
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for(SmsMessage sms : messages)
        {

            String msg = sms.getMessageBody();
            // here we are spliting the sms using " : " symbol
            String otp_msg = msg;
            String digits = otp_msg.replaceAll("[^0-9]", "");
            editText.setText(digits.trim());
        }

    }
}
