package app.easy.text.texteasy.About;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.ftinc.scoop.Scoop;
import com.vansuita.materialabout.builder.AboutBuilder;

import app.easy.text.texteasy.R;

public class DevAbout extends AppCompatActivity {

    String name;
    String subtitle;
    String brief;

    FrameLayout fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_custom_theme_creator);

        fl = (FrameLayout) findViewById(R.id.framelayouted);

        boolean jj = getIntent().getBooleanExtra("AboutName", true);


        String holder = "I made this app for" +
                " a Mobile App Bowl. It wasn't supposed to be a project that I thought I would " +
                "be putting this much time into. But I did and I learned a lot from it.\n" +
                "I started this project on September 12, 2016 and worked endlessly on it." +
                " By the end of September, I launched version 1 of TextEasy." +
                " Oh, how young it was back then. I, unfortunately, had to take a break due to " +
                "classes.\nBut February 11th, 2017 came around and my programming finger " +
                "started to twitch and I started up development again. I had tons of ideas that " +
                "I wanted to add to TextEasy. Unfortunately, that same day my poor Nexus 5X " +
                "caught the infamous boot loop bug. So I called and got a new one on Valentine's Day." +
                " Development still isn't over.";

        if(jj) {
            name = "Jacob Rein";
            subtitle = "Mobile Developer";
            brief = "My name is Jacob Rein and I am the developer of this app.\nThis project is one that I will always cherish." +
                    " The amount of hours I've put into this app is more than I'd want to say. I" +
                    " hope that you, the one reading/using this app, can feel at least a little, " +
                    "amazingness that had gone into the creation of this app.";
        } else {
            name = "Jack Lu";
            subtitle = "Graphic Designer";
            brief = "I am a graphic designer";
        }

        View view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName(name)
                .setSubTitle(subtitle)
                .setBrief(brief)
                .setLinksAnimated(true)
                .setAppIcon(R.drawable.texteasyicon)
                .setAppName(R.string.app_name)
                .addFiveStarsAction("app.easy.text.texteasy")
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name + "", "Check out this awesome texting app I found!\n" +
                        "https://play.google.com/store/apps/details?id=app.easy.text.texteasy")
                .build();


        //setContentView(view);

        fl.addView(view);

        /*addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*/


    }
}
