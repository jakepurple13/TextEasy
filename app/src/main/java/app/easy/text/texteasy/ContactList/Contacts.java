package app.easy.text.texteasy.ContactList;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.orm.SugarApp;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.easy.text.texteasy.Dictionary.ListOfWords;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Settings.SettingsActivity;
import app.easy.text.texteasy.Splash;
import app.easy.text.texteasy.Translator;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import me.everything.providers.core.Data;

/**
 *
 */

/**
 *
 */
public class Contacts extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ContactInfo> al = new ArrayList<>();
    //Translator translate = new Translator(this);
    EditText searchBar;
    String searchKey = "";
    ArrayList<ContactInfo> searched;
    ProgressTask pt;
    RecyclerViewFastScroller fastScroller;
    boolean firstTimeAddContact = false;
    /**
     * @param savedInstanceState
     */
    boolean firstTimeSearch = false;
    FloatingActionButton fab;

    Dialog feed;

    Resources.Theme lastTheme;

    String currentTheme;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentTheme = setThemed();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8950844463555971~6055866848");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        pt = new ProgressTask(Contacts.this);
        pt.execute();

        searched = new ArrayList<>();
        /**Ask User for Location Premisson and Accounts**/
        //AskPermission();

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //String syncConnPref = sharedPref.getString(SettingsActivity., "");
        //setTheme(getApplicationInfo().getThemeId());
        //System.err.println(PreferenceManager.getDefaultSharedPreferences(this).getString("defaultTheme", "0"));


        SharedPreferences load = getPreferences(Context.MODE_PRIVATE);
        firstTimeAddContact = load.getBoolean("add contact", false);
        /**
         *
         * @param view
         */
        firstTimeSearch = load.getBoolean("search", false);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        int backColor = currentTheme.equals("2") ? R.color.charcoal : R.color.lavender_indigo;

        fab.setBackgroundTintList(getResources().getColorStateList(backColor));

        int iconColor = currentTheme.equals("2") ? R.color.white : R.color.apple_green;

        fab.getDrawable().mutate().setTint(ContextCompat.getColor(this, iconColor));

        fab.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {

                //Add here

                if (!firstTimeAddContact) {

                    setTutorial("Add Contact", "Add a contact", fab);

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
                    mAdapter = new ContactAdapter(al, Contacts.this);
                    mRecyclerView.setAdapter(mAdapter);

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


        mRecyclerView = (RecyclerView) findViewById(R.id.contacts);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        searchBar = (EditText) findViewById(R.id.search);

        searchBar.addTextChangedListener(new TextWatcher() {
            /**
             *
             * @param s
             * @param start
             * @param count
             * @param after
            /**
             *
             * @param s
             */
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
                    /**
                     *
                     * @param v
                     */
                }

                mAdapter = new ContactAdapter(searched, Contacts.this);
                mRecyclerView.setAdapter(mAdapter);
            }

            /**
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             *
             * @param v
             * @param hasFocus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!firstTimeSearch) {
                    setTutorial("Search", "Search for a contact to find", searchBar);

                    SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = enter.edit();
                    editor.putBoolean("search", true);
                    editor.apply();

                    firstTimeSearch = true;
                }
            }
        });

        searchBar.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             /**
             *
             * */
            @Override
            public boolean onLongClick(View v) {

                setTutorial("Search", "Search for a contact to find", searchBar);
                return false;
            }
        });


        try {
            //readContacts();
        } catch (Exception e) {

        }

        fastScroller = (RecyclerViewFastScroller) findViewById(R.id.fast_scroller);

        // adds in Alphabetical scroller
        fastScroller.setRecyclerView(mRecyclerView);

        ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < al.size(); i++) {
            String name = al.get(i).name;
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }

        fastScroller.setUpAlphabet(mAlphabetItems);



    }


    public void getTexts() {
        TelephonyProvider tp = new TelephonyProvider(this);

        Data<Sms> ds = tp.getSms(TelephonyProvider.Filter.ALL);


        List<Sms> ls = ds.getList();

        for(int i=0;i<ls.size();i++) {

            Log.e(ls.get(i).address, ls.get(i).body);

        }


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
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,   ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, selection, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            Log.d("con ", "name " + contactName + " number" + contactNumber);

            //String whereAddress = "address = '" + contactNumber + "'";
            //Cursor c = cr.query(uris, proj, whereAddress, null, "date desc limit 1");
            //Log.wtf("QWE", c.getCount() + "");
            String text = " ";
            /*if (c.isNull(0) == false) {
                *//*do {
                    text = c.getString(c.getColumnIndex("body"));
                    Log.w("ASD", text);
                    text = translate.translate(text);
                } while (c.moveToNext());*//*
                text = c.getString(c.getColumnIndex("body"));
                Log.w("ASD", text);
                text = translate.translate(text);
            }*/

            al.add(new ContactInfo(contactName, contactNumber, text));
            //c.close();

            //al.add(new ContactInfo(contactName, contactNumber, "Hello"));

            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;

        endnow = android.os.SystemClock.uptimeMillis();
        Log.d("END", "TimeForContacts " + (endnow - startnow) + " ms");
    }

    /**
     * @param title
     * @param description
     * @param v
     */

    public void setTutorial(String title, String description, View v) {
        new TapTargetView.Builder(Contacts.this) // The activity that hosts this view
                .title(title) // Specify the title text
                .description(description + "\nPress and hold to bring up again") // Specify the description text
                .cancelable(true)
                .drawShadow(true)
                .outerCircleColor(R.color.lavender_indigo)
                .targetCircleColor(R.color.paris_daisy)
                .listener(new TapTargetView.Listener() {
                    /**
                     *
                     * @param view
                     */
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        view.dismiss(true);
                    }

                    @Override
                    public void onTargetLongClick(TapTargetView view) {

                    }
                })
                .showFor(v);
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

            dialog.show();
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            dialog.hide();
            dialog.dismiss();
            Collections.sort(al, new InfoCompare());
            mAdapter = new ContactAdapter(al, Contacts.this);
            /**
             *
             * @param permsRequestCode
             * @param permissions
             * @param grantResults
             */
            mRecyclerView.setAdapter(mAdapter);
            // adds in Alphabetical scroller
            fastScroller.setRecyclerView(mRecyclerView);

            ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
            List<String> strAlphabets = new ArrayList<>();
            for (int i = 0; i < al.size(); i++) {
                String name = al.get(i).name;
                if (name == null || name.trim().isEmpty())
                    continue;

                String word = name.substring(0, 1);
                if (!strAlphabets.contains(word)) {
                    strAlphabets.add(word);
                    mAlphabetItems.add(new AlphabetItem(i, word, false));
                }
            }

            fastScroller.setUpAlphabet(mAlphabetItems);
            /**
             *
             */
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


        public ContactInfo(String name, String number, String text) {
            this.name = name;
            this.number = PhoneNumberUtils.normalizeNumber(number);
            this.text = text;
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
         *
         */
        @Override
        public String toString() {
            return name + ": " + number + "\n" + text;
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
            case R.id.wordChange:

                Intent callIntent = new Intent(this, ListOfWords.class);
                startActivity(callIntent);

                return true;

            case R.id.settings:
                lastTheme = getTheme();
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingIntent, 201);

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

    public String setThemed() {

        SharedPreferences prefs = getSharedPreferences("theming", MODE_PRIVATE);
        String themer = prefs.getString("themeID", "0");

        Log.e("adsl;kfj", themer);
        setTheme(themer.equals("2") ? R.style.NightTheme1 : R.style.LightTheme);
        //boolean ? (if true) : (if false);
        return themer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 201) {

            if (1 == SettingsActivity.RESULT_CODE_THEME_UPDATED) {
                reload();
                return;

            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(lastTheme!=null) {
            if(!getTheme().equals(lastTheme)) {
                reload();
            }
        }
    }


    public void reload() {

        //recreate();

        setThemed();
        Intent intent = new Intent(this, Contacts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}


