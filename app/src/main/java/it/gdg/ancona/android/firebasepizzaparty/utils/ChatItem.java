package it.gdg.ancona.android.firebasepizzaparty.utils;

import android.os.Build;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Libero Rignanese.
 * read license file for more informations.
 */


public class ChatItem {

    public ChatItem(){//MUST senza parametri per FIRBASE getValue(ChatItem.class)
        super();
    }

    private ChatItem(String username, String message){
        super();
        this.username = username;
        this.message = message;
        this.time = System.currentTimeMillis();
        this.serial = getSerial();
    }

    public boolean itsMe(){
        return getSerial().equals(serial);
    }

    public String username = "";

    public String message = "";

    public long time = -1;

    public String serial = "";

    public static ChatItem loadFromDataSnaphot(DataSnapshot dataSnapshot){
        return dataSnapshot.getValue(ChatItem.class);
    }

    public static void send(DatabaseReference reference, String username, String message){
        ChatItem chatItem = new ChatItem(username, message);
        reference.push().setValue(chatItem);
    }

    private static String getSerial(){
        String serial = Build.SERIAL;
        if(serial == null || serial.equals(Build.UNKNOWN)){
            serial = ChatUtils.getMacAddress();
        }
        return serial;
    }

    public String getDate(){
        Date date = new Date(time);
        SimpleDateFormat fmtOut = new SimpleDateFormat("KK:mm");
        return fmtOut.format(date);
    }





}
