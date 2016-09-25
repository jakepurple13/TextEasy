package app.easy.text.texteasy.ContactList;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.easy.text.texteasy.MessageAdapter;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;

public class Contacts extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ContactInfo> al = new ArrayList<>();
    Translator translate = new Translator();
    EditText searchBar;
    String searchKey = "";
    ArrayList<ContactInfo> searched;
    ProgressTask pt;
    RecyclerViewFastScroller fastScroller;
    boolean firstTimeAddContact = false;
    boolean firstTimeSearch = false;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pt = new ProgressTask(Contacts.this);
        pt.execute();

        searched = new ArrayList<>();
        /**Ask User for Location Premisson and Accounts**/
        //AskPermission();

        SharedPreferences load = getPreferences(Context.MODE_PRIVATE);
        firstTimeAddContact = load.getBoolean("add contact", false);
        firstTimeSearch = load.getBoolean("search", false);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setBackgroundTintList(getResources().getColorStateList(R.color.lavender_indigo));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Add here

                if(!firstTimeAddContact) {

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
                    //readContacts();
                    Collections.sort(al, new InfoCompare());
                    mAdapter = new ContactAdapter(al, Contacts.this);
                    mRecyclerView.setAdapter(mAdapter);

                }

            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                setTutorial("Add Contact", "Add a contact", fab);

                return false;
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.contacts);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        searchBar = (EditText) findViewById(R.id.search);

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

                mAdapter = new ContactAdapter(searched, Contacts.this);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!firstTimeSearch) {
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
            @Override
            public boolean onLongClick(View v) {

                setTutorial("Search", "Search for a contact to find", searchBar);
                return false;
            }
        });


        try {
            //readContacts();
        } catch(Exception e) {

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

    public void readContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Uri uri = Uri.parse("content://sms");
        String[] proj = {"*"};


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
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.e("Contact ", "Name: " + name + ", Phone No: " + phoneNo);

                        String whereAddress = "address = '" + phoneNo + "'";
                        Cursor c = cr.query(uri, proj, whereAddress, null, "date desc limit 1");
                        String text = "Hello";
                        if (c.moveToFirst()) {
                            do {
                                text = c.getString(c.getColumnIndex("body"));
                                Log.w("ASD", text);
                                text = translate.translate(text);
                            } while (c.moveToNext());
                        }

                        al.add(new ContactInfo(name, phoneNo, text));
                        c.close();
                    }
                    pCur.close();
                }
            }
        }

        /*Collections.sort(al, new InfoCompare());
        mAdapter = new ContactAdapter(al, this);
        mRecyclerView.setAdapter(mAdapter);*/

    }

    public void setTutorial(String title, String description, View v) {
        new TapTargetView.Builder(Contacts.this) // The activity that hosts this view
                .title(title) // Specify the title text
                .description(description + "\nPress and hold to bring up again") // Specify the description text
                .cancelable(true)
                .drawShadow(true)
                .outerCircleColor(R.color.lavender_indigo)
                .targetCircleColor(R.color.paris_daisy)
                .listener(new TapTargetView.Listener() {
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


    private class ProgressTask extends AsyncTask<String, Void, Boolean> {
        private Dialog dialog;
        private Contacts activity;
        private Context context;


        public ProgressTask(Contacts activity) {
            this.activity = activity;
            context = activity;
            dialog = new Dialog(context);
        }



        /** progress dialog to show user that the backup is processing. */

        @Override
        protected Boolean doInBackground(String... params) {

            activity.readContacts();

            return null;
        }

        /** application context. */


        protected void onPreExecute() {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        }




    }

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


        @Override
        public String toString() {
            return name + ": " + "Number goes here";//number + "\n" + text;
        }

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

}
