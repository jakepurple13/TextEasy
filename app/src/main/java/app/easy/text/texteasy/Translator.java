package app.easy.text.texteasy;

import java.util.LinkedHashMap;

/**
 * Created by Jacob on 9/13/16.
 */
public class Translator {



    LinkedHashMap<String, String> hm;

    public Translator() {

        hm = new LinkedHashMap<>();



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
