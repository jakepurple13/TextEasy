package app.easy.text.texteasy.Tester;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import java.util.Locale;

/**
 * Created by Jacob on 4/4/17.
 */

public class ContactInfoTest {
    String name;
    String text;
    String number;
    boolean facebook;

    public ContactInfoTest(String name, String number, String text) {
        this.name = name;
        this.number = PhoneNumberUtils.normalizeNumber(number);
        this.text = text;
        facebook = false;
    }

    public ContactInfoTest(String name, String number, String text, boolean facebook) {
        this.name = name;
        this.number = PhoneNumberUtils.normalizeNumber(number);
        this.text = text;
        this.facebook = facebook;
    }


    /**
     *
     */
    @Override
    public String toString() {
        return name + ": " + PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry()) + "\n" + text;
    }

}
