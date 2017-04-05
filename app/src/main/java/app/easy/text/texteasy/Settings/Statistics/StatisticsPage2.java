package app.easy.text.texteasy.Settings.Statistics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.ftinc.scoop.Scoop;
import com.github.lzyzsd.circleprogress.ArcProgress;

import app.easy.text.texteasy.R;
import me.drakeet.materialdialog.MaterialDialog;

public class StatisticsPage2 extends AppCompatActivity {


    ArcProgress ap;

    private MaterialMenuDrawable materialMenu;

    Button back;

    MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scoop.getInstance().apply(this);
        setContentView(R.layout.activity_statistics_page2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.statTools);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle your drawable state here
                materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK);
                onBackPressed();
            }
        });

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        toolbar.setNavigationIcon(materialMenu);


        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Hi")
                .setMessage("Hello")
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


        SharedPreferences load2 = PreferenceManager.getDefaultSharedPreferences(this);
        int g = load2.getInt("GSBug", 0);

        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        int num = load.getInt("roles", 0);



        ap = (ArcProgress) findViewById(R.id.arc_progress);
        ap.setTextColor(Color.YELLOW);
        ap.setFinishedStrokeColor(Color.YELLOW);

        View.OnClickListener goldTheme;


        if(g>=79) {
            ap.setProgress(100);
            ap.setBottomText("Gold Theme");
            goldTheme = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.setTitle("Gold Theme");
                    mMaterialDialog.setMessage("You Unlocked the Gold Theme");
                    mMaterialDialog.show();
                }
            };
        } else {
            ap.setProgress(g/79);
            ap.setBottomText("???");
            goldTheme = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.setTitle("New Theme");
                    mMaterialDialog.setMessage("Location: About Screen");
                    mMaterialDialog.show();
                }
            };
        }

        ap.setOnClickListener(goldTheme);

        back = (Button) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatisticsPage2.this, StatisticsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.back_to_contacts, R.anim.from_contacts);
                finish();
            }
        });


    }
}
