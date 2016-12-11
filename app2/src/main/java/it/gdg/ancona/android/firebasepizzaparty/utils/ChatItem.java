package it.gdg.ancona.android.firebasepizzaparty.utils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Libero Rignanese.
 * read license file for more informations.
 */


public class ChatItem {

    public String message = "";

    public long time = -1;


    public ChatUser user;

    public ChatItem() {//MUST senza parametri per FIRBASE getValue(ChatItem.class)
        super();
    }

    public boolean itsMe(FirebaseUser currentUser) {
        return currentUser.getUid().equals(this.user.uid);
    }

    public static ChatItem loadFromDataSnaphot(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(ChatItem.class);
    }

    public String getDate() {
        Date date = new Date(time);
        SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return fmtOut.format(date);
    }

}