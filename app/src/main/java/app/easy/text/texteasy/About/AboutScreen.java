package app.easy.text.texteasy.About;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.ftinc.scoop.Scoop;

import app.easy.text.texteasy.R;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Jacob on 2/21/17.
 */

public class AboutScreen extends MaterialAboutActivity {

    int num = 0;

    MaterialDialog mMaterialDialog;

    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {

        Scoop.getInstance().apply(this);

        mMaterialDialog = new MaterialDialog(AboutScreen.this)
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



        MaterialAboutCard.Builder devTeam = new MaterialAboutCard.Builder();

        devTeam.title("Development Team");

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


        MaterialAboutCard.Builder contTeam = new MaterialAboutCard.Builder();

        contTeam.title("Contributions");

        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(AboutScreen.this);
        num = load.getInt("GSBug", 0);

        String georgeName = num>79 ? "<font color=\"#FFD700\">George Samuels</font>" : "George Samuels";
        String georgeText = num>79 ? "<font color=\"#FFD700\">Came up with App Name</font>" : "Came up with App Name";

        contTeam.addItem(new MaterialAboutActionItem.Builder()
                .text(Html.fromHtml(georgeName))
                .subTextHtml(georgeText)
                .icon(R.mipmap.ic_launcher)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {

                        num++;

                        if(num==79) {
                            // You can change the message anytime. before show
                            mMaterialDialog.setTitle("Easter Egg Unlocked");
                            // You can change the message anytime. after show
                            mMaterialDialog.setMessage("You have unlocked the Gold Theme");
                            mMaterialDialog.show();
                        }

                        SharedPreferences enter = PreferenceManager.getDefaultSharedPreferences(AboutScreen.this);
                        SharedPreferences.Editor editor = enter.edit();
                        editor.putInt("GSBug", num);
                        editor.apply();
                    }
                })
                .build());

        contTeam.addItem(new MaterialAboutActionItem.Builder()
                .text("Ryou")
                .subText("Spriter")
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {

                        // You can change the message anytime. before show
                        mMaterialDialog.setTitle("Ryou");
                        // You can change the message anytime. after show
                        mMaterialDialog.setMessage("Thank you for the german words!");
                        mMaterialDialog.show();
                    }
                })
                .icon(R.mipmap.ic_launcher)
                .build());



        MaterialAboutCard.Builder specialThanks = new MaterialAboutCard.Builder();

        specialThanks.title("Special Thanks");

        specialThanks.addItem(new MaterialAboutActionItem.Builder()
                .text("Dr. Jean Coppola")
                .subText("Mentor and Great Professor")
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {

                        // You can change the message anytime. before show
                        mMaterialDialog.setTitle("Dr. Coppola");
                        // You can change the message anytime. after show
                        mMaterialDialog.setMessage("Thank you so much for being there for and with us." +
                                " Helping us learn and introduce us to so much, it means a lot to us.");
                        mMaterialDialog.show();
                    }
                })
                .icon(R.mipmap.ic_launcher)
                .build());


        return new MaterialAboutList(devTeam.build(), contTeam.build(), specialThanks.build());

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