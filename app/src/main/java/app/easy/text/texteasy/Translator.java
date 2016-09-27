package app.easy.text.texteasy;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import app.easy.text.texteasy.Dictionary.ListOfWords;

/**
 * Created by Jacob on 9/13/16.
 */
public class Translator {



    LinkedHashMap<String, String> hm;

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

        InputStreamReader is = new InputStreamReader(context.getResources().openRawResource(R.raw.wordlist));

        BufferedReader br = new BufferedReader(is);

        String word = " ";
        String meaning = " ";
        while(word!=null) {
            try {
                word = br.readLine();
                meaning = br.readLine();

                if(word==null) {
                    break;
                }

                hm.put(word, meaning);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("asdfkjh", word + "\t" + meaning);

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


