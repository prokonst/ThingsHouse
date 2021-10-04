package com.prokonst.thingshouse;

import java.util.UUID;

public class Utils {
    public static String generateUUIDStr() {
        return UUID.randomUUID().toString().toUpperCase();
    }
}
