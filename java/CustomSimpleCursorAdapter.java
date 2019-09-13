package com.example.antonellab.sms_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by AntonellaB on 01-Nov-16.
 */

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

    Cursor c;
    Context context;
    int SEND_SMS = 1, RECEIVE_SMS = 2;

    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.c = c;
        this.context = context;
    }

    //getView() va completa campurile din layout (cel defini intr-un XML separat si care descrie un rand
    //dintr-un ListView) cu valorile din Cursor.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //apelam getView din clasa de baza
        View view = super.getView(position, convertView, parent);
        //modificam background-ul pentru view
        if (context instanceof Conversation) {
            TextView dateTxt = (TextView) view.findViewById(R.id.dateC);
            int rowDate = c.getColumnIndex("timestamp");
            String timestamp = c.getString(rowDate);

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(timestamp));
            format.format(calendar.getTime());
            dateTxt.setText(format.format(calendar.getTime()));

            LinearLayout conversationID = (LinearLayout) view.findViewById(R.id.conversationID);
            LinearLayout conversationLayout = (LinearLayout) view.findViewById(R.id.conversationLayout);
            int rowTypeSMS = c.getColumnIndex("type");
            String type = c.getString(rowTypeSMS);

            if (Integer.valueOf(type).equals(SEND_SMS) ) {
                conversationLayout.setGravity(Gravity.RIGHT);
                conversationLayout.setBackgroundColor(Color.parseColor("#c4c4ff"));
//                View someView = view.findViewById(R.id.messageId);
//                someView.setBackgroundColor(Color.parseColor("#ffffff"));
                conversationID.setBackgroundResource(R.drawable.send_drawable);
            } else if (Integer.valueOf(type).equals(RECEIVE_SMS) ) {
                conversationLayout.setGravity(Gravity.LEFT);
                conversationLayout.setBackgroundColor(Color.parseColor("#ff8000"));
                conversationID.setBackgroundResource(R.drawable.receive_drawable);
            }
        } else if (context instanceof ListSMS) {
            TextView date = (TextView) view.findViewById(R.id.dateM);
            int indexDate = c.getColumnIndex("timestamp");
            String timeMessage = c.getString(indexDate);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(timeMessage));
            sdf.format(calendar.getTime());

            date.setText(sdf.format(calendar.getTime()));
        }
        //returnam view-ul modificat
        return view;
    }
}
