package com.prokonst.thingshouse.model.tables;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.prokonst.thingshouse.model.Authorization;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public interface Synced{

    //private String id;
    String getId();
    void setId(String id);

    //private Date dateChange;
    Date getDateChange();
    void setDateChange(Date dateChange);

    //private String dataHash;
    String getDataHash();
    void setDataHash(String dataHash);

    //private boolean isDeleted;
    boolean getIsDeleted();
    void setIsDeleted(boolean isDeleted);

    //private String userId;
    String getUserId();
    void setUserId(String userId);

    String getDataForHash();

    @RequiresApi(api = Build.VERSION_CODES.O)
    default void calculateHash() {
        String dataForHash = this.getDataForHash();
        String hash = Synced.calcHash(dataForHash);

        this.setDataHash(hash);
        this.setDateChange(new Date());
        this.setUserId(Authorization.getCurrentUser().getUid());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static String calcHash(String dataStr){
        if(dataStr == null)
            dataStr = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(dataStr.getBytes());
            byte[] digest = md.digest();
            byte[] digestBase64 = Base64.getEncoder().encode(digest);
            String hash = new String(digestBase64);
            return hash;
        } catch (Exception ex) {
            Log.e(ex.getClass().getName(), ex.getMessage());
            return  null;
        }
    }




}
