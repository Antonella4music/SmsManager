package com.example.antonellab.sms_app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ListSMS extends AppCompatActivity {
    TextView phoneTxt;
    String PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sms);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ListSMS.this, New_Message.class));
                    finish();

                    Snackbar.make(v, getString(R.string.SMS_send), Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.parseColor("#00578a"))
                            .setAction(getString(R.string.send), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // FAB Action goes here
                                }
                            }).show();
                }
            });
        }

        smsList();
    }

    private void smsList() {
        final ListView listView = (ListView) findViewById(R.id.list_view);
        MessagesDBHelper dbHelper = new MessagesDBHelper(this);

        final Cursor c = dbHelper.getSMS();
        String[] columns = {
                "phone",
                "message",
        };

        int[] widgets = {
                R.id.phoneNumberId,
                R.id.messageM,
        };
        final CustomSimpleCursorAdapter adapter = new CustomSimpleCursorAdapter(this,R.layout.message, c, columns, widgets, 0);

        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                phoneTxt = (TextView) view.findViewById(R.id.phoneNumberId);

                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.setSelection(0);

                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor c = (Cursor) parent.getAdapter().getItem(position);
                phoneTxt = (TextView) view.findViewById(R.id.phoneNumberId);
                phoneTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer i = c.getColumnIndex("phone");
                        String nr = c.getString(i);

                        String[] nrArray = nr.split(";");

                        if (nrArray.length > 0) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + nrArray[0]));
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    }
                });
                Intent intent = new Intent(ListSMS.this, Conversation.class);
                intent.putExtra(PHONE, phoneTxt.getText().toString());
                startActivity(intent);
                finish();

            }
        });
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setSelection(0);

    }
}