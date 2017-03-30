package app.easy.text.texteasy.Settings;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.ftinc.scoop.Scoop;
import com.ftinc.scoop.ui.ScoopSettingsActivity;
import com.rey.material.widget.TextView;

import app.easy.text.texteasy.About.AboutScreen;
import app.easy.text.texteasy.MainApp;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Tester.LoginAcitivities;

import java.util.List;
import java.util.Locale;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Settings1Activity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Scoop.getInstance().apply(this);
        super.onCreate(savedInstanceState);

        setupActionBar();



    }



    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DefaultPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || SettingsThemePreferenceFragment.class.getName().equals(fragmentName)
                || ThemePreferenceFragment.class.getName().equals(fragmentName)
                || FeedbackPreferenceFragment.class.getName().equals(fragmentName)
                || NewAbbreviationsPreferenceFragment.class.getName().equals(fragmentName)
                || StatisticsPreferenceFragment.class.getName().equals(fragmentName)
                || ShowNumberFragment.class.getName().equals(fragmentName)
                || LoginPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            //Intent settings = ScoopSettingsActivity.createIntent(getActivity(), "Change Theme");
            //startActivityForResult(settings, 201);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));

            Intent i = new Intent(this.getContext(), AboutScreen.class);
            startActivity(i);
            getActivity().finish();

        }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DefaultPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            //Intent settings = ScoopSettingsActivity.createIntent(getActivity(), "Change Theme");
            //startActivityForResult(settings, 201);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));

            final String myPackageName = getActivity().getPackageName();
            if (!Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(myPackageName)) {

                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                /**
                 *
                 */
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                        myPackageName);
                startActivityForResult(intent, 201);
                // App is not default.
                // Show the "not currently set as the default SMS app" interface
                //AskPermission();

            }

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {


            if (requestCode == 201) {

                getActivity().finish();

            }

            super.onActivityResult(requestCode, resultCode, data);

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                //startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ThemePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            setHasOptionsMenu(true);

            Intent settings = ScoopSettingsActivity.createIntent(getActivity(), "Change Theme");
            startActivityForResult(settings, 201);
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {


            if (requestCode == 201) {

                getActivity().finish();

            }

            super.onActivityResult(requestCode, resultCode, data);

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsThemePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            setHasOptionsMenu(true);

            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            getActivity().finish();
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FeedbackPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            setHasOptionsMenu(true);

            String url = getResources().getString(R.string.feedback_link);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            getActivity().finish();
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NewAbbreviationsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            setHasOptionsMenu(true);

            String url = getResources().getString(R.string.abbrev_link);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            getActivity().finish();
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class StatisticsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            setHasOptionsMenu(true);

            Intent i = new Intent(this.getContext(), StatisticsActivity.class);
            startActivity(i);
            getActivity().finish();
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ShowNumberFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            addPreferencesFromResource(R.xml.show_number);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));

            //User's phone number

            TelephonyManager tm = (TelephonyManager) this.getContext().getSystemService(TELEPHONY_SERVICE);
            String number = tm.getLine1Number();

            EditTextPreference phoneNumber = (EditTextPreference) findPreference("phone_number");
            phoneNumber.setTitle("My Number: " + number);
            phoneNumber.setSelectable(false);

            //Device Model

            EditTextPreference modelNumber = (EditTextPreference) findPreference("model_number");
            modelNumber.setTitle("Device: " + getDeviceName());
            modelNumber.setSelectable(false);

            //Android version number

            EditTextPreference versionNumber = (EditTextPreference) findPreference("version_number");
            versionNumber.setTitle("Android Version: " + Build.VERSION.RELEASE);
            versionNumber.setSelectable(false);

            //Battery Life

            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = this.getContext().registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float)scale;

            EditTextPreference batteryLife = (EditTextPreference) findPreference("battery_life");
            batteryLife.setTitle("Battery Life: " + ((int) (batteryPct*100)) + "%");
            batteryLife.setSelectable(false);

            //border width

            EditTextPreference strokeWidthChoice = (EditTextPreference) findPreference("stroke_width_choice");

            SharedPreferences settings = strokeWidthChoice.getPreferenceManager().getDefaultSharedPreferences(this.getContext());
            String defaultValue = settings.getString("stroke_width_choice", "1");
            strokeWidthChoice.setDefaultValue(defaultValue);

            strokeWidthChoice.setTitle("Border Width: " + defaultValue);

            strokeWidthChoice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    Log.e("asdfsadfasdfsafafs", "onPreferenceChange: " + newValue);
                    SharedPreferences.Editor editor = preference.getEditor();
                    editor.putString("stroke_width_choice", newValue+"");
                    editor.apply();

                    preference.setTitle("Stroke Width: " + newValue);

                    return false;
                }
            });

        }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LoginPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Scoop.getInstance().apply(getActivity());
            setHasOptionsMenu(true);

            Intent i = new Intent(this.getContext(), LoginAcitivities.class);
            startActivity(i);
            getActivity().finish();
            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("example_text"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), Settings1Activity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
