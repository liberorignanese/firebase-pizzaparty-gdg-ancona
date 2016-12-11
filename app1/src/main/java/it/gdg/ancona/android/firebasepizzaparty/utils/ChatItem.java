package it.gdg.ancona.android.firebasepizzaparty.utils;

import android.os.Build;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Libero Rignanese.
 * read license file for more informations.
 */


public class ChatItem {

    public String username = "";

    public String message = "";

    public long time = -1;


    public ChatUser user;

    public ChatItem() {//MUST senza parametri per FIRBASE getValue(ChatItem.class)
        super();
    }

    private ChatItem(String username, String message, String displayName, String email, String photoURL, String uid) {
        super();
        this.username = username;
        this.message = message;
        this.time = System.currentTimeMillis();
        this.user = new ChatUser();
        this.user.displayName = displayName;
        this.user.email = email;
        this.user.photoURL = photoURL;
        this.user.uid = uid;
    }

    public boolean itsMe() {
        return true;
    }

    public static ChatItem loadFromDataSnaphot(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(ChatItem.class);
    }

    public void send(DatabaseReference reference) {
        reference.push().setValue(this);
    }


    public String getDate() {
        Date date = new Date(time);
        SimpleDateFormat fmtOut = new SimpleDateFormat("KK:mm", Locale.getDefault());
        return fmtOut.format(date);
    }

}