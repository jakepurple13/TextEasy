package app.easy.text.texteasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
    String filename = "wordlisted.txt";

    ArrayList<String> words = new ArrayList<>();
    ArrayList<String> abb = new ArrayList<>();


    public Translator(Context context) {

        hm = new LinkedHashMap<>();

        boolean existing = false;

        filename = context.getResources().getString(R.string.filename);

        SharedPreferences load = context.getSharedPreferences("FirstLoad", Context.MODE_PRIVATE);
        firsttime = load.getBoolean("FirstLoad", true);
        SharedPreferences load1 = PreferenceManager.getDefaultSharedPreferences(context);
        amITranslating = load1.getBoolean("translate_option", true);

        Log.e("Translator: ", firsttime + "");
        //firsttime
        if (true) {

            InputStreamReader is;

            if (Locale.getDefault().getLanguage().equals("de")) {
                is = new InputStreamReader(context.getResources().openRawResource(R.raw.germanwordlist));
            } else {
                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
                File file;
                String path =
                        Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.folder_name);
                // Create the folder.
                File folder = new File(path);
                folder.mkdirs();
                // Create the file.
                file = new File(folder, filename);

                if (file1.exists()) {
                    try {
                        is = new InputStreamReader(new FileInputStream(file1));
                        setUpDictionary(is);
                        file1.delete();
                    } catch (FileNotFoundException e) {
                        //is = new InputStreamReader(context.getResources().openRawResource(R.raw.wordlist));
                        e.printStackTrace();
                    }
                }
                if (file.exists()) {
                    try {
                        is = new InputStreamReader(new FileInputStream(file));
                        Log.i("TRANSALTION", "Translator: " + file.getPath());
                        existing = true;
                        setUpDictionary(is);
                    } catch (FileNotFoundException e) {
                        //is = new InputStreamReader(context.getResources().openRawResource(R.raw.wordlist));
                    }
                }

                is = new InputStreamReader(context.getResources().openRawResource(R.raw.wordlist));
                setUpDictionary(is);
            }

            if (!existing) {
                writeToFile(words, abb);
            }

            firsttime = false;

            SharedPreferences enter = context.getSharedPreferences("FirstLoad", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = enter.edit();
            editor.putBoolean("FirstLoad", firsttime);
            editor.apply();

        } else {

            List<Lingo> ll = Lingo.listAll(Lingo.class);

            for (int i = 0; i < ll.size(); i++) {
                hm.put(ll.get(i).getWord(), ll.get(i).getMeaning());
            }
            //Log.w("HM KEYS", hm.keySet().toString());
            //Log.e("Split", "-----------------");
            //Log.w("HM ENTRIES", hm.entrySet().toString());

        }

    }

    private void setUpDictionary(InputStreamReader is) {
        BufferedReader br = new BufferedReader(is);
        String word = " ";
        String meaning = " ";
        while (word != null) {
            try {
                word = br.readLine();
                meaning = br.readLine();
                if(words.contains(word)) {
                    words.add(word);
                    abb.add(meaning);
                }

                if (word == null) {
                    break;
                }

                hm.put(word, meaning);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("asdfkjh", word + "\t" + meaning);

        }
    }

    public void writeToFile(ArrayList<String> word, ArrayList<String> abb) {
        // Get the directory for the user's public pictures directory.
        String path =
                Environment.getExternalStorageDirectory() + File.separator + "TextEasy";
        // Create the folder.
        File folder = new File(path);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, filename);

        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            for (int i = 0; i < word.size(); i++) {
                String text = word.get(i) + "\n" + abb.get(i) + "\n";
                myOutWriter.append(text);
            }
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String translate(String words) {
        //split the sentence apart
        String[] sentence = words.split(" ");
        //go through each element
        for (int i = 0; i < sentence.length; i++) {
            //if the dictionary contains the word
            if (hm.containsKey(sentence[i])) {
                //change it out with the new translated one
                sentence[i] = hm.get(sentence[i]) + " (" + sentence[i] + ")";
            }
        }

        //Build the sentence back together
        String text = "";

        for (int i = 0; i < sentence.length; i++) {
            text += sentence[i] + " ";
        }

        return text;

        /*if(amITranslating) {
            return text;
        } else {
            return words;
        }*/


    }

    public LinkedHashMap<String, String> getWords() {
        return hm;
    }

}


