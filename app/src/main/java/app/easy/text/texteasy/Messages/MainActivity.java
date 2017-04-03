package app.easy.text.texteasy.Messages;


import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
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
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.ftinc.scoop.Scoop;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.jinatonic.confetti.CommonConfetti;
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

import app.easy.text.texteasy.About.AboutScreen;
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
    ImageButton speechToText;
    EditText message;
    SmallBang mSmallBang;

    Translator translate;

    private static final String KEY_TEXT_REPLY = "key_text_reply";

    private static MainActivity inst;

    public int lastPosition = 0;

    MaterialDialog mMaterialDialog;

    MaterialStyledDialog mSDialog;

    ExplosionField mExplosionField;

    private MaterialMenuDrawable materialMenu;

    /**
     *
     */
    private Dialog dialog;
    ImageView achievementIcon;

    String CONTACT_NAME;

    RelativeLayout rl;


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


        rl = (RelativeLayout) findViewById(R.id.confettiContain);

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

                    SharedPreferences load = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
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

                    SharedPreferences enter = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
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

        speechToText = (ImageButton) findViewById(R.id.speech_button);

        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        getsms();

        String shared = getIntent().getStringExtra("MessageToPass");

        message.setText(shared);

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

        CommonConfetti.rainingConfetti(rl, new int[] { Color.BLACK, Color.BLUE, Color.MAGENTA })
                .oneShot()
                .setTouchEnabled(true);

        mSDialog = new MaterialStyledDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setDescription(message)
                .setStyle(Style.HEADER_WITH_ICON)
                //.setStyle(Style.HEADER_WITH_TITLE)
                .withDialogAnimation(true)
                .setScrollable(true)
                .setIcon(R.drawable.texteasyicon)
                .setPositiveText("OK")
                .setNegativeText("CANCEL")
                .onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull com.afollestad.materialdialogs.MaterialDialog dialog, @NonNull DialogAction which) {
                        mSDialog.dismiss();
                    }
                })
                .onNegative(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull com.afollestad.materialdialogs.MaterialDialog dialog, @NonNull DialogAction which) {
                        mSDialog.dismiss();
                    }
                })
                .show();
    }


    public void updateList(String message, String defaultMessage, int fromTo) {
        al.add(0, new TextInfo(message, defaultMessage, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
        /**
         *
         */
    }


    public void updateList(String message, String defaultMessage, int fromTo, Date d) {
        al.add(0, new TextInfo(message, defaultMessage, fromTo, d));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
        /**
         *
         */
    }


    public void updateList(String message, String defaultMessage, int fromTo, boolean sent) {
        al.add(new TextInfo(message, defaultMessage, fromTo));
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
        String defaultText;
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
        public TextInfo(String text, String defaultText, int fromTo) {
            this.text = text;
            this.fromTo = fromTo;
            this.defaultText = defaultText;
        }

        public TextInfo(String text, int fromTo, Date dateOfText) {
            this.text = text;
            this.fromTo = fromTo;
            this.dateOfText = dateOfText;
        }

        public TextInfo(String text, String defaultText, int fromTo, Date dateOfText) {
            this.text = text;
            this.fromTo = fromTo;
            this.dateOfText = dateOfText;
            this.defaultText = defaultText;
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

            case R.id.editContact:

                long idContact = getContactID(phoneNumber);
                Intent i = new Intent(Intent.ACTION_EDIT);
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idContact);
                i.setData(contactUri);
                i.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(i);

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

    public long getContactID(String phoneNumber) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor == null) {
            return 0;
        }
        long contactName = 0;
        if (cursor.moveToFirst()) {
            contactName = cursor.getLong(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID));
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
                //TOD: LOOK AT DIS
                Log.w("Text from MainActivity", type + "\t" + text);
                text = translate.translate(text);
                String place;
                if (type.equals("2")) {
                    place = "You: " + text;
                } else {
                    place = getContactName(c.getString(2)) + ": " + text;
                }

                updateList(place, text, Integer.parseInt(type));

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

                    updateList(type + ": " + number + ": " + body, body, q);

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
                        updateList("You: " + translate.translate(result.getString("body")), translate.translate(result.getString("body")), Integer.parseInt(result.getString("type")),d);
                    } else if(result.getString("address").equals("+1"+phoneNumber) && result.getString("type").equals("1")) {
                        updateList(CONTACT_NAME + ": " + translate.translate(result.getString("body")), translate.translate(result.getString("body")), Integer.parseInt(result.getString("type")),d);
                    }

                    result = new JSONObject();

                    c.moveToNext();
                }
            }
            c.close();

            result.put("smslist", jarray);
            //result = new JSONObject(jarray.toString());

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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

        updateList("You: " + translate.translate(message.trim()), translate.translate(message), 2, true);

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

    private final int SPEECH_RECOGNITION_CODE = 1;

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say your text...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    for (int i = 0; i < result.size(); i++) {
                        //Log.d("jhgfds", "onActivityResult: " + result.get(i));
                    }
                    message.setText(message.getText().toString().trim() + " " + text);
                }
                break;
            }
        }
    }


}


