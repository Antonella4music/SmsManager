package com.example.antonellab.sms_app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class Conversation extends AppCompatActivity {

    EditText smsInput;
    String phoneNumber;
    ListView listVw;
    String PHONE_NUMBER = "phone";
    int SEND_SMS = 1;
    CustomSimpleCursorAdapter adapter;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent = getIntent();

        phoneNumber = intent.getStringExtra(PHONE_NUMBER);
        smsInput = (EditText) findViewById(R.id.messageEdit);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.conversationToolbar);
        myToolbar.setTitle(phoneNumber);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(R.drawable.back);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Conversation.this, ListSMS.class));
                finish();
            }
        });

        final MessagesDBHelper dbHelper = new MessagesDBHelper(this);
        c = dbHelper.getSMSAfterPhone(phoneNumber);
        listVw = (ListView) findViewById(R.id.list_view);
        String[] columns = {
                "message"
        };

        int[] widgets = {
                R.id.messageC
        };
        adapter = new CustomSimpleCursorAdapter(this,
                R.layout.conversation, c, columns, widgets, 0);

        listVw.setAdapter(adapter);
        listVw.setSelection(adapter.getCount() - 1);

        listVw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int iID = c.getColumnIndex("_id");
                final String valueID = c.getString(iID);
                listVw.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listVw.setSelection(adapter.getCount() - 1);

                return true;
            }
        });
        listVw.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listVw.setSelection(adapter.getCount() - 1);
    }

    public void sendConversationOk(View view) {
        boolean empty = false;
        smsInput.setError(null);
        String message = smsInput.getText().toString();

        if (TextUtils.isEmpty(message)) {
            smsInput.setError(getString(R.string.error_no_sms));
            view = smsInput;
            empty = true;
        }

        if (empty) {
            //Toast.makeText(Conversation.this, getString(R.string.errorNoSMS), Toast.LENGTH_SHORT).show();
            view.requestFocus();
        } else { //send sms
            boolean SMSsent = checkInput(message, phoneNumber, this);
            //boolean SMSsent = sendMessage(message, phoneNumber, this);

            if (!SMSsent) {
                saveMessage(message, phoneNumber, System.currentTimeMillis(), SEND_SMS, this);
                c.requery();
                //finish();
                startActivity(getIntent());
                smsInput.setText("");
                listVw.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            listVw.setSelection(adapter.getCount() - 1);
        }
    }

    public static boolean checkInput(String message, String phone, Context context) {
        String[] array = phone.split(";");
        boolean messageNotSent = false;

        for (String strPhone: array) {
            messageNotSent = sendMessage(strPhone, message, context);
        }
        return messageNotSent;
    }

    public static boolean sendMessage(String phone, String message, Context context) {
        boolean[] messageNotSent = {false};

        String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPending = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPending = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

        SmsManager msg = SmsManager.getDefault();
        msg.sendTextMessage(phone, null, message, sentPending, deliveredPending);

        return messageNotSent[0];
    }

    public static void saveMessage(String phone, String message,  long timestamp, Integer type, Context context){
        MessagesDBHelper dbHelper = new MessagesDBHelper(context);
        dbHelper.insertSms(phone, message, type, timestamp);
    }
}