package it.gdg.ancona.android.firebasepizzaparty.utils;

import com.google.firebase.database.DataSnapshot;
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

    public static ChatItem loadFromDataSnaphot(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(ChatItem.class);
    }
}