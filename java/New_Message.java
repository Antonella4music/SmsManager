package com.example.antonellab.sms_app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class New_Message extends AppCompatActivity {

    EditText phoneTxt, messageTxt;
    Button sendMessageBtn;
    int SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__message);

        phoneTxt = (EditText) findViewById(R.id.phoneID);
        messageTxt = (EditText) findViewById(R.id.messageID);
        sendMessageBtn = (Button) findViewById(R.id.sendMessageBtn);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.newMessageToolbar);
        myToolbar.setTitle(getString(R.string.type_new_sms));
        setSupportActionBar(myToolbar);
    }

    public void cancelAction(View view)
    {
        Intent i = new Intent(getBaseContext(), ListSMS.class);
        startActivity(i);
    }

    public void sendMessage(View view) {
        Button btn = (Button) findViewById(R.id.sendMessageBtn);

        boolean messageNotSent = checkInput(messageTxt.getText().toString(), phoneTxt.getText().toString(), this);
        if (!messageNotSent) {
            btn.setEnabled(true);

            saveMessage(messageTxt.getText().toString(), phoneTxt.getText().toString(), System.currentTimeMillis(),SEND_SMS, this);
            //yourView.setBackgroundColor(Color.parseColor("#ffffff"));

            phoneTxt.setText("");
            messageTxt.setText("");
            view = phoneTxt;
            view.requestFocus();

            Toast.makeText(New_Message.this, getString(R.string.SMS_sent), Toast.LENGTH_LONG).show();
            Intent i = new Intent(getBaseContext(), ListSMS.class);
            startActivity(i);
            finish();
        }
        else
        {
            btn.setEnabled(false);

            EditText phone = (EditText) findViewById(R.id.phoneID);
            phone.setError(null);
            String phoneStr = phone.getText().toString();

            if (TextUtils.isEmpty(phoneStr)) {
                Toast.makeText(New_Message.this, getString(R.string.error_noPhone), Toast.LENGTH_LONG).show();
                phone.setError(getString(R.string.error_noPhone));
                //view = phone;
            }

        }
    }

    public static boolean checkInput(String message, String phone, Context context) {
        String[] inputArray = phone.split(";");
        boolean messageNotSent = false;

        //loop array
        for ( String strPhone: inputArray ) {
            messageNotSent = sendMessage(message, strPhone, context);
        }
        return messageNotSent;
    }

   private static boolean sendMessage(String message, String phone, final Context context) {
        String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
        int flag = 0;

        final boolean[] messageNotSent = {false};
        PendingIntent sentPending = PendingIntent.getBroadcast(context, 0,new Intent(SENT), flag);
        PendingIntent deliveredPending = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), flag);

        SmsManager msg = SmsManager.getDefault();
        msg.sendTextMessage(phone, null , message, sentPending, deliveredPending);

        return messageNotSent[0];
    }

    public static void saveMessage(String message, String phone, long timestamp, Integer type, Context context){
        MessagesDBHelper dbHelper = new MessagesDBHelper(context);
        dbHelper.insertSms(message, phone, type, timestamp);
    }
}