package app.easy.text.texteasy.About;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.ftinc.scoop.Scoop;
import com.vansuita.materialabout.builder.AboutBuilder;

import app.easy.text.texteasy.R;

public class CustomThemeCreator extends AppCompatActivity {

    String name;
    String subtitle;
    String brief;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_custom_theme_creator);

        boolean jj = getIntent().getBooleanExtra("AboutName", true);




        if(jj) {
            name = "Jacob Rein";
            subtitle = "Mobile Developer";
            brief = "I am an android developer";
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
                .setAppIcon(R.drawable.texteasyicon)
                .setAppName(R.string.app_name)
                .addFiveStarsAction("app.easy.text.texteasy")
                .setVersionAsAppTitle()
                .addShareAction(R.string.app_name + "", "Check out this awesome texting app I found!\n" +
                        "https://play.google.com/store/apps/details?id=app.easy.text.texteasy")
                .build();


        //setContentView(view);

        addContentView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


    }
}
