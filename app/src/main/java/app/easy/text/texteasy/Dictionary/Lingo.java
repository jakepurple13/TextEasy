package app.easy.text.texteasy.Dictionary;

import com.orm.SugarRecord;

/**
 * Created by Jacob on 9/27/16.
 */
public class Lingo extends SugarRecord {

    String word;
    String meaning;

    public Lingo() {

    }

    public Lingo(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public String toString() {
        return word + " = " + meaning;
    }



}
