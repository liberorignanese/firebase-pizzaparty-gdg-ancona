package it.gdg.ancona.android.firebasepizzaparty.utils;

/**
 * Created by Libero Rignanese.
 * read license file for more informations.
 */


public class ChatUser {

    public ChatUser() {//MUST senza parametri per FIRBASE getValue(ChatItem.class)
        super();
    }

    public String displayName = "";
    public String email = "";
    public String photoURL = "";
    public String uid = "";

}
