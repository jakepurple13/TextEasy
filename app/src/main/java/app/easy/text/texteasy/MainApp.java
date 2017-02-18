package app.easy.text.texteasy;

import android.app.Application;
import android.preference.PreferenceManager;

import com.ftinc.scoop.Scoop;

/**
 * Created by Jacob on 2/16/17.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Scoop
        Scoop.waffleCone()
                .addFlavor("Default", R.style.Theme_Scoop, true)
                .addFlavor("Light", R.style.Theme_Scoop_Light)
                .addDayNightFlavor("DayNight", R.style.Theme_Scoop_DayNight)
                .addFlavor("Alternate 1", R.style.Theme_Scoop_Alt1)
                .addFlavor("Alternate 2", R.style.Theme_Scoop_Alt2)
                .addDayNightFlavor("Dark Mode", R.style.Theme_NightTheme_DayNight_NightMODE)
                .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                .initialize();

        // Plant the logger
        //Timber.plant(new Timber.DebugTree());



    }
}