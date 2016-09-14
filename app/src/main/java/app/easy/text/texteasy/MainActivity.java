package app.easy.text.texteasy;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<TextInfo> al = new ArrayList<>();

    Button send;
    EditText message;

    String TAG = "MAIN";

    Translator translate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translate = new Translator();

        /**Ask User for Location Premisson and Accounts**/
        AskPermission();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

        String msgData = "";
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {

                /*for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    Log.e("sms", msgData);
                    //al.add(new TextInfo(cursor.getString(2) + ":" + cursor.getString(12)));

                }*/
                //String contactName = getContacts(cursor.getString(2));
                //al.add(new TextInfo(contactName + ": " + cursor.getString(12)));
                al.add(new TextInfo(cursor.getString(2) + ": " + cursor.getString(12)));
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }

        Log.e("smses", msgData);

        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        send = (Button) findViewById(R.id.button);
        message = (EditText) findViewById(R.id.editText);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String contact = "2017854423";

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(contact, null, message.getText().toString(), null, null);

                al.add(new TextInfo(message.getText().toString()));

                mAdapter = new MessageAdapter(al, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);

                message.setText("");
            }
        });




    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

            case 200:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else
                    Toast.makeText(this, "For full app functions these premission are needed", Toast.LENGTH_LONG).show();
                break;
        }

    }




    public void AskPermission() {

        String[] perms = {"android.permission.RECEIVE_SMS","android.permission.WRITE_CONTACTS","android.permission.READ_CONTACTS","android.permission.SEND_SMS","android.permission.READ_SMS"};

        int permsRequestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    /**If the app does have their Permission  dont ask again**/
                    requestPermissions(perms, permsRequestCode);
                }

            }

        }


        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

            Intent intent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    myPackageName);
            startActivity(intent);
            // App is not default.
            // Show the "not currently set as the default SMS app" interface

        } else {
            // App is the default.
            // Hide the "not currently set as the default SMS app" interface

        }

    }




    public class TextInfo {
        String text;

        public TextInfo(String text) {
            this.text = text;
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

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
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



    public String getContacts(String number) {

        if(number.contains("+")) {
            number = number.substring(1);
        }

        HashMap<String, String> contact = new HashMap<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.e("alkdshf ", "Name: " + name + ", Phone No: " + phoneNo);

                        contact.put(phoneNo, name);
                    }
                    pCur.close();
                }
            }
        }



        return contact.get(number);

    }


    public void notification() {

        //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.myrect);
        //   if(high == top) {
        mBuilder.setContentTitle("WE GOT IT!!!");
        mBuilder.setContentText("CONNECTED BABY!");
        //mBuilder.setLights(Color.BLUE, 5000, 1);
        //mBuilder.setLights(Color.MAGENTA, 5000, 1);
        //mBuilder.setLights(Color.rgb(200, 100, 210), 5000, 1);
        //v.vibrate(5*100);
        //    }

        mBuilder.setOnlyAlertOnce(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this,  MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }


}
