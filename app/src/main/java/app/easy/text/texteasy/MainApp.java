package app.easy.text.texteasy;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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


    }

}