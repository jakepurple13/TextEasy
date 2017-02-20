package app.easy.text.texteasy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.ftinc.scoop.Scoop;

import app.easy.text.texteasy.R;

public class CustomThemeCreator extends AppCompatActivity {


    FloatingSearchView fsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_custom_theme_creator);

        fsv = (FloatingSearchView) findViewById(R.id.floating_search_view);

        fsv.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                //fsv.swapSuggestions(newSuggestions);
            }
        });



    }
}
