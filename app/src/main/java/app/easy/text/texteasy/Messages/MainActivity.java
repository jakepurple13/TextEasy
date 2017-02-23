package app.easy.text.texteasy.Messages;


import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftinc.scoop.Scoop;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;

import app.easy.text.texteasy.About.AboutScreen;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;
import me.drakeet.materialdialog.MaterialDialog;
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

    private static MainActivity inst;

    public int lastPosition = 0;

    MaterialDialog mMaterialDialog;

    ExplosionField mExplosionField;

    /**
     * 
     */
    private Dialog dialog;
    ImageView achievementIcon;

    /**
    /**
     * 
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
     * 
     * @param savedInstanceState 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setThemed();

        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_main);

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
        } catch(NullPointerException e) {

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

        setTitle(getContactName(phoneNumber));

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

        ScanSMS(phoneNumber);

        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.scrollToPosition(al.size() - 1);

        send = (ImageButton) findViewById(R.id.button);
        message = (EditText) findViewById(R.id.editText);

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
            /**
             * 
             * @param v 
             */

                    sendSMS(phoneNumber, message.getText().toString());

                    message.setText("");

                    num++;

                    achievements(num);

                    SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
                            /**
                             * 
                             * @param view 
                             */
                    SharedPreferences.Editor editor = enter.edit();
                    editor.putInt("DotNum", num);
                    editor.apply();

                }
                            /**
                             * 
                             * @param view 
                             */
            }
        });

        send.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * 
             * @param v 
             */
            @Override
            public boolean onLongClick(View v) {

                new TapTargetView.Builder(MainActivity.this) // The activity that hosts this view
                        .title("Send") // Specify the title text
                        .description("Send your text") // Specify the description text
                        .outerCircleColor(R.color.lavender_indigo)
                        .targetCircleColor(R.color.paris_daisy)
                        .cancelable(true)
                        .listener(new TapTargetView.Listener() {
                            /**
                             * 
                             * @param view 
                             */
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                view.dismiss(true);
                            }

                            /**
                             * 
                             * @param view 
                             */
                            @Override
                            public void onTargetLongClick(TapTargetView view) {

                            }
                        })
                        .showFor(send);

                return false;
            }
        });

    }

    /**
     * 
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



    public void updateList(String message, int fromTo, boolean sent) {
        al.add(new TextInfo(message, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
    /**
     * 
     * @param item 
     */
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

        /**
         * 
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
         * 
         * @param text 
         * @param fromTo 
         */
        public TextInfo(String text, int fromTo) {
            this.text = text;
            this.fromTo = fromTo;
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
     * 
     * @param menu 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    /**
     * 
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
     * 
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
     * 
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
                Log.w("Text from MainActivity", text);
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

    /**
     * 
     * @param phoneNumber 
     * @param message 
     */
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);

        updateList("You: " + translate.translate(message), 2, true);
    }

    public void setThemed() {
        SharedPreferences prefs = getSharedPreferences("theming", MODE_PRIVATE);
        String themer = prefs.getString("themeID", "0");
        setTheme(themer.equals("2") ? R.style.NightTheme1 : R.style.LightTheme);
        //boolean ? (if true) : (if false);
    }

}


