package com.prokonst.thingshouse.tools;

import com.prokonst.thingshouse.model.tables.Synced;

import java.util.HashMap;

public class DataComparer {
    private static HashMap<String, DataComparer> dataDict = null;
    private String objId;

    private Synced localObj;
    private Synced fireBaseObj;

    public static void prepare(){
        if(dataDict == null)
            dataDict = new HashMap<>();
    }

    public static void finish(){
        dataDict = null;
    }

    public static DataComparer addObjToMap(Synced obj, ObjType objType){
        DataComparer dataComparer = DataComparer.getDataComparerByObj(obj);

        if(objType.equals(ObjType.LOCAL_OBJ)){
            dataComparer.localObj = obj;
        } else if(objType.equals(ObjType.FIRE_BASE_OBJ)) {
            dataComparer.fireBaseObj = obj;
        }

        return dataComparer;
    }

    private static DataComparer getDataComparerByObj(Synced obj){
        DataComparer.prepare();

        String objId = obj.getId();

        if(!DataComparer.dataDict.containsKey(objId)){
            DataComparer.dataDict.put(objId, new DataComparer());
        }

        return DataComparer.dataDict.get(objId);
    }

    public enum ObjType {
        LOCAL_OBJ,
        FIRE_BASE_OBJ
    }

    public static int getMapSize(){
        return DataComparer.dataDict.size();
    }

    public static int getCountByObjType(ObjType objType){
        int res = 0;
        for(DataComparer curDataComparer : DataComparer.dataDict.values()){

            if(objType.equals(ObjType.LOCAL_OBJ) && curDataComparer.localObj != null){
                res++;
            } else if(objType.equals(ObjType.FIRE_BASE_OBJ) && curDataComparer.fireBaseObj != null) {
                res++;
            }
        }
        return res;
    }

    public static int getCountIntersect(){
        int res = 0;
        for(DataComparer curDataComparer : DataComparer.dataDict.values()){
            if(curDataComparer.localObj != null && curDataComparer.fireBaseObj != null){
                res++;
            }
        }
        return res;
    }
}
