package app.easy.text.texteasy.Dictionary;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import app.easy.text.texteasy.ContactList.ContactAdapter;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;

public class ListOfWords extends AppCompatActivity {

    Translator t;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerViewFastScroller fastScroller;
    EditText searchBar;
    String searchKey = "";
    ArrayList<WordInfo> searched;
    ArrayList<WordInfo> al = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_words);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8950844463555971~6055866848");

        AdView mAdView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        t = new Translator(this);

        searched = new ArrayList<>();

        LinkedHashMap<String, String> hm = t.getWords();

        for(String s : hm.keySet()) {
            al.add(new WordInfo(s, hm.get(s)));
        }

        al.add(new WordInfo("lol", "laugh out loud"));

        mRecyclerView = (RecyclerView) findViewById(R.id.dictionary);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Collections.sort(al, new InfoCompare());

        mAdapter = new WordAdapter(al, ListOfWords.this);
        mRecyclerView.setAdapter(mAdapter);


        fastScroller = (RecyclerViewFastScroller) findViewById(R.id.quickDictionary);

        // adds in Alphabetical scroller
        fastScroller.setRecyclerView(mRecyclerView);

        ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < al.size(); i++) {
            String name = al.get(i).word;
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }

        fastScroller.setUpAlphabet(mAlphabetItems);

        searchBar = (EditText) findViewById(R.id.wordsearch);

        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searched.clear();
                searchKey = s.toString();
                System.out.println(searchKey);
                for (int i = 0; i < al.size(); i++) {
                    if (al.get(i).word.toUpperCase().contains(searchKey.toUpperCase()) ||
                            al.get(i).word.contains(searchKey)) {
                        searched.add(al.get(i));
                    }
                }

                mAdapter = new WordAdapter(searched, ListOfWords.this);
                mRecyclerView.setAdapter(mAdapter);
            }

            /**
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public class InfoCompare implements Comparator<WordInfo> {

        public int compare(WordInfo e1, WordInfo e2) {
            return e1.word.compareTo(e2.word);
        }
    }

    public class WordInfo {
        String word;
        String meaning;


        public WordInfo(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        @Override
        public String toString() {
            return word + " = " + meaning;
        }

    }

    Dialog dialog;

    public void changeWord(String word, String meaning, final int location) {

        dialog = new Dialog(this);
        /**
         *
         */

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.update_word);
        /**
         *
         * @param text
         */
        dialog.setTitle("Change the Word");

        final EditText origin = (EditText)  dialog.findViewById(R.id.original);

        final EditText newWord = (EditText) dialog.findViewById(R.id.newMeaning);

        origin.setText(word);
        newWord.setText(meaning);


        Button closeButton = (Button) dialog.findViewById(R.id.cancel);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button accept = (Button) dialog.findViewById(R.id.okay);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WordInfo wi = al.remove(location);

                wi.setWord(origin.getText().toString());
                wi.setMeaning(newWord.getText().toString());

                al.add(wi);

                Collections.sort(al, new InfoCompare());

                mAdapter = new WordAdapter(al, ListOfWords.this);
                mRecyclerView.setAdapter(mAdapter);

                dialog.dismiss();
            }
        });


        //tv.setText(message);

        dialog.show();


        /**
         *
         */
    }



}
