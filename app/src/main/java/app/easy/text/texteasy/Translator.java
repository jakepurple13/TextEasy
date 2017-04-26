package app.easy.text.texteasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import app.easy.text.texteasy.Dictionary.Lingo;


/**
 * Created by Jacob on 9/13/16.
 */
public class Translator {

    //The dictionary
    LinkedHashMap<String, String> hm;
    boolean firsttime;
    boolean amITranslating = true;


    public Translator(Context context) {

        hm = new LinkedHashMap<>();

        SharedPreferences load = context.getSharedPreferences("FirstLoad", Context.MODE_PRIVATE);
        firsttime = load.getBoolean("FirstLoad", true);
        SharedPreferences load1 = PreferenceManager.getDefaultSharedPreferences(context);
        amITranslating = load1.getBoolean("translate_option", true);

        Log.e("Translator: ", firsttime + "");
        //firsttime
        if(true) {

            InputStreamReader is;

            if(Locale.getDefault().getLanguage().equals("de")) {
                is = new InputStreamReader(context.getResources().openRawResource(R.raw.germanwordlist));
            } else {
                is = new InputStreamReader(context.getResources().openRawResource(R.raw.wordlist));
            }

            BufferedReader br = new BufferedReader(is);

            String word = " ";
            String meaning = " ";
            while (word != null) {
                try {
                    word = br.readLine();
                    meaning = br.readLine();

                    if (word == null) {
                        break;
                    }

                    //Lingo l = new Lingo(word, meaning);
                    //l.save();

                    hm.put(word, meaning);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("asdfkjh", word + "\t" + meaning);

            }

            firsttime = false;

            SharedPreferences enter = context.getSharedPreferences("FirstLoad", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = enter.edit();
            editor.putBoolean("FirstLoad", firsttime);
            editor.apply();

        } else {

            List<Lingo> ll = Lingo.listAll(Lingo.class);

            for(int i=0;i<ll.size();i++) {
                hm.put(ll.get(i).getWord(), ll.get(i).getMeaning());
            }

            //Log.w("HM KEYS", hm.keySet().toString());
            //Log.e("Split", "-----------------");
            //Log.w("HM ENTRIES", hm.entrySet().toString());


        }

    }

    public String translate(String words) {
        //split the sentence apart
        String[] sentence = words.split(" ");
        //go through each element
        for(int i=0;i<sentence.length;i++) {
            //if the dictionary contains the word
            if(hm.containsKey(sentence[i])) {
                //change it out with the new translated one
                sentence[i] = hm.get(sentence[i]) + " (" + sentence[i] + ")";
            }
        }

        //Build the sentence back together
        String text = "";

        for(int i=0;i<sentence.length;i++) {
            text+=sentence[i] + " ";
        }

        if(amITranslating) {
            return text;
        } else {
            return words;
        }


    }

    public LinkedHashMap<String, String> getWords() {
        return hm;
    }

}


