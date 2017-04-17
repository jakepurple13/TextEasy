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
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.RemoteInput;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import tyrantgit.explosionfield.ExplosionField;
import xyz.hanks.library.SmallBang;

//Actually displays the texts
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //All of the texts
    ArrayList<TextInfo> al = new ArrayList<>();
    //The phone number
    String phoneNumber;
    //send button
    ImageButton send;
    //speech to text button
    ImageButton speechToText;
    //the message
    EditText message;
    //BANG!
    SmallBang mSmallBang;
    //translator to translate texting acronyms
    Translator translate;
    //this is for replying
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    //an instance of MainActivity used for the adapter
    private static MainActivity inst;
    //last position
    public int lastPosition = 0;
    //A nice looking dialog
    MaterialDialog mMaterialDialog;
    //Another nice looking dialog
    MaterialStyledDialog mSDialog;
    //The drawable for the tool bar
    private MaterialMenuDrawable materialMenu;
    //Contact name
    String CONTACT_NAME;
    //The relative layout
    RelativeLayout rl;
    //A check to see if its the user's first time
    boolean firstTime;
    //the toolbar
    Toolbar toolbar;

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
        //Scoop! Deals with the theme
        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbarsed);
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


        //get the phone number
        phoneNumber = getIntent().getStringExtra("Number");
        try {
            Log.w("Number", phoneNumber);
        } catch (NullPointerException e) {

            Intent intent = getIntent();
            Uri data = intent.getData();
            //Log.d("Data", data.toString());
            phoneNumber = data.toString().substring(6);
            //Log.d("Data", phoneNumber);

        }
        //Normalize the phone number to make it look like an actual phone number
        phoneNumber = PhoneNumberUtils.normalizeNumber(phoneNumber);
        //Log.w("Number", phoneNumber);

        //Set the contact name
        CONTACT_NAME = getContactName(phoneNumber);
        //Change the tool bar text to the contact's name
        setTitle(CONTACT_NAME);
        //initialize our translator
        translate = new Translator(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        //Get our texts!
        getsms();
        //scroll to the bottom
        mRecyclerView.scrollToPosition(al.size() - 1);
        //send button
        send = (ImageButton) findViewById(R.id.button);
        //message
        message = (EditText) findViewById(R.id.editText);
        //when message gains focus, scroll to the bottom of the list
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mRecyclerView.scrollToPosition(al.size() - 1);
            }
        });
        //when message is pressed, scroll to the bottom of the list
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(al.size() - 1);
            }
        });
        //BANG!
        mSmallBang = SmallBang.attach2Window(this);
        //Our OnClickListener for sending a text
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //As long as the text is not ""
                if (!(message.getText().toString().length() < 1)) {
                    //Some banging fun!
                    SharedPreferences load = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
                    int num = load.getInt("DotNum", 0);
                    //if num if greater than 25 than bang 25. Lags phone if its too much
                    if (num >= 25) {
                        mSmallBang.setDotNumber(25);
                    } else {
                        //else, we can bang as long as it's under 25
                        mSmallBang.setDotNumber(num);
                    }
                    //Bang!
                    mSmallBang.bang(send);
                    //Send our text!
                    sendSMS(phoneNumber, message.getText().toString());
                    //Reset message text
                    message.setText("");
                    //Add one to num
                    num++;
                    //check if we are at a landmark
                    achievements(num);
                    //update our num variable
                    SharedPreferences enter = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = enter.edit();
                    editor.putInt("DotNum", num);
                    editor.apply();

                }
            }
        });

        //speech to text button
        speechToText = (ImageButton) findViewById(R.id.speech_button);
        //set the OnClickListener
        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
        //are we sharing anything?
        String shared = getIntent().getStringExtra("MessageToPass");
        //set the message text to what we want to share
        message.setText(shared);

        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                firstTimeHere();
                return false;
            }
        });

        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        firstTime = load.getBoolean("FirstTimeOptions", true);

        ViewTreeObserver vto = rl.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //We are now sure the view is drawn and should be able to do what you wanted:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(rl,InputMethodManager.SHOW_IMPLICIT);

                if(firstTime) {
                    firstTimeHere();
                }

            }
        });

    }

    View mi;

    public void firstTimeHere() {
        /*//The beautiful dialog to show
            mSDialog = new MaterialStyledDialog.Builder(MainActivity.this)
                    .setTitle("More Options")
                    .setDescription("Click on a text to see the date of the text. " +
                            "Click and hold to Copy the text, Read the text Out Loud,
                            or Share with the Facebook Messenger")
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
                    .show();*/

        FancyShowCaseView speech = new FancyShowCaseView.Builder(MainActivity.this)
                .focusOn(speechToText)
                .title("Speech to Text")
                //.showOnce("speechtotextonce")
                .titleGravity(Gravity.BOTTOM)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        FancyShowCaseView sent = new FancyShowCaseView.Builder(MainActivity.this)
                .focusOn(send)
                .title("Send Your Text\nAlso Press and Hold here to see this again.")
                //.showOnce("sendtextonce")
                .titleGravity(Gravity.BOTTOM)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        FancyShowCaseView messages = new FancyShowCaseView.Builder(MainActivity.this)
                .focusOn(message)
                .title("Your Message to Send")
                //.showOnce("messagetextonce")
                .titleGravity(Gravity.END | Gravity.CENTER)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        FancyShowCaseView tool = new FancyShowCaseView.Builder(MainActivity.this)
                .focusOn(toolbar)
                .title("Here you can Call or Edit this Contact")
                //.showOnce("toolbaronce")
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        FancyShowCaseView.Builder textedBuild;

        if(al.size()!=0) {
            textedBuild = new FancyShowCaseView.Builder(MainActivity.this)
                    .focusOn(mRecyclerView.findViewHolderForAdapterPosition(al.size()-1).itemView);
        } else {
            textedBuild = new FancyShowCaseView.Builder(MainActivity.this)
                    .focusOn(mRecyclerView);

        }

        FancyShowCaseView texted = textedBuild.title("This is where the text messages will go.\n" +
                "Click on a text to see the date of the text.\n" +
                "Click and hold to bring some Options up.")
                /*"\nYou can:\n Copy the text.\nRead the text Out Loud.\n" +
                "Or Share with the Facebook Messenger.")*/
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                //.showOnce("textmessageonce")
                .build();

        new FancyShowCaseQueue()
                .add(speech)
                .add(sent)
                .add(messages)
                .add(tool)
                .add(texted)
                .show();

        SharedPreferences enter = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = enter.edit();

        editor.putBoolean("FirstTimeOptions", false);
        editor.apply();
    }

    /*
    * achievements
    * YAY!
    * num - (int) - the number of texts sent
    */
    public void achievements(int num) {

        //Log.e("Amount", num + "");
        //number for calculations
        int amount;
        //digits in num
        int digits = String.valueOf(num).length();

        //Log.e("asd", digits + "");
        //ten
        int tens = 10;
        //number of digits
        for (int i = 1; i < digits; i++) {
            tens *= 10;
        }

        //Log.d("dak;sfj", "achievements: " + tens);
        //if we are under tens
        if (num <= tens) {
            //get the mod of num
            amount = num % tens;
        } else {
            //else amount = num
            amount = num;
        }

        //Log.d("dak;sfj", "amount: " + amount);
        //if num == 1
        if (num == 1) {
            //First text! YAY!
            anotherAchieve("Congrats!", "You just sent your first text! Celebrate!");
        } else if (amount % (tens / 10) == 0 && !(amount < 10)) {
            //every ten or hundred or thousand, etc
            anotherAchieve("Milestone Reached!", "You've sent your " + num + "th text!");
        }

    }

    /*
    * anotherAchieve
    * summon the ACHIEVEMENT DIALOG!
    * title - (String) - the title
    * message - (String) - the message
    */
    public void anotherAchieve(String title, String message) {
        //CONFETTI!
        CommonConfetti.rainingConfetti(rl, new int[] { Color.BLACK, Color.BLUE, Color.MAGENTA })
                .oneShot()
                .setTouchEnabled(true);

        //The beautiful dialog to show
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

    /*
    * updateList
    * updates the recyclerview
    * message - (String) - the message to show
    * defaultMessage - (String) - the message without transaltion
    * fromTo - (int) - 1 or 0. This is whether its from someone or to them.
    * from == 1
    * to == 2
    */
    public void updateList(String message, String defaultMessage, int fromTo) {
        al.add(0, new TextInfo(message, defaultMessage, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }

    /*
    * updateList
    * updates the recyclerview
    * message - (String) - the message to show
    * defaultMessage - (String) - the message without transaltion
    * fromTo - (int) - 1 or 0. This is whether its from someone or to them.
    * d - (Date) - the date the text was sent
    * from == 1
    * to == 2
    */
    public void updateList(String message, String defaultMessage, int fromTo, Date d) {
        al.add(0, new TextInfo(message, defaultMessage, fromTo, d));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }

    /*
    * updateList
    * updates the recyclerview
    * message - (String) - the message to show
    * defaultMessage - (String) - the message without transaltion
    * fromTo - (int) - 1 or 0. This is whether its from someone or to them.
    * d - (Date) - the date the text was sent
    * sent - (Boolean) - for if the user sends it
    * from == 1
    * to == 2
    */
    public void updateList(String message, String defaultMessage, int fromTo, boolean sent) {
        al.add(new TextInfo(message, defaultMessage, fromTo));
        mAdapter = new MessageAdapter(al, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(al.size() - 1);
    }
    //To keep track of the texts
    public class TextInfo {
        //the actual text
        String text;
        int fromTo;
        //1 is from
        //2 is to
        //the default
        String defaultText;
        //the date of the text
        Date dateOfText;
        //constructor
        public TextInfo(String text) {
            this.text = text;
            fromTo = 1;
        }
        //constructor
        public TextInfo(String text, String defaultText, int fromTo) {
            this.text = text;
            this.fromTo = fromTo;
            this.defaultText = defaultText;
        }
        //constructor
        public TextInfo(String text, int fromTo, Date dateOfText) {
            this.text = text;
            this.fromTo = fromTo;
            this.dateOfText = dateOfText;
        }
        //constructor
        public TextInfo(String text, String defaultText, int fromTo, Date dateOfText) {
            this.text = text;
            this.fromTo = fromTo;
            this.dateOfText = dateOfText;
            this.defaultText = defaultText;
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.back_to_contacts, R.anim.from_contacts);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

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
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    /*
    * getContactName
    * gets the contacts name
    * phoneNumber - (String) - the contacts phone number
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
    /*
    * getContactName
    * gets the contacts name
    * phoneNumber - (String) - the contacts phone number
    */
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

    /*
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
    }*/

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

    //SENDS THE SMS
    private void sendSMS(String phoneNumber, String message) {

        String SENT = "SENT_SMS_ACTION";
        String DELIVERED = "DELIVERED_SMS_ACTION";

        Intent sentIntent = new Intent(SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);

        Intent deliveryIntent = new Intent(DELIVERED);
        PendingIntent deliver = PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliver);
        //Updates the recyclerview with the new message
        updateList("You: " + translate.translate(message.trim()), translate.translate(message), 2, true);

        //Change menu icon back to arrow
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //ANIMATIONS! YAY!
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


