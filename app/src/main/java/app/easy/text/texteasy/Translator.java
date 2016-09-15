package app.easy.text.texteasy;

import java.util.LinkedHashMap;

/**
 * Created by Jacob on 9/13/16.
 */
public class Translator {



    LinkedHashMap<String, String> hm;

    public Translator() {

        hm = new LinkedHashMap<>();
        /**
         *
         * Put all words into hashmap here
         *
         */

        hm.put("brb", "be right back");
        hm.put("hi", "hello world");
        hm.put("Good", "Amazing");

    }

    public String translate(String words) {


        String[] sentence = words.split(" ");

        for(int i=0;i<sentence.length;i++) {
            if(hm.containsKey(sentence[i])) {
                sentence[i] = hm.get(sentence[i]);
            }
        }

        String text = "";

        for(int i=0;i<sentence.length;i++) {
            text+=sentence[i] + " ";
        }

        return text;
    }

}
