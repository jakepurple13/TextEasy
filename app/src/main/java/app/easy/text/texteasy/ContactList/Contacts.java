package app.easy.text.texteasy.ContactList;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.ftinc.scoop.Scoop;
import com.ftinc.scoop.ui.ScoopSettingsActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListener;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.viethoa.RecyclerViewFastScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import app.easy.text.texteasy.Dictionary.ListOfWords;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Settings.Settings1Activity;
import app.easy.text.texteasy.About.AboutScreen;
import app.easy.text.texteasy.Tester.AboutAndCont;
import app.easy.text.texteasy.Tester.BlankTestingActivity;
import app.easy.text.texteasy.Translator;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import me.drakeet.materialdialog.MaterialDialog;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.Data;

/**
 *
 */

/**
 *
 */
public class Contacts extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    public static final String NOTIFICATION_REPLY = "notification_reply";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerViewFastScroller fastScroller;
    IndexFastScrollRecyclerView alphabetScroller;

    FloatingSearchView fsv;

    ArrayList<ContactInfo> al = new ArrayList<>();
    Translator translate;// = new Translator(this);
    EditText searchBar;
    String searchKey = "";
    ArrayList<ContactInfo> searched;
    ProgressTask pt;

    boolean firstTimeAddContact = false;
    /**
     * @param savedInstanceState
     */
    boolean firstTimeSearch = false;
    FloatingActionButton fab;

    Dialog feed;

    int lastTheme = -1;

    String currentTheme;

    String listOfNames = "";

    MaterialSearchBar searchBars;

    BoomMenuButton bmb;

    MaterialDialog mMaterialDialog;

    public String messageToPass = "";

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //currentTheme = setThemed();

        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8950844463555971~6055866848");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        translate = new Translator(this);

        pt = new ProgressTask(Contacts.this);
        pt.execute();

        searched = new ArrayList<>();
        /**Ask User for Location Premisson and Accounts**/
        //AskPermission();




        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //String syncConnPref = sharedPref.getString(SettingsActivity., "");
        //setTheme(getApplicationInfo().getThemeId());
        //System.err.println(PreferenceManager.getDefaultSharedPreferences(this).getString("defaultTheme", "0"));

        mMaterialDialog = new MaterialDialog(Contacts.this)
                .setTitle("MaterialDialog")
                .setMessage("Hello world!")
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




        SharedPreferences load = getPreferences(Context.MODE_PRIVATE);
        firstTimeAddContact = load.getBoolean("add contact", false);

        firstTimeSearch = load.getBoolean("search", false);



        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {

                //Add here

                if (!firstTimeAddContact) {

                    //setTutorial("Add Contact", "Add a contact", fab);

                    SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = enter.edit();
                    editor.putBoolean("add contact", true);
                    editor.apply();

                    firstTimeAddContact = true;

                } else {

                    // Creates a new Intent to insert a contact
                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    // Sets the MIME type to match the Contacts Provider
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    startActivity(intent);

                    //al.clear();
                    /**
                     *
                     * @param v
                     */
                    //readContacts();
                    Collections.sort(al, new InfoCompare());
                    mAdapter = new ContactAdapter(al, Contacts.this, listOfNames);
                    alphabetScroller.setAdapter(mAdapter);

                }

            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             *
             * @param v
             */
            @Override
            public boolean onLongClick(View v) {

                setTutorial("Add Contact", "Add a contact", fab);

                return false;
            }
        });

        fab.setVisibility(View.GONE);

        //---------------BOOM---------------------

        bmb = (BoomMenuButton) findViewById(R.id.bmb);

        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);

        HamButton.Builder contactAdd = new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_add)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        // Sets the MIME type to match the Contacts Provider
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                        startActivity(intent);
                        Collections.sort(al, new InfoCompare());
                        mAdapter = new ContactAdapter(al, Contacts.this, listOfNames);
                        alphabetScroller.setAdapter(mAdapter);
                    }
                })
                .normalText("Add a Contact")
                .subNormalText("Met a new friend? Add their Contact info!");

        bmb.addBuilder(contactAdd);

        HamButton.Builder settings = new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_preferences)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent settingsIntent = new Intent(Contacts.this, Settings1Activity.class);
                        startActivityForResult(settingsIntent, 201);
                    }
                })
                .normalText("Settings")
                .subNormalText("Go to the Settings Menu");

        bmb.addBuilder(settings);

        HamButton.Builder sendAText = new HamButton.Builder()
                .normalImageRes(android.R.drawable.sym_action_chat)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        mMaterialDialog.setTitle("WIP");
                        mMaterialDialog.setMessage("WIP");
                        mMaterialDialog.show();
                    }
                })
                .normalText("Send a Text")
                .subNormalText("Send a text to a number that's not in your contacts");

        //bmb.addBuilder(sendAText);

        HamButton.Builder phoneCall = new HamButton.Builder()
                .normalImageRes(android.R.drawable.ic_menu_call)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        startActivity(intent);
                    }
                })
                .normalText("Call Someone");

        bmb.addBuilder(phoneCall);

        HamButton.Builder groupChat = new HamButton.Builder()
                .normalImageRes(android.R.drawable.sym_action_chat)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        mMaterialDialog.setTitle("Group Chat");
                        mMaterialDialog.setMessage("Coming Soon!");
                        mMaterialDialog.show();
                    }
                })
                .normalText("Set up a Group Chat")
                .subNormalText("Coming Soon!");

        //bmb.addBuilder(groupChat);

        bmb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                setTutorial("See More Options", "Add a Contact\nCall Someone\nGroup Chat", bmb);
                return false;
            }
        });

        //---------------BOOM---------------------
        /**
         *
         * @param s
         * @param start
         * @param count
         * @param after
         */


       /* FloatingActionButton feedback = (FloatingActionButton) findViewById(R.id.feedback);

        feedback.setVisibility(View.GONE);

        feedback.setBackgroundTintList(getResources().getColorStateList(R.color.lavender_indigo));

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                feed = new Dialog(Contacts.this);

                feed.requestWindowFeature(Window.FEATURE_NO_TITLE);
                *//**
                 *
                 * @param phoneNumber
                 *//*
                feed.setCancelable(true);
                feed.setContentView(R.layout.feed_back);

                Button send = (Button) feed.findViewById(R.id.send);

                Button cancel = (Button) feed.findViewById(R.id.dontsend);

                final SimpleRatingBar srb = (SimpleRatingBar) feed.findViewById(R.id.ratingBar);

                final EditText comments = (EditText) feed.findViewById(R.id.commentfield);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        feed.dismiss();
                    }
                });

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String text = "";



                    }
                });

                feed.show();

            }
        });*/


        alphabetScroller = (IndexFastScrollRecyclerView) findViewById(R.id.contactScroller);

        alphabetScroller.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        alphabetScroller.setLayoutManager(mLayoutManager);

        searchBars = (MaterialSearchBar) findViewById(R.id.searchBars);

        searchBars.setTextColor(R.color.black);

        searchBars.setSpeechMode(false);

        //searchBars.setHint("Search");

        //searchBars.setPlaceHolder("Search");

        searchBars.setMaxSuggestionCount(5);

        searchBars.inflateMenu(R.menu.contact_activity_menu);

        searchBars.getMenu().setOnMenuItemClickListener(this);

        searchBars.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i3, int i1, int i2) {

                searched.clear();
                searchKey = s.toString();
                System.out.println(searchKey);
                for (int i = 0; i < al.size(); i++) {
                    if (al.get(i).name.toUpperCase().contains(searchKey.toUpperCase()) ||
                            al.get(i).number.contains(searchKey)) {
                        searched.add(al.get(i));
                    }

                }

                mAdapter = new ContactAdapter(searched, Contacts.this, listOfNames);
                alphabetScroller.setAdapter(mAdapter);

                List<String> suggestions = new ArrayList<>();

                if(searched.size()!=0) {

                    for (int i = 0; i < 5 && i<searched.size(); i++) {
                        suggestions.add(searched.get(i).name);
                    }

                    searchBars.updateLastSuggestions(suggestions);


                    if(s.length()==0) {
                        searchBars.hideSuggestionsList();
                    } else {
                        //searchBars.showSuggestionsList();
                    }

                } else {
                    searchBars.hideSuggestionsList();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBars.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                searchBars.hideSuggestionsList();
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchBars.hideSuggestionsList();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                searchBars.hideSuggestionsList();
            }
        });

        searchBars.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                setTutorial("Search", "Search for a contact to find", searchBars);
                return false;
            }
        });

        /*searchBar = (EditText) findViewById(R.id.search);

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searched.clear();
                searchKey = s.toString();
                System.out.println(searchKey);
                for (int i = 0; i < al.size(); i++) {
                    if (al.get(i).name.toUpperCase().contains(searchKey.toUpperCase()) ||
                            al.get(i).number.contains(searchKey)) {
                        searched.add(al.get(i));
                    }
                }

                mAdapter = new ContactAdapter(searched, Contacts.this, listOfNames);
                alphabetScroller.setAdapter(mAdapter);
            }

            *//**
             *
             * @param s
             *//*
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                setTutorial("Search", "Search for a contact to find", searchBar);
                return false;
            }
        });
*/

        try {
            //readContacts();
        } catch (Exception e) {

        }

        alphabetScroller.requestFocus();

        themeChanged();

        String upAgain = "\nPress and hold to bring up again";

        if (!firstTimeSearch) {

            SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = enter.edit();
            editor.putBoolean("search", true);
            editor.apply();

            firstTimeSearch = true;

            /*new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.searchBars), "Search", "Search for a contact to find" + upAgain)
                                    .targetCircleColor(R.color.deep_orange_50)
                                    .outerCircleColor(R.color.accent)
                                    .textColor(R.color.white)
                                    .descriptionTextColor(R.color.white)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(R.id.bmb), "Menu", "Want to\nAdd a Contact? Call Someone? Start a Group Chat?" + upAgain)
                                    .targetCircleColor(R.color.deep_orange_50)
                                    .outerCircleColor(R.color.accent)
                                    .textColor(R.color.white)
                                    .descriptionTextColor(R.color.white)
                                    .cancelable(false),
                            TapTarget.forView(findViewById(R.id.toolbar), "Settings", "Change the Theme and Go into Settings")
                                    .targetCircleColor(R.color.deep_orange_50)
                                    .outerCircleColor(R.color.accent)
                                    .textColor(R.color.white)
                                    .descriptionTextColor(R.color.white)
                                    .cancelable(false))
                    .listener(new TapTargetSequence.Listener() {
                        // This listener will tell us when interesting(tm) events happen in regards
                        // to the sequence
                        @Override
                        public void onSequenceFinish() {
                            // Yay
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget) {
                            // Perfom action for the current target
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // Boo
                        }
                    }).start();*/

        }

        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();
        try {
            // Figure out what to do based on the intent type
            if (intent.getType().equals("text/plain")) {
                // Handle intents with text ...
                messageToPass = intent.getStringExtra(Intent.EXTRA_TEXT);
                mMaterialDialog.setTitle("Share");
                mMaterialDialog.setMessage("Pick a Contact to share with");
                mMaterialDialog.show();
            }
        } catch(NullPointerException e) {
            Log.e("Contacts Line 183", "onCreate: " + e);
        }

    }



    public void getTexts() {
        TelephonyProvider tp = new TelephonyProvider(this);

        Data<Sms> ds = tp.getSms(TelephonyProvider.Filter.ALL);


        List<Sms> ls = ds.getList();

        for(int i=0;i<ls.size();i++) {

            Log.e(ls.get(i).address, ls.get(i).body);

        }


    }


    public String getsms(String phoneNumber) {
        JSONObject result = null;
        JSONArray jarray = null;
        String link[] = {"content://sms/inbox", "content://sms/sent", "content://sms/draft"};

        String text = " ";


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

                    if(result.getString("address").equals(phoneNumber) && result.getString("type").equals("2")) {
                        text = result.getString("body");
                    } else if(result.getString("address").equals("+1"+phoneNumber) && result.getString("type").equals("1")) {
                        text = result.getString("body");
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

        return text;
    }


    public void getAllContacts() {
        long startnow;
        long endnow;
        //getTexts();
        Uri uris = Uri.parse("content://sms");
        String[] proj = {"body", "address"};

        startnow = android.os.SystemClock.uptimeMillis();
        ContentResolver cr = getContentResolver();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                selection,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //Log.d("con ", "name " + contactName + " number" + contactNumber);

            String text = " ";

            /*Uri myMessage = Uri.parse("content://sms/");

            ContentResolver cr1 = getContentResolver();
            Cursor c = cr1.query(myMessage, new String[] { "_id",
                            "address", "date", "body", "read" }, null,
                    null, null);
            getSmsLogs(c, this);

            String num = sms_num.get(sms_num.size()-1);
            String body = sms_body.get(sms_num.size()-1);

            Log.d(num, body);*/

            /*String whereAddress = "address = '" + contactNumber + "'";
            Cursor c = cr.query(uris, proj, whereAddress, null, "date desc limit 1");
            //Log.wtf("QWE", c.getCount() + "");

            if (c.isNull(0) == false) {
                do {
                    text = c.getString(c.getColumnIndex("body"));
                    Log.w("ASD", text);
                    text = translate.translate(text);
                } while (c.moveToNext());
                text = c.getString(c.getColumnIndex("body"));
                Log.w("ASD", text);
                text = translate.translate(text);
            }*/

            //text = body;
            //text = translate.translate(text);

            al.add(new ContactInfo(contactName, contactNumber, text));
            //c.close();

            //al.add(new ContactInfo(contactName, contactNumber, "Hello"));

            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;

        endnow = android.os.SystemClock.uptimeMillis();
        Log.d("END", "TimeForContacts " + (endnow - startnow) + " ms");

        getFaceBookStuff();

        ArrayList<String> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < al.size(); i++) {
            String name = al.get(i).name;
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(word);
            }
        }

        for(int i=0;i<mAlphabetItems.size();i++) {
            listOfNames+=mAlphabetItems.get(i);
        }



        SharedPreferences enter = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = enter.edit();
        editor.putInt("numOfContacts", al.size());
        editor.apply();

    }

    public static final ArrayList<String> sms_num = new ArrayList<String>();
    public static final ArrayList<String> sms_body = new ArrayList<String>();


    public void getSmsLogs(Cursor c, Context con) {

        if (sms_num.size() > 0) {
            sms_num.clear();
            sms_body.clear();
        }

        try {

            if (c.moveToFirst()) {
                do {
                    Log.d("error", "" + c.getString(c.getColumnIndexOrThrow("address")));
                    if (c.getString(c.getColumnIndexOrThrow("address")) == null) {
                        c.moveToNext();
                        continue;
                    }

                    String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                    String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();

                    sms_num.add(Number);

                    sms_body.add(Body);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @param title
     * @param description
     * @param v
     */

    public void setTutorial(String title, String description, View v) {

        /*TapTargetView.showFor(this,
                TapTarget.forView(v, title, description + "\nPress and hold to bring up again")
                        .cancelable(true)
                        .drawShadow(true)
                        .tintTarget(true)
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
                });*/

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            /*case R.id.wordChange:

                Intent callIntent = new Intent(this, ListOfWords.class);
                startActivity(callIntent);

                return true;*/

            case R.id.themes:

                Intent settings = ScoopSettingsActivity.createIntent(this, "Settings");
                startActivityForResult(settings, 201);

                return true;

            case R.id.settings:

                Intent settingsIntent = new Intent(this, Settings1Activity.class);
                startActivityForResult(settingsIntent, 201);

                return true;

            case R.id.testPage:

                Intent tester = new Intent(this, BlankTestingActivity.class);
                tester.putExtra("contactNum", al.size());
                startActivity(tester);

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
     */
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private Dialog dialog;
        private Contacts activity;
        private Context context;


        /**
         * @param activity
         */
        public ProgressTask(Contacts activity) {
            this.activity = activity;
            context = activity;
            dialog = new Dialog(context);
        }


        /**
         * /**
         */

        @Override
        protected Boolean doInBackground(String... params) {
            /**
             *
             */
            activity.getAllContacts();

            return null;
        }
        /**
         *
         * @param name
         * @param number
         * @param text
         */

        /**
         * application context.
         */


        protected void onPreExecute() {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            /**
             *
             * @param phoneNumber
             */
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialogload);

            ProgressBar mProgress = (ProgressBar) dialog.findViewById(R.id.load_and_wait);

            int[] colors = {
                    Color.rgb(255, 238, 85),
                    Color.rgb(242, 157, 58),
                    Color.rgb(198, 62, 62),
                    Color.rgb(159, 83, 242)
            };

            mProgress.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(Contacts.this)
                    .colors(colors)
                    .build());

            if(dialog!=null) {
                dialog.dismiss();
            } else {
                dialog.show();
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            dialog.hide();
            if(dialog!=null) {
                dialog.dismiss();
            }
            dialog.dismiss();
            Collections.sort(al, new InfoCompare());
            mAdapter = new ContactAdapter(al, Contacts.this, listOfNames);

            alphabetScroller.setAdapter(mAdapter);

            if(al.size()<=1) {
                mMaterialDialog.setTitle("Achievement Unlocked!");
                mMaterialDialog.setMessage("Forever Alone!\nCongrats! Here is the emotional support number for you!");
                mMaterialDialog.setPositiveButton("Call for support", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:18007842433"));
                        startActivity(callIntent);
                    }
                });
                mMaterialDialog.show();
            }
        }


    }

    /**
     *
     */
    public class InfoCompare implements Comparator<ContactInfo> {
        public int compare(ContactInfo e1, ContactInfo e2) {
            return e1.name.compareTo(e2.name);
        }
    }

    public class ContactInfo {
        String name;
        String text;
        String number;
        boolean facebook;

        public ContactInfo(String name, String number, String text) {
            this.name = name;
            this.number = PhoneNumberUtils.normalizeNumber(number);
            this.text = text;
            facebook = false;
        }

        public ContactInfo(String name, String number, String text, boolean facebook) {
            this.name = name;
            this.number = PhoneNumberUtils.normalizeNumber(number);
            this.text = text;
            this.facebook = facebook;
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
                return phoneNumber;
            }
            String contactName = null;
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            if(contactName==null) {
                contactName = phoneNumber;
            }

            return contactName;
        }


        /**
         *
         */
        @Override
        public String toString() {
            return name + ": " + PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry()) + "\n" + text;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_activity_menu, menu);
        return true;
    }

    /**
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.wordChange:

                Intent callIntent = new Intent(this, ListOfWords.class);
                startActivity(callIntent);

                return true;*/

            case R.id.themes:

                launchSettings();

                return true;

            case R.id.settings:

                Intent settingsIntent = new Intent(this, Settings1Activity.class);
                startActivityForResult(settingsIntent, 201);

                return true;

            /*case R.id.testPage:

                Intent tester = new Intent(this, FloatingActionTester.class);
                startActivity(tester);

                return true;*/

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
     * @param permsRequestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     *
     */
    public void AskPermission() {

        String[] perms = {"android.permission.RECEIVE_SMS", "android.permission.WRITE_CONTACTS", "android.permission.READ_CONTACTS", "android.permission.SEND_SMS", "android.permission.READ_SMS"};

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


    public void themeChanged() {

        /*int backColor = currentTheme.equals("2") ? R.color.charcoal : R.color.lavender_indigo;

        fab.setBackgroundTintList(getResources().getColorStateList(backColor));

        int iconColor = currentTheme.equals("2") ? R.color.white : R.color.apple_green;

        fab.getDrawable().mutate().setTint(ContextCompat.getColor(this, iconColor));

        int indexBarColor = currentTheme.equals("2") ? R.color.charcoal : R.color.redred;

        alphabetScroller.setIndexBarColor(getColored(indexBarColor));

        int indexBarTextColor = R.color.white;//currentTheme.equals("2") ? R.color.white : R.color.yellow;

        alphabetScroller.setIndexBarTextColor(getColored(indexBarTextColor));*/
        if(getThemeId() == R.style.Theme_NightTheme_DayNight_NightMODE) {
            fab.getDrawable().mutate().setTint(ContextCompat.getColor(this, R.color.white));
        }
    }


    public int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            Log.w("themeid", e.toString());
        }
        return 0;
    }

    public String getColored(int resource) {
        //resource - int - an id from the R.color file
        return "#"+Integer.toHexString(getResources().getColor(resource));
        //"#"+Integer.toHexString(getResources().getColor(R.color.blue))
    }


    public String setThemed() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String themer = prefs.getString("themeID", "0");

        int numTheme = Integer.parseInt(themer);

        setTheme(numTheme==2 ? R.style.BlueTheme : R.style.LightTheme);
        //boolean ? (if true) : (if false);
        return themer+"";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 201) {

            //recreate();

        }

        recreate();

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int selectedTheme = Integer.parseInt(prefs.getString("themeID", "-1"));

        //Log.e("sdlaf;jk", "selectedTheme: " + selectedTheme + " lastTheme: " + lastTheme);

        if ((lastTheme != -1) && (lastTheme != selectedTheme)) {
            //Log.d("Theme", "the theme was changed");
            //recreate();
        }
        lastTheme = -1;
    }

    protected void launchSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lastTheme = Integer.parseInt(prefs.getString("themeID", "-1"));
        //Intent settingIntent = new Intent(this, SettingsActivity.class);
        //startActivityForResult(settingIntent, 201);
        //startActivity(settings);
    }



    public void getFaceBookStuff() {



        /* make the API call */
        try {
            String fbID = AccessToken.getCurrentAccessToken().getUserId();
            Profile profile = Profile.getCurrentProfile();
            Log.e("IDIDIDIDIDIIDID", fbID);

            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.d("adskjfhakljsdhfjdasf", "onCompleted() returned: " + response.toString());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();

            final String TAG = "gello world";



            GraphRequest data_request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject json_object,
                                GraphResponse response) {

                            try {
                                // convert Json object into Json array
                                Log.i(TAG, "onCompleted: " + response.toString());
                                Log.i(TAG, "Next Line: " + json_object.toString());
                                JSONArray posts = json_object.getJSONObject("likes").
                                optJSONArray("data");

                                for (int i = 0; i < posts.length(); i++) {

                                    JSONObject post = posts.optJSONObject(i);
                                    String id = post.optString("id");
                                    String category = post.optString("category");
                                    String name = post.optString("name");
                                    int count = post.optInt("likes");
                                    // print id, page name and number of like of facebook page
                                    Log.e("id -", id+" name -"+name+ " category-"+
                                            category+ " likes count -" + count);
                                }

                            } catch(Exception e){

                            }
                        }
                    });
            Bundle permission_param = new Bundle();
            // add the field to get the details of liked pages
            permission_param.putString("fields", "likes{id,category,name,location,likes}");
            data_request.setParameters(permission_param);
            data_request.executeAsync();





            GraphRequestBatch batch = new GraphRequestBatch(
                    GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject jsonObject,
                                        GraphResponse response) {
                                    // Application code for user
                                    Log.v("ME", response.toString());
                                }
                            }),
                    GraphRequest.newMyFriendsRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        JSONArray jsonArray,
                                        GraphResponse response) {
                                    // Application code for users friends
                                    Log.v("FRIEND", response.toString());

                                    try {
                                        //Log.i(TAG, "onCompleted: " + response.getJSONObject().getJSONArray("data").getString(0));
                                        final JSONArray arr = response.getJSONObject().getJSONArray("data");
                                        List<String> list = new ArrayList<String>();
                                        for (int i = 0; i < arr.length(); i++) {
                                            list.add(arr.getJSONObject(i).getString("name"));

                                        }

                                        for (int i = 0; i < list.size(); i++) {
                                            al.add(new ContactInfo("Facebook: " + list.get(i),
                                                    arr.getJSONObject(0).getString("id"), " ", true));
                                            Log.v("asdlfkjh", list.get(i));
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    mAdapter = new ContactAdapter(al, Contacts.this, listOfNames);
                                    alphabetScroller.setAdapter(mAdapter);

                                }
                            })
            );
            batch.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch graphRequests) {
                    // Application code for when the batch finishes
                }
            });
            batch.executeAsync();

        } catch(NullPointerException e) {
            e.printStackTrace();
        }




    }




}


