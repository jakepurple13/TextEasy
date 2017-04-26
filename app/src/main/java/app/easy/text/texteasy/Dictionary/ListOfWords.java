package app.easy.text.texteasy.Dictionary;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.ftinc.scoop.Scoop;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import me.toptas.fancyshowcase.FancyShowCaseView;

public class ListOfWords extends AppCompatActivity {

    Translator t;
    FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerViewFastScroller fastScroller;
    EditText searchBar;
    String searchKey = "";
    ArrayList<WordInfo> searched;
    ArrayList<WordInfo> al = new ArrayList<>();

    String TAG = "List Of Words";

    boolean firstTimeAddWord;

    private MaterialMenuDrawable materialMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Scoop.getInstance().apply(this);

        setContentView(R.layout.activity_list_of_words);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8950844463555971~6055866848");

        final AdView mAdView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        SharedPreferences load = getPreferences(Context.MODE_PRIVATE);
        firstTimeAddWord = load.getBoolean("add word", false);

        t = new Translator(this);

        searched = new ArrayList<>();

        LinkedHashMap<String, String> hm = t.getWords();

        for (String s : hm.keySet()) {
            al.add(new WordInfo(s, hm.get(s)));
        }

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
                    if (al.get(i).toString().toUpperCase().contains(searchKey.toUpperCase()) ||
                            al.get(i).toString().contains(searchKey)) {
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


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.lavender_indigo));
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(firstTimeAddWord) {
                    changeWord("", "", -1);
                } else {
                    setTutorial("Add Your Own", "Add your own lingos", fab);

                    SharedPreferences enter = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = enter.edit();
                    editor.putBoolean("add word", true);
                    editor.apply();

                    firstTimeAddWord = true;
                }
            }
        });

    }




    public void setTutorial(final String title, final String description, View v) {
        TapTargetView.showFor(this,
                TapTarget.forView(v, title, description + "\nPress and hold to bring up again")
                        .cancelable(true)
                        .drawShadow(true)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .outerCircleColor(R.color.primary)
                        .targetCircleColor(R.color.primary_dark),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        view.dismiss(true);
                    }

                    @Override
                    public void onTargetLongClick(TapTargetView view) {

                    }
                });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                TapTargetView.showFor(ListOfWords.this,
                        TapTarget.forView(v, title, description + "\nPress and hold to bring up again")
                                .cancelable(true)
                                .drawShadow(true)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .outerCircleColor(R.color.primary)
                                .targetCircleColor(R.color.primary_dark),
                        new TapTargetView.Listener() {
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                view.dismiss(true);
                            }

                            @Override
                            public void onTargetLongClick(TapTargetView view) {

                            }
                        });


                return false;
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

    public void changeWord(String word, final String meaning, final int location) {

        dialog = new Dialog(this);
        /**
         *
         */

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.update_word);
        /**
         *
         * @param text
         */
        dialog.setTitle("Change the Word");

        final EditText origin = (EditText) dialog.findViewById(R.id.original);

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

                Lingo l;
                WordInfo wi;

                String org = origin.getText().toString();
                String mean = newWord.getText().toString();

                if (location != -1) {

                    wi = al.remove(location);

                    wi.setWord(org);
                    wi.setMeaning(mean);

                    List<Lingo> q;

                    q = Lingo.find(Lingo.class, "word = ?", org);

                    if (q.size() == 0) {
                        q = Lingo.listAll(Lingo.class);

                        Log.i("size", q.size() + "");

                        for (int i = 0; i < q.size(); i++) {
                            Log.d("Log number " + i, q.get(i).toString());
                        }

                        Log.i("Location", location + "");
                        l = q.get(location);

                    } else {
                        l = new Lingo();
                    }

                } else {
                    l = new Lingo();
                    wi = new WordInfo(org, mean);
                }

                l.word = org;
                l.meaning = mean;
                l.save();

                al.add(wi);

                Collections.sort(al, new InfoCompare());

                mAdapter = new WordAdapter(al, ListOfWords.this);

                mRecyclerView.setAdapter(mAdapter);

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

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setThemed() {
        SharedPreferences prefs = getSharedPreferences("theming", MODE_PRIVATE);
        String themer = prefs.getString("themeID", "0");
        setTheme(themer.equals("2") ? R.style.NightTheme1 : R.style.LightTheme);
        //boolean ? (if true) : (if false);
    }


}
