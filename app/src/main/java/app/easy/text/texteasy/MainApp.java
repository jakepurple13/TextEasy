package app.easy.text.texteasy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ftinc.scoop.Scoop;

/**
 * Created by Jacob on 2/16/17.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        int num = load.getInt("GSBug", 0);

        if(num>79) {
            // Initialize Scoop
            Scoop.waffleCone()
                    .addFlavor("Default", R.style.Theme_Scoop, true)
                    .addFlavor("Light", R.style.Theme_Scoop_Light)
                    .addDayNightFlavor("DayNight", R.style.Theme_Scoop_DayNight)
                    .addFlavor("Alternate 1", R.style.Theme_Scoop_Alt1)
                    .addFlavor("Alternate 2", R.style.Theme_Scoop_Alt2)
                    .addFlavor("Dark Mode", R.style.Theme_NightTheme_DayNight_NightMODE)
                    .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                    .initialize();
        } else {
            // Initialize Scoop
            Scoop.waffleCone()
                    .addFlavor("Default", R.style.Theme_Scoop, true)
                    .addFlavor("Light", R.style.Theme_Scoop_Light)
                    .addDayNightFlavor("DayNight", R.style.Theme_Scoop_DayNight)
                    .addFlavor("Alternate 1", R.style.Theme_Scoop_Alt1)
                    .addFlavor("Alternate 2", R.style.Theme_Scoop_Alt2)
                    .addFlavor("Dark Mode", R.style.Theme_NightTheme_DayNight_NightMODE)
                    .setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
                    .initialize();
        }



    }
}