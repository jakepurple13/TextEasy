package app.easy.text.texteasy.ContactList;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.ftinc.scoop.Scoop;
import com.ftinc.scoop.ui.ScoopSettingsActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mooveit.library.Fakeit;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.viethoa.RecyclerViewFastScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.easy.text.texteasy.Dictionary.ListOfWords;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Settings.Settings1Activity;
import app.easy.text.texteasy.Tester.BlankTestingActivity;
import app.easy.text.texteasy.Translator;
import dmax.dialog.SpotsDialog;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import me.drakeet.materialdialog.MaterialDialog;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.Data;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

import static android.R.attr.id;

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
    IndexFastScrollRecyclerView alphabetScroller;
    //Contact list
    ArrayList<ContactInfo> al = new ArrayList<>();
    //Translator
    Translator translate;// = new Translator(this);
    //This is for searching through the contacts
    String searchKey = "";
    //This is the list for searching
    ArrayList<ContactInfo> searched;
    //This will come up as contacts are retrieved
    ProgressTask pt;
    //first time for tutorial
    boolean firstTimeAddContact = false;
    //first time for tutorial
    boolean firstTimeSearch = false;
    //first time check
    boolean firstTime;
    //the last theme id
    int lastTheme = -1;
    //This is for the fast index bar
    String listOfNames = "";
    //This is the search bar
    MaterialSearchBar searchBars;
    //Our BEAUTIFUL floating action button
    BoomMenuButton bmb;
    //A quick dialog
    MaterialDialog mMaterialDialog;
    //This is for sharing
    public String messageToPass = "";
    //purely for generating fake data
    boolean testingVariable = true;
    //layout for the contact activity
    RelativeLayout rl;
    //the toolbar
    Toolbar toolbar;
    //Ads!
    AdView mAdView;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Scoop! Deals with the theme
        Scoop.getInstance().apply(this);
        setContentView(R.layout.activity_contacts);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Ad initializer! Yay!
        MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.ad_code));

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //Get the translator running
        translate = new Translator(this);
        //set up the layout
        rl = (RelativeLayout) findViewById(R.id.contactlayout);

        //This is to get the contacts
        pt = new ProgressTask(Contacts.this);
        pt.execute();
        //Search list
        searched = new ArrayList<>();

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

        //These are to get first time data
        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        firstTime = load.getBoolean("first time contact", true);
        firstTimeAddContact = load.getBoolean("add contact", false);
        firstTimeSearch = load.getBoolean("search", false);
        //firstTime = true;

        //---------------BOOM---------------------

        bmb = (BoomMenuButton) findViewById(R.id.bmb);

        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);
        //Add contact
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
                        mAdapter = new ContactAdapter(al, Contacts.this, listOfNames, translate);
                        alphabetScroller.setAdapter(mAdapter);
                    }
                })
                .normalText("Add a Contact")
                .subNormalText("Met a new friend? Add their Contact info!");

        bmb.addBuilder(contactAdd);
        //Goto settings
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
        //WIP (Work in Progress)
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
        //Call someone
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
        //WIP (Work in Progress)
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

        /*//First time
        if (!firstTimeAddContact) {

            SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = enter.edit();
            editor.putBoolean("add contact", true);
            editor.apply();

            firstTimeAddContact = true;

            TapTargetView.showFor(this,
                    TapTarget.forView(bmb, "Settings", "Click here for more options")
                            .cancelable(true)
                            .drawShadow(true)
                            .tintTarget(true)
                            .transparentTarget(false)
                            .outerCircleColor(R.color.accent_color)
                            .targetCircleColor(R.color.accent),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            view.dismiss(true);
                        }

                        @Override
                        public void onTargetLongClick(TapTargetView view) {

                        }
                    });

        }*/

        //---------------BOOM---------------------

        alphabetScroller = (IndexFastScrollRecyclerView) findViewById(R.id.contactScroller);

        alphabetScroller.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        alphabetScroller.setLayoutManager(mLayoutManager);
        //Search Bar set up
        searchBars = (MaterialSearchBar) findViewById(R.id.searchBars);
        searchBars.setTextColor(R.color.black);
        searchBars.setSpeechMode(false);
        searchBars.setMaxSuggestionCount(5);
        searchBars.inflateMenu(R.menu.contact_activity_menu);
        searchBars.getMenu().setOnMenuItemClickListener(this);

        searchBars.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i3, int i1, int i2) {
                //All this is to search
                searched.clear();
                searchKey = s.toString();
                System.out.println(searchKey);
                //We search both number and name
                for (int i = 0; i < al.size(); i++) {
                    if (al.get(i).name.toUpperCase().contains(searchKey.toUpperCase()) ||
                            al.get(i).number.contains(searchKey)) {
                        searched.add(al.get(i));
                    }

                }
                //set the adapter for real time searching
                mAdapter = new ContactAdapter(searched, Contacts.this, listOfNames, translate);
                alphabetScroller.setAdapter(mAdapter);

                //this is for the suggestion list
                List<String> suggestions = new ArrayList<>();
                //As long as the search size isn't 0
                if(searched.size()!=0) {
                    //Go through and show only 5 suggestions if applicable
                    for (int i = 0; i < 5 && i<searched.size(); i++) {
                        suggestions.add(searched.get(i).name);
                    }
                    //From here, update suggest list
                    searchBars.updateLastSuggestions(suggestions);
                    //if the search text is "" then just hide the list
                    if(s.length()==0) {
                        searchBars.hideSuggestionsList();
                    }
                //otherwise just hide the list
                } else {
                    searchBars.hideSuggestionsList();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //on the action of searching, just hide the suggestion list.
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

        //Have the recyclerview gain focus so the suggestion list for the search bar doesn't come up
        alphabetScroller.requestFocus();

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
            Log.e("Contacts Line 667", "onCreate: " + e);
        }

    }

    public void firstTimeHere() {

        FancyShowCaseView search = new FancyShowCaseView.Builder(Contacts.this)
                .focusOn(searchBars)
                .title("Search Bar\nSearch for a contact here")
                .titleGravity(Gravity.CENTER)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        FancyShowCaseView bmbText = new FancyShowCaseView.Builder(Contacts.this)
                .focusOn(bmb)
                .title("Options\nAdd a Contact, Go to the Settings Menu, or Call Someone by clicking this")
                .titleGravity(Gravity.END | Gravity.CENTER)
                .focusShape(FocusShape.CIRCLE)
                .build();

        FancyShowCaseView texted = new FancyShowCaseView.Builder(Contacts.this)
                .focusOn(alphabetScroller)
                .title("Contacts Will Show Up Here\nClick On a Contact to Text Them")
                .titleGravity(Gravity.BOTTOM | Gravity.START)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        FancyShowCaseView ads = new FancyShowCaseView.Builder(Contacts.this)
                .focusOn(mAdView)
                .title("And if you want to support the developers, you can click on this ad" +
                        " to give him a few pennies.")
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .build();

        new FancyShowCaseQueue()
                .add(search)
                .add(bmbText)
                //.add(texted)
                .add(ads)
                .show();

        SharedPreferences enter = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = enter.edit();
        editor.putBoolean("first time contact", false);
        editor.apply();
    }

    /*
    *getsms
    *get texts of a contact
    * phoneNumber - (String) - a contacts phone number
    */
    public String getsms(String phoneNumber) {
        //the text that will be returned
        String text = " ";

        Uri uri = Uri.parse("content://sms/");
        ContentResolver contentResolver = getContentResolver();
        //SQL based stuff to single out a contact

        //gets the last text contact sent
        //String sms = "address='"+ "+1" + phoneNumber + "'";
        //gets last contact you sent
        //String sms = "address='" + phoneNumber + "'";
        //Gets the last text sent or received
        String[] smsTest = new String[]{ "+1" + phoneNumber, phoneNumber};
        //Cursor cursor = contentResolver.query(uri, new String[] { "_id", "body", "date", "address" }, sms, null, null);
        Cursor cursor = contentResolver.query(uri, new String[] { "_id", "body", "date", "address" }, "address IN(?,?)", smsTest, null);
        //body of text
        String strbody = "";
        //To get the date
        Calendar cal = Calendar.getInstance();
        //get the most recent data
        assert cursor != null;
        if(cursor.moveToFirst()) {
            strbody = cursor.getString(cursor.getColumnIndex("body"));
            cal.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date")).toString()));
            //Log.i(phoneNumber, "getsms: " + strbody);
        }
        //Change calendar to a date
        Date d = cal.getTime();
        //deal with patterns to conversion
        String pattern = "hh:mm:ss a MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        //Closing the cursor
        cursor.close();
        //moving on to String manipulation
        text = strbody;
        //Possible change in color
        String textColor = "gray";

        //String result = "<br><small><small><font color=\"" + textColor + "\">" + translate.translate(text) +
          //      "<br>" + format.format(d) + "</font></small></small>";


        //This is what will be returned
        String result = "<br><small><small><small>" + translate.translate(text) + "<br>" + format.format(d) + "</small></small></small>";

        //if no text was sent, make result do a break line so things still look nice
        if(text.equals("")) {
            result = "<br>";
        }
        //return the text
        return result;
    }


    /*
    *getAllContacts
    *gets all of the contacts
    */
    public void getAllContacts() {
        // Default locale is en
        Fakeit.init(this);
        long startnow;
        long endnow;
        //getTexts();
        Uri uris = Uri.parse("content://sms");
        String[] proj = {"body", "address"};

        //this is to see how long this method takes
        startnow = android.os.SystemClock.uptimeMillis();
        ContentResolver cr = getContentResolver();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                selection,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        assert cursor != null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //Log.d("con ", "name " + contactName + " number" + contactNumber);

            String text = "";
            //gets last text
            //text = getsms(contactNumber);

            //This is for privacy
            if(testingVariable) {
                al.add(new ContactInfo(Fakeit.name().name(),
                        Fakeit.phone().formats(),
                        ""));
            } else {
                al.add(new ContactInfo(contactName, contactNumber, text));
            }

            //c.close();

            //al.add(new ContactInfo(contactName, contactNumber, "Hello"));

            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;
        //This is for how long it took to run this method
        endnow = android.os.SystemClock.uptimeMillis();
        Log.d("END", "TimeForContacts " + (endnow - startnow) + " ms");

        if(testingVariable) {
            al.add(new ContactInfo("The Master Programmer", "2017854423", ""));
        }

        getFaceBookStuff();

        if(al.size()==0) {
           al.add(new ContactInfo("Please goto settings to enable permissions",
                   "Please goto settings to enable permissions",
                   "Please goto settings to enable permissions"));
        }

        SharedPreferences enter = getSharedPreferences("numOfTexts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = enter.edit();
        editor.putInt("numOfContacts", al.size());
        editor.apply();

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
            case R.id.wordChange:

                Intent callIntent = new Intent(this, ListOfWords.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(this, R.anim.back_to_contacts, R.anim.going_down).toBundle();
                startActivity(callIntent, bndlanimation);

                return true;

            case R.id.themes:

                Intent settings = ScoopSettingsActivity.createIntent(this, "Settings");
                startActivityForResult(settings, 201);

                return true;

            case R.id.settings:

                Intent settingsIntent = new Intent(this, Settings1Activity.class);
                startActivityForResult(settingsIntent, 201);

                return true;

            /*case R.id.testPage:

                Intent tester = new Intent(this, BlankTestingActivity.class);
                tester.putExtra("contactNum", al.size());
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
     *
     */
    //This class is for reading the contacts
    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private Contacts activity;
        private Context context;
        SpotsDialog dialog1;

        public ProgressTask(Contacts activity) {
            this.activity = activity;
            context = activity;
            dialog1 = new SpotsDialog(context, "Loading Contacts...\nPlease Wait");
        }

        //This is where contacts are retrieved
        @Override
        protected Boolean doInBackground(String... params) {
            activity.getAllContacts();
            return null;
        }
        //Show our dialog so things won't get messed up
        protected void onPreExecute() {
            dialog1.show();
        }
        //Get rid of our dialog
        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            dialog1.hide();
            //Sorts the list of contacts
            Collections.sort(al, new InfoCompare());

            ArrayList<String> mAlphabetItems = new ArrayList<>();
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < al.size(); i++) {
                String name = al.get(i).name;
                if (name == null || name.trim().isEmpty())
                    continue;
                //Gets the first letter
                String word = name.substring(0, 1);
                if (!strAlphabets.contains(word)) {
                    strAlphabets.add(word);
                    mAlphabetItems.add(word);
                }
            }
            //Adds it to listOfNames for index bar
            for(int i=0;i<mAlphabetItems.size();i++) {
                listOfNames+=mAlphabetItems.get(i);
            }

            mAdapter = new ContactAdapter(al, Contacts.this, listOfNames, translate);

            alphabetScroller.setAdapter(mAdapter);
            //EASTER EGG!
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

            ViewTreeObserver vto = alphabetScroller.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    alphabetScroller.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    //We are now sure the view is drawn and should be able to do what you wanted:
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(alphabetScroller,InputMethodManager.SHOW_IMPLICIT);

                    if(firstTime) {
                        firstTimeHere();
                    }

                }
            });

        }

    }

    //This class is to compare contacts
    public class InfoCompare implements Comparator<ContactInfo> {
        public int compare(ContactInfo e1, ContactInfo e2) {
            return e1.name.compareTo(e2.name);
        }
    }
    //Our Contact class!
    public class ContactInfo {
        //Name
        String name;
        //Last text sent or received
        String text;
        //Number
        String number;
        //Is this their facebook info?
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

            final String TAG = "hello world";

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

                            } catch(Exception e) {

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

                                    mAdapter = new ContactAdapter(al, Contacts.this, listOfNames, translate);
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
            //e.printStackTrace();
            Log.e("error", e.toString());
        }

    }

}


