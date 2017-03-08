package app.easy.text.texteasy.Messages;


import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.ftinc.scoop.Scoop;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import app.easy.text.texteasy.ContactList.ContactAdapter;
import app.easy.text.texteasy.ContactList.Contacts;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;
import me.drakeet.materialdialog.MaterialDialog;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import tyrantgit.explosionfield.ExplosionField;
import xyz.hanks.library.SmallBang;

/**
 *
 */

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<TextInfo> al = new ArrayList<>();
    String phoneNumber;
    ImageButton send;
    EditText message;
    SmallBang mSmallBang;

    Translator translate;

    private static final String KEY_TEXT_REPLY = "key_text_reply";

    private static MainActivity inst;

    public int lastPosition = 0;

    MaterialDialog mMaterialDialog;

    ExplosionField mExplosionField;

    private MaterialMenuDrawable materialMenu;

    /**
     *
     */
    private Dialog dialog;
    ImageView achievementIcon;

    String CONTACT_NAME;


    /**
     * /**
     */
    public static MainActivity instance() {
        return inst;
    }


    @Override
    public void onStart() {
        super.onStart();
        /**
         *
         * @param savedInstanceState
         */
        inst = this;
    }


    /**
     *
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setThemed();

        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsed);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle your drawable state here
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK);
                onBackPressed();
            }
        });

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationIcon(materialMenu);


        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Hi")
                .setMessage("Hello")
                .setCanceledOnTouchOutside(true)
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });


       /* Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
        List<ResolveInfo> infos = getPackageManager().queryBroadcastReceivers(intent, 0);
        for (ResolveInfo info : infos) {
            System.out.println("Receiver name:" + info.activityInfo.name + "; priority=" + info.priority);
        }*/

        phoneNumber = getIntent().getStringExtra("Number");
        try {
            Log.w("Number", phoneNumber);
        } catch (NullPointerException e) {

            Intent intent = getIntent();
            Uri data = intent.getData();
            Log.d("Data", data.toString());
            phoneNumber = data.toString().substring(6);
            Log.d("Data", phoneNumber);

        }
        //phoneNumber = phoneNumber.replaceAll("(", " ");
        phoneNumber = PhoneNumberUtils.normalizeNumber(phoneNumber);
        //phoneNumber = phoneNumber.replaceAll("\\^([0-9]+)", "");
        Log.w("Number", phoneNumber);

        CONTACT_NAME = getContactName(phoneNumber);

        setTitle(CONTACT_NAME);

        translate = new Translator(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        /**
         *
         * @param v
         */
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //ScanSMS(phoneNumber);

        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.scrollToPosition(al.size() - 1);

        send = (ImageButton) findViewById(R.id.button);
        message = (EditText) findViewById(R.id.editText);

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mRecyclerView.scrollToPosition(al.size() - 1);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(al.size() - 1);
            }
        });

        mSmallBang = SmallBang.attach2Window(this);

        send.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                if (!(message.getText().toString().length() < 1)) {

                    SharedPreferences load = getPreferences(Context.MODE_PRIVATE);
                    int num = load.getInt("DotNum", 0);

                    if (num >= 25) {
                        mSmallBang.setDotNumber(25);
                    } else {
                        mSmallBang.setDotNumber(num);
                    }

                    mSmallBang.bang(send);

                    sendSMS(phoneNumber, message.getText().toString());

                    message.setText("");

                    num++;

                    achievements(num);

                    SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = enter.edit();
                    editor.putInt("DotNum", num);
                    editor.apply();

                }
            }
        });

        send.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                TapTargetView.showFor(MainActivity.this,
                        TapTarget.forView(send, "Send", "Send your text\nPress and hold to bring up again")
                                .cancelable(true)
                                .drawShadow(true)
                                .tintTarget(true)
                                //.icon(R.drawable.sendbutton)
                                .transparentTarget(false)
                                .outerCircleColor(R.color.primary)
                                .targetCircleColor(R.color.primary_dark),
                        new TapTargetView.Listener() {
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                view.dismiss(true);
                            }

                            @Override
                            public void onTargetLongClick(TapTargetView view) {

                            }
                        });

                return false;
            }
        });


        getsms();

    }

    /**
     * @param num
     */
    public void achievements(int num) {

        Log.e("Amount", num + "");
        /**
         *
         * @param title
         * @param message
         */

        int amount;

        int digits = String.valueOf(num).length();

        Log.e("asd", digits + "");

        int tens = 10;

        for (int i = 1; i < digits; i++) {
            tens *= 10;
        }

        Log.d("dak;sfj", "achievements: " + tens);

        /**
         *
         * @param v
         */
        if (num <= tens) {
            amount = num % tens;
        } else {
            amount = num;
        }

        Log.d("dak;sfj", "amount: " + amount);

        if (num == 1) {

            anotherAchieve("Congrats!", "You just sent your first text! Celebrate!");

        } else if (amount % (tens / 10) == 0 && !(amount < 10)) {

            /**
             *
             * @param message
             * @param fromTo
             */
            anotherAchieve("Milestone Reached!", "You've sent your " + num + "th text!");

        }

    }


    public void anotherAchieve(String title, String message) {


        mMaterialDialog.setTitle(title);
        mMaterialDialog.setMessage(message);
        mMaterialDialog.show();


        /*dialog = new Dialog(this);
    *//**
         *
         *//*

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.achievement_window);
        *//**
         *
         * @param text
         *//*
        dialog.setTitle(title);

        achievementIcon = (ImageView) dialog.findViewById(R.id.achieveicon);
        TextView tv = (TextView) dialog.findViewById(R.id.achievetexet);

        Button closeButton = (Button) dialog.findViewById(R.id.achievebutton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        tv.setText(message);

        dialog.show();

        mSmallBang.bang(achievementIcon);*/

        /**
         *
         */
    }


    public void updateList(String message, int fromTo) {
        al.add(0, new TextInfo(message, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
        /**
         *
         */
    }


    public void updateList(String message, int fromTo, Date d) {
        al.add(0, new TextInfo(message, fromTo, d));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
        /**
         *
         */
    }


    public void updateList(String message, int fromTo, boolean sent) {
        al.add(new TextInfo(message, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }

    /**
     *
     */
    public class TextInfo {
        String text;
        int fromTo; //1 is from
        //2 is to

        Date dateOfText;

        /**
         * @param text
         */
        public TextInfo(String text) {
            this.text = text;
            fromTo = 1;
        }
        /**
         *
         * @param phoneNumber
         */

        /**
         * @param text
         * @param fromTo
         */
        public TextInfo(String text, int fromTo) {
            this.text = text;
            this.fromTo = fromTo;
        }

        public TextInfo(String text, int fromTo, Date dateOfText) {
            this.text = text;
            this.fromTo = fromTo;
            this.dateOfText = dateOfText;
        }

        /**
         *
         */
        @Override
        public String toString() {
            return text;
        }

    }
    /**
     *
     * @param number
     */

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     *
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.back_to_contacts, R.anim.from_contacts);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    /**
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call:

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);

                return true;

            default:
                /**
                 *
                 * @param phoneNumber
                 * @param message
                 */
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    /**
     * @param phoneNumber
     */
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

    /**
     * @param number
     */
    public void ScanSMS(String number) {
        System.out.println("==============================ScanSMS()==============================");
        //Initialize Box
        Uri uri = Uri.parse("content://sms");

        String[] proj = {"*"};
        ContentResolver cr = this.getContentResolver();
        String whereAddress = "address = '" + number + "'";
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

                String address = c.getString(c.getColumnIndex("address"));
                String person = c.getString(c.getColumnIndex("person"));
                String date = c.getString(c.getColumnIndex("date"));
                String protocol = c.getString(c.getColumnIndex("protocol"));
                String read = c.getString(c.getColumnIndex("read"));
                String status = c.getString(c.getColumnIndex("status"));
                String type = c.getString(c.getColumnIndex("type"));
                String subject = c.getString(c.getColumnIndex("subject"));
                String body = c.getString(c.getColumnIndex("body"));


                String sms = "Address: " + address +
                        "\nPerson: " + person +
                        "\nDate: " + date +
                        "\nProtocol: " + protocol +
                        "\nRead: " + read +
                        "\nStatus: " + status +
                        "\nType: " + type +
                        "\nSubject: " + subject +
                        "\nBody: " + body;

                //Log.i("SMS text", sms);

                Log.e("Person?", person + "HERE");

                String text = body;
                //TODO: LOOK AT DIS
                Log.w("Text from MainActivity", type + "\t" + text);
                text = translate.translate(text);
                String place;
                if (type.equals("2")) {
                    place = "You: " + text;
                } else {
                    place = getContactName(c.getString(2)) + ": " + text;
                }

                updateList(place, Integer.parseInt(type));

            } while (c.moveToNext());
        }
        c.close();
    }

    public void getAllSms(Context context) {

        ContentResolver cr = context.getContentResolver();
        String[] proj = {"*"};
        String whereAddress = "address = '" + phoneNumber + "'";
        Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    Date dateFormat = new Date(Long.valueOf(smsDate));
                    String type = null;
                    switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                        case Telephony.Sms.MESSAGE_TYPE_INBOX:
                            type = "inbox";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_SENT:
                            type = "sent";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                            type = "outbox";
                            break;
                        default:
                            break;
                    }

                    int q = 0;

                    if (type.equals("inbox")) {
                        q = 1;
                    } else if (type.equals("outbox")) {
                        q = 2;
                    } else if (type.equals("sent")) {
                        q = 2;
                    }

                    updateList(type + ": " + number + ": " + body, q);

                    c.moveToNext();
                }
            }
        } else {
            Toast.makeText(this, "No message to show!", Toast.LENGTH_SHORT).show();
        }
    }

    public JSONObject getsms() {
        JSONObject result = null;
        JSONArray jarray = null;
        String link[] = {"content://sms/inbox", "content://sms/sent", "content://sms/draft"};

        try {

            jarray = new JSONArray();

            result = new JSONObject();
            Uri uri = Uri.parse("content://sms/");
            String whereAddress = "address = '" + phoneNumber + "'";
            Cursor c = getContentResolver().query(uri, null, null, null, null);


            // Read the sms data and store it in the list
            if (c.moveToFirst()) {

                for (int i = 0; i < c.getCount(); i++) {

                    result.put("body", c.getString(c.getColumnIndexOrThrow("body")).toString());

                    result.put("date", c.getString(c.getColumnIndexOrThrow("date")).toString());
                    result.put("read", c.getString(c.getColumnIndexOrThrow("read")).toString());
                    result.put("type", c.getString(c.getColumnIndexOrThrow("type")).toString());
                    if ((c.getString(c.getColumnIndexOrThrow("type")).toString()).equals("3")) {
                        //Cursor cur= getContentResolver().query("", null, null ,null,null);
                        //startManagingCursor(cur);

                        String threadid = c.getString(c.getColumnIndexOrThrow("thread_id")).toString();
                        Cursor cur = getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), null, "_id =" + threadid, null, null);

                        if (cur.moveToFirst()) {
                            String recipientId = cur.getString(cur.getColumnIndexOrThrow("recipient_ids")).toString();
                            cur = getContentResolver().query(Uri.parse("content://mms-sms/canonical-addresses"), null, "_id = " + recipientId, null, null);
                            if (cur.moveToFirst()) {
                                String address = cur.getString(cur.getColumnIndexOrThrow("address")).toString();
                                result.put("address", address);
                                cur.close();
                            }
                        }

                    } else {
                        result.put("address", c.getString(c.getColumnIndexOrThrow("address")).toString());
                    }
                    jarray.put(result);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(Long.parseLong(result.getString("date")));
                    Log.w("cal", cal.toString());
                    Date d = cal.getTime();

                    if(result.getString("address").equals(phoneNumber) && result.getString("type").equals("2")) {
                        updateList("You: " + translate.translate(result.getString("body")), Integer.parseInt(result.getString("type")),d);
                    } else if(result.getString("address").equals("+1"+phoneNumber) && result.getString("type").equals("1")) {
                        updateList(CONTACT_NAME + ": " + translate.translate(result.getString("body")), Integer.parseInt(result.getString("type")),d);
                    }

                    result = new JSONObject();

                    c.moveToNext();
                }
            }
            c.close();

            result.put("smslist", jarray);
            //result = new JSONObject(jarray.toString());

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("MainActivity", result.toString());

        return result;
    }

    /**
     * @param phoneNumber
     * @param message
     */
    private void sendSMS(String phoneNumber, String message) {

        String SENT = "SENT_SMS_ACTION";
        String DELIVERED = "DELIVERED_SMS_ACTION";

        Intent sentIntent = new Intent(SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);

        Intent deliveryIntent = new Intent(DELIVERED);
        PendingIntent deliver = PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliver);

        updateList("You: " + translate.translate(message), 2, true);

        //Change menu icon back to arrow
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
            }
        }, 2500);

        //change menu icon to a check showing that the text was sent
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK);

    }

    public void setThemed() {
        SharedPreferences prefs = getSharedPreferences("theming", MODE_PRIVATE);
        String themer = prefs.getString("themeID", "0");
        setTheme(themer.equals("2") ? R.style.NightTheme1 : R.style.LightTheme);
        //boolean ? (if true) : (if false);
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

}


