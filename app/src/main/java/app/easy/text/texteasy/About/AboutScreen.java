package app.easy.text.texteasy.About;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.ftinc.scoop.Scoop;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import app.easy.text.texteasy.R;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Jacob on 2/21/17.
 */

public class AboutScreen extends MaterialAboutActivity {

    int num = 0;

    MaterialStyledDialog mSDialog;

    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {

        Scoop.getInstance().apply(this);
        //Card builder
        MaterialAboutCard.Builder devTeam = new MaterialAboutCard.Builder();
        //Title for the development team
        devTeam.title("Development Team");
        //Jacob!
        devTeam.addItem(new MaterialAboutActionItem.Builder()
                .text("Jacob Rein")
                .subText("Developer")
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(AboutScreen.this, DevAbout.class);
                        i.putExtra("AboutName", true);
                        startActivity(i);
                    }
                })
                .icon(R.mipmap.ic_launcher)
                //.icon(R.mipmap.profile_picture)
                .build());
        //Jack!
        devTeam.addItem(new MaterialAboutActionItem.Builder()
                .text("Jack Lu")
                .subText("Graphic Designer")
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent i = new Intent(AboutScreen.this, DevAbout.class);
                        i.putExtra("AboutName", false);
                        startActivity(i);
                    }
                })
                .icon(R.mipmap.ic_launcher)
                .build());

        //Contribution people
        MaterialAboutCard.Builder contTeam = new MaterialAboutCard.Builder();
        //title
        contTeam.title("Contributions");
        //George's Easter Egg
        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(AboutScreen.this);
        num = load.getInt("GSBug", 0);

        String georgeName = num>=79 ? "<font color=\"#FFD700\">George Samuels</font>" : "George Samuels";
        String georgeText = num>=79 ? "<font color=\"#FFD700\">Came up with App Name</font>" : "Came up with App Name";

        contTeam.addItem(new MaterialAboutActionItem.Builder()
                .text(Html.fromHtml(georgeName))
                .subTextHtml(georgeText)
                .icon(R.mipmap.ic_launcher)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {

                        num++;

                        if(num==79) {
                            mSDialog = new MaterialStyledDialog.Builder(AboutScreen.this)
                                    .setTitle("Easter Egg Unlocked")
                                    .setDescription("You have unlocked the Gold Theme")
                                    //.setStyle(Style.HEADER_WITH_ICON)
                                    .setStyle(Style.HEADER_WITH_TITLE)
                                    .withDialogAnimation(true)
                                    .setIcon(R.drawable.texteasyicon)
                                    .setScrollable(true)
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

                        } else if(num<79) {
                            Toast.makeText(AboutScreen.this, num+"",Toast.LENGTH_SHORT).show();
                        }
                        SharedPreferences enter = PreferenceManager.getDefaultSharedPreferences(AboutScreen.this);
                        SharedPreferences.Editor editor = enter.edit();
                        editor.putInt("GSBug", num);
                        editor.apply();
                    }
                })
                .build());

        //Adding everyone else
        addPerson(contTeam, "Ryou", "Helped with the German words", "Thank you for the german words!");

        addPerson(contTeam, "Carlie", "Helped with the English words", "Thank you for the english words");

        addPerson(contTeam, "Ian", "Helped with the English words", "Thank you for the english words");

        addPerson(contTeam, "Kit", "Helped with the English words", "Thank you for the english words");

        addPerson(contTeam, "Gerry", "Gave some ideas", "Thank you for your ideas");

        //Special thanks
        MaterialAboutCard.Builder specialThanks = new MaterialAboutCard.Builder();
        //Title
        specialThanks.title("Special Thanks");
        //Coppola!
        addPerson(specialThanks, "Dr. Jean Coppola", "Mentor and Great Professor",
                "Thank you so much for being there for and with us." +
                        " Helping us learn and introduce us to so much, it means a lot to us.");




        return new MaterialAboutList(devTeam.build(), contTeam.build(), specialThanks.build());

    }


    public void addPerson(MaterialAboutCard.Builder macb, final String name, String subText, final String description) {

        macb.addItem(new MaterialAboutActionItem.Builder()
                .text(name)
                .subText(subText)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {

                        mSDialog = new MaterialStyledDialog.Builder(AboutScreen.this)
                                .setTitle(name)
                                .setDescription(description)
                                //.setStyle(Style.HEADER_WITH_ICON)
                                .setStyle(Style.HEADER_WITH_TITLE)
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
                })
                .icon(R.mipmap.ic_launcher)
                .build());

    }


    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.mal_title_about);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}