package it.gdg.ancona.android.firebasepizzaparty.utils;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Libero Rignanese.
 * read license file for more informations.
 */


public class ChatUtils {

    static String getMacAddress() {
        try {
            ArrayList all = Collections.list(NetworkInterface.getNetworkInterfaces());
            Iterator var1 = all.iterator();

            while(var1.hasNext()) {
                NetworkInterface nif = (NetworkInterface)var1.next();
                if(nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if(macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    byte[] var5 = macBytes;
                    int var6 = macBytes.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        byte b = var5[var7];
                        String toAppend = Integer.toHexString(b & 255) + ":";
                        res1.append(toAppend.length() > 2?toAppend:"0" + toAppend);
                    }

                    if(res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }

                    return res1.toString();
                }
            }
        } catch (Exception var10) {

        }

        return "unknown";
    }
}
