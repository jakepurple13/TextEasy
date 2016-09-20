package app.easy.text.texteasy;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<TextInfo> al = new ArrayList<>();
    String phoneNumber;
    Button send;
    EditText message;

    Translator translate;

    private static MainActivity inst;

    public int lastPosition = 0;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       /* Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
        for (ResolveInfo info : infos) {
            System.out.println("Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
        }*/


        phoneNumber = getIntent().getStringExtra("Number");
        Log.w("Number", phoneNumber);
        //phoneNumber = phoneNumber.replaceAll("(", " ");
        phoneNumber = PhoneNumberUtils.normalizeNumber(phoneNumber);
        //phoneNumber = phoneNumber.replaceAll("\\^([0-9]+)", "");
        Log.w("Number", phoneNumber);

        setTitle(getContactName(phoneNumber));

        translate = new Translator();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ScanSMS();

        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.scrollToPosition(al.size() - 1);

        send = (Button) findViewById(R.id.button);
        message = (EditText) findViewById(R.id.editText);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendSMS(phoneNumber, message.getText().toString());

                message.setText("");
            }
        });

        //enableBroadcastReceiver(send);

    }

    public void updateList(String message) {
        al.add(0, new TextInfo(message));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }

    public void updateList(String message, int fromTo) {
        al.add(0, new TextInfo(message, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }


    public void updateList(String message, int fromTo, boolean sent) {
        al.add(new TextInfo(message, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }


    public void enableBroadcastReceiver(View view) {

        /*ComponentName receiver = new ComponentName(this, SmsReceiver.class); //created SMSLog class above!
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);*/
    }


    public class TextInfo {
        String text;
        int fromTo; //1 is from
        //2 is to

        public TextInfo(String text) {
            this.text = text;
            fromTo = 1;
        }

        public TextInfo(String text, int fromTo) {
            this.text = text;
            this.fromTo = fromTo;
        }

        @Override
        public String toString() {
            return text;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public String getContactName(String phoneNumber) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }


    public void ScanMMS() {
        System.out.println("==============================ScanMMS()==============================");
        //Initialize Box
        Uri uri = Uri.parse("content://mms");
        String[] proj = {"*"};
        ContentResolver cr = getContentResolver();
        String whereAddress = "address = '" + phoneNumber + "'";
        Cursor c = cr.query(uri, proj, whereAddress, null, null);

        if (c.moveToFirst()) {
            do {
                /*String[] col = c.getColumnNames();
                String str = "";
                for(int i = 0; i < col.length; i++) {
                    str = str + col[i] + ": " + c.getString(i) + ", ";
                }
                System.out.println(str);*/
                //System.out.println("--------------------MMS------------------");
                Msg msg = new Msg(c.getString(c.getColumnIndex("_id")));
                msg.setThread(c.getString(c.getColumnIndex("thread_id")));
                msg.setDate(c.getString(c.getColumnIndex("date")));
                msg.setAddr(getMmsAddr(msg.getID()));


                ParseMMS(msg);

                String text = translate.translate(c.getString(12));
                updateList(c.getString(2) + ": " + text);
                //System.out.println(msg);
            } while (c.moveToNext());
        }

        c.close();

    }


    public void ParseMMS(Msg msg) {
        Uri uri = Uri.parse("content://mms/part");
        String mmsId = "mid = " + msg.getID();
        Cursor c = getContentResolver().query(uri, null, mmsId, null, null);
        while (c.moveToNext()) {
/*          String[] col = c.getColumnNames();
            String str = "";
            for(int i = 0; i < col.length; i++) {
                str = str + col[i] + ": " + c.getString(i) + ", ";
            }
            System.out.println(str);*/

            String pid = c.getString(c.getColumnIndex("_id"));
            String type = c.getString(c.getColumnIndex("ct"));
            if ("text/plain".equals(type)) {
                msg.setBody(msg.getBody() + c.getString(c.getColumnIndex("text")));
            } else if (type.contains("image")) {
                msg.setImg(getMmsImg(pid));
            }

        }
        c.close();
        return;
    }

    public void ScanSMS() {
        System.out.println("==============================ScanSMS()==============================");
        //Initialize Box
        Uri uri = Uri.parse("content://sms");
        //Uri uri = Uri.parse("content://mms-sms/complete-conversations");

        String[] proj = {"*"};
        ContentResolver cr = getContentResolver();
        String whereAddress = "address = '" + phoneNumber + "'";
        Cursor c = cr.query(uri, proj, whereAddress, null, "date desc limit 20");

        if (c.moveToFirst()) {
            do {
                String[] col = c.getColumnNames();
                String str = "";
                for (int i = 0; i < col.length; i++) {
                    str = str + col[i] + ": " + c.getString(i) + ", ";
                }
                //System.out.println(str);

                System.out.println("--------------------SMS------------------");

                Msg msg = new Msg(c.getString(c.getColumnIndex("_id")));
                msg.setDate(c.getString(c.getColumnIndex("date")));
                msg.setAddr(c.getString(c.getColumnIndex("Address")));
                msg.setBody(c.getString(c.getColumnIndex("body")));
                msg.setDirection(c.getString(c.getColumnIndex("type")));
                msg.setContact(c.getString(c.getColumnIndex("person")));
                System.out.println(msg);
                String text = msg.body;//c.getString(12);
                Log.w("ASD", text);
                text = translate.translate(text);
                String place;
                if (c.getString(c.getColumnIndex("type")).equals("2")) {
                    place = "You: " + text;
                } else {
                    place = getContactName(c.getString(2)) + ": " + text;
                }

                updateList(place, Integer.parseInt(c.getString(c.getColumnIndex("type"))));

            } while (c.moveToNext());
        }
        c.close();
    }

    public Bitmap getMmsImg(String id) {
        Uri uri = Uri.parse("content://mms/part/" + id);
        InputStream in = null;
        Bitmap bitmap = null;

        try {
            in = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(in);
            if (in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public String getMmsAddr(String id) {
        String sel = new String("msg_id=" + id);
        String uriString = MessageFormat.format("content://mms/{0}/addr", id);
        Uri uri = Uri.parse(uriString);
        Cursor c = getContentResolver().query(uri, null, sel, null, null);
        String name = "";
        while (c.moveToNext()) {
/*          String[] col = c.getColumnNames();
            String str = "";
            for(int i = 0; i < col.length; i++) {
                str = str + col[i] + ": " + c.getString(i) + ", ";
            }
            System.out.println(str);*/
            String t = c.getString(c.getColumnIndex("address"));
            if (!(t.contains("insert")))
                name = name + t + " ";
        }
        c.close();
        return name;
    }


    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        updateList("You: " + message, 2, true);
    }

    public class Msg {
        private String id;
        private String t_id;
        private String date;
        private String dispDate;
        private String addr;
        private String contact;
        private String direction;
        private String body;
        private Bitmap img;
        private boolean bData;
        //Date vdat;

        public Msg(String ID) {
            id = ID;
            body = "";
        }

        public void setDate(String d) {
            date = d;
            dispDate = msToDate(date);
        }

        public void setThread(String d) {
            t_id = d;
        }

        public void setAddr(String a) {
            addr = a;
        }

        public void setContact(String c) {
            if (c == null) {
                contact = "Unknown";
            } else {
                contact = c;
            }
        }

        public void setDirection(String d) {
            if ("1".equals(d))
                direction = "FROM: ";
            else
                direction = "TO: ";

        }

        public void setBody(String b) {
            body = b;
        }

        public void setImg(Bitmap bm) {
            img = bm;
            if (bm != null)
                bData = true;
            else
                bData = false;
        }

        public String getDate() {
            return date;
        }

        public String getDispDate() {
            return dispDate;
        }

        public String getThread() {
            return t_id;
        }

        public String getID() {
            return id;
        }

        public String getBody() {
            return body;
        }

        public Bitmap getImg() {
            return img;
        }

        public boolean hasData() {
            return bData;
        }

        public String toString() {

            String s = id + ". " + dispDate + " - " + direction + " " + contact + " " + addr + ": " + body;
            if (bData)
                s = s + "\nData: " + img;
            return s;
        }

        public String msToDate(String mss) {

            long time = Long.parseLong(mss, 10);

            long sec = (time / 1000) % 60;
            time = time / 60000;

            long min = time % 60;
            time = time / 60;

            long hour = time % 24 - 5;
            time = time / 24;

            long day = time % 365;
            time = time / 365;

            long yr = time + 1970;

            day = day - (time / 4);
            long mo = getMonth(day);
            day = getDay(day);

            mss = String.valueOf(yr) + "/" + String.valueOf(mo) + "/" + String.valueOf(day) + " " + String.valueOf(hour) + ":" + String.valueOf(min) + ":" + String.valueOf(sec);

            return mss;
        }

        public long getMonth(long day) {
            long[] calendar = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            for (int i = 0; i < 12; i++) {
                if (day < calendar[i]) {
                    return i + 1;
                } else {
                    day = day - calendar[i];
                }
            }
            return 1;
        }

        public long getDay(long day) {
            long[] calendar = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            for (int i = 0; i < 12; i++) {
                if (day < calendar[i]) {
                    return day;
                } else {
                    day = day - calendar[i];
                }
            }
            return day;
        }


    }


}
