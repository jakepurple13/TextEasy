package app.easy.text.texteasy;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.ftinc.scoop.Scoop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.easy.text.texteasy.ContactList.Contacts;
import app.easy.text.texteasy.Messages.MainActivity;

/**
 * Created by Jacob on 2/16/17.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        int num = load.getInt("GSBug", 0);
        //This is for if the easter egg is going to work
        if(num>=79) {
            // Initialize Scoop
            Scoop.waffleCone()
                    .addFlavor("Default", R.style.Theme_Scoop, true)
                    .addFlavor("Light", R.style.AppTheme)
                    .addFlavor("Blue", R.style.BlueTheme)
                    .addFlavor("Gold", R.style.GoldTheme)
                    .addFlavor("Dark Mode", R.style.Theme_NightTheme_DayNight_NightMODE)
                    .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                    .initialize();
        } else {
            // Initialize Scoop
            Scoop.waffleCone()
                    .addFlavor("Default", R.style.Theme_Scoop, true)
                    .addFlavor("Light", R.style.AppTheme)
                    .addFlavor("Blue", R.style.BlueTheme)
                    .addFlavor("Dark Mode", R.style.Theme_NightTheme_DayNight_NightMODE)
                    .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                    .initialize();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

            if (shortcutManager.getDynamicShortcuts().size() == 0) {
                // Application restored. Need to re-publish dynamic shortcuts.
                if (shortcutManager.getPinnedShortcuts().size() > 0) {
                    // Pinned shortcuts have been restored. Use
                    // updateShortcuts() to make sure they contain
                    // up-to-date information.
                    shortcutManager.removeAllDynamicShortcuts();
                }
            }

            List<ShortcutInfo> scl = new ArrayList<>();

            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "id1")
                    .setShortLabel("Go to Contacts")
                    .setLongLabel("Go to Contacts")
                    .setIcon(Icon.createWithResource(this, R.drawable.texteasyicon))
                    .setIntent(new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, Contacts.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    .build();

            String number = load.getString("phoneNumberLast", "nope");

            if(!number.equals("nope")) {

                Intent i = new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("Number", number);

                ShortcutInfo shortcut1 = new ShortcutInfo.Builder(this, "id2")
                        .setShortLabel("Text " + getContactName(number))
                        .setLongLabel("Text " + getContactName(number))
                        .setIcon(Icon.createWithResource(this, R.drawable.texteasyicon))
                        .setIntent(i)
                        .build();

                scl.add(shortcut1);
            }

            scl.add(shortcut);

            shortcutManager.setDynamicShortcuts(scl);

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

}