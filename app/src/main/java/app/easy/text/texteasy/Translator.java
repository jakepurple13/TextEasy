package app.easy.text.texteasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import app.easy.text.texteasy.Dictionary.Lingo;


/**
 * Created by Jacob on 9/13/16.
 */
public class Translator {



    LinkedHashMap<String, String> hm;
    boolean firsttime;

    /**
     * 
     */
    /**
     * 
     */
    public Translator(Context context) {

        hm = new LinkedHashMap<>();
        /**
         *
         * Put all words into hashmap here
         *
         */
        SharedPreferences load = context.getSharedPreferences("FirstLoad", Context.MODE_PRIVATE);
        firsttime = load.getBoolean("FirstLoad", false);

        Log.e("Translator: ", firsttime + "");

        if(firsttime==false) {

            InputStreamReader is = new InputStreamReader(context.getResources().openRawResource(R.raw.wordlist));

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

                    Lingo l = new Lingo(word, meaning);
                    l.save();

                    hm.put(word, meaning);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("asdfkjh", word + "\t" + meaning);

            }

            firsttime = true;

            SharedPreferences enter = context.getSharedPreferences("FirstLoad", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = enter.edit();
            editor.putBoolean("FirstLoad", firsttime);
            editor.apply();

        } else {

            long size = Lingo.count(Lingo.class);

            Log.e("Translator: ", size + "");

            for(int i=1;i<=size-1;i++) {

                Lingo l = Lingo.findById(Lingo.class, i);
                hm.put(l.getWord(), l.getMeaning());

            }

            Log.w("HM KEYS", hm.keySet().toString());
            Log.e("Split", "-----------------");
            Log.w("HM ENTRIES", hm.entrySet().toString());


        }




    }

    /**
     * 
     * @param words 
     */
    public String translate(String words) {

        String[] sentence = words.split(" ");

        for(int i=0;i<sentence.length;i++) {
            if(hm.containsKey(sentence[i])) {
                sentence[i] = hm.get(sentence[i]) + " (" + sentence[i] + ")";
            }
        }

        String text = "";

        for(int i=0;i<sentence.length;i++) {
            text+=sentence[i] + " ";
        }

        return text;
    }

    public LinkedHashMap<String, String> getWords() {
        return hm;
    }

}


