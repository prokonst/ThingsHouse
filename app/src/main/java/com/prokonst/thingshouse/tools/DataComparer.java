package com.prokonst.thingshouse.tools;

import com.prokonst.thingshouse.model.tables.Synced;

import java.util.Collection;
import java.util.HashMap;

public class DataComparer {
    private static HashMap<String, DataComparer> dataDict = null;
    private String objId;

    private Synced localObj;
    private Synced fireBaseObj;
    private ObjType objType;
    private ActionType actionType;
    private boolean isHandled;

    private DataComparer(String objId, ObjType objType) {
        this.objId = objId.toLowerCase();
        this.actionType = ActionType.UNKNOWN;
        this.isHandled = false;
        this.objType = objType;
    }

    public String getObjId() {
        return objId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public ObjType getObjType() {
        return objType;
    }

    public Synced getLocalObj() {
        return localObj;
    }

    public Synced getFireBaseObj() {
        return fireBaseObj;
    }

    public boolean getIsHandled() {
        return this.isHandled;
    }

    public void setIsHandled(boolean handled) {
        this.isHandled = handled;
    }

    private void compare(){

        // NO_CHANGE
        if(localObj == null && fireBaseObj == null){
            this.actionType = ActionType.NO_CHANGE;
            return;
        }

        // LOCAL_INSERT
        // FIRE_BASE_DELETE_PHYSICALLY
        if(localObj == null && fireBaseObj != null){

            if(fireBaseObj.getIsDeleted()){
                this.actionType = ActionType.FIRE_BASE_DELETE_PHYSICALLY;
            }else{
                this.actionType = ActionType.LOCAL_INSERT;
            }

            return;
        }

        // FIRE_BASE_INSERT
        // LOCAL_DELETE_PHYSICALLY
        if(localObj != null && fireBaseObj == null){

            if(localObj.getIsDeleted()){
                this.actionType = ActionType.LOCAL_DELETE_PHYSICALLY;
            }else{
                this.actionType = ActionType.FIRE_BASE_INSERT;
            }

            return;
        }

        // BOTH_DELETE_PHYSICALLY
        if(localObj.getIsDeleted() && fireBaseObj.getIsDeleted()) {
            this.actionType = ActionType.BOTH_DELETE_PHYSICALLY;
            return;
        }

        // LOCAL_UPDATE,
        // FIRE_BASE_UPDATE
        if(!localObj.getDataHash().equals(fireBaseObj.getDataHash())){
            int dateCompareResult = localObj.getDateChange().compareTo(fireBaseObj.getDateChange());
            boolean localObjIsLastUpdate = dateCompareResult > 0;

            if(localObjIsLastUpdate){
                this.actionType = ActionType.FIRE_BASE_UPDATE;
            }else{
                this.actionType = ActionType.LOCAL_UPDATE;
            }
            return;
        }

        // NO_CHANGE
        this.actionType = ActionType.NO_CHANGE;
    }

    public static void prepare(){
        if(dataDict == null)
            dataDict = new HashMap<>();
    }

    public static Collection<DataComparer> getAllDataComparers(){
        DataComparer.prepare();
        return DataComparer.dataDict.values();
    }

    public static void compareAll(){
        prepare();
        for(DataComparer curDC : DataComparer.dataDict.values()){
            curDC.compare();
        }
    }

    public static void finish(){
        dataDict = null;
    }

    public static DataComparer addObjToMap(Synced obj, DbType dbType, ObjType objType){
        DataComparer dataComparer = DataComparer.getDataComparerByObj(obj, objType);

        if(dbType.equals(DbType.LOCAL_OBJ)){
            dataComparer.localObj = obj;
        } else if(dbType.equals(DbType.FIRE_BASE_OBJ)) {
            dataComparer.fireBaseObj = obj;
        }

        return dataComparer;
    }

    private static DataComparer getDataComparerByObj(Synced obj, ObjType objType){
        DataComparer.prepare();

        String objId = obj.getId().toLowerCase();

        if(!DataComparer.dataDict.containsKey(objId)){
            DataComparer.dataDict.put(objId, new DataComparer(objId, objType));
        }

        return DataComparer.dataDict.get(objId);
    }

    public static DataComparer getDataComparerById(String objId){
        String objIdLC = objId.toLowerCase();

        if(DataComparer.dataDict.containsKey(objIdLC)){
            return DataComparer.dataDict.get(objIdLC);
        }

        return null;
    }


    public enum DbType {
        LOCAL_OBJ,
        FIRE_BASE_OBJ
    }

    public enum ObjType {
        THING,
        STORAGE
    }

    public enum ActionType {
        UNKNOWN,
        LOCAL_INSERT,
        FIRE_BASE_INSERT,
        LOCAL_UPDATE,
        FIRE_BASE_UPDATE,
        LOCAL_DELETE_PHYSICALLY,
        FIRE_BASE_DELETE_PHYSICALLY,
        BOTH_DELETE_PHYSICALLY,
        NO_CHANGE
    }

    public static int getMapSize(){
        return DataComparer.dataDict.size();
    }

    public static int getCountByObjType(DbType dbType){
        int res = 0;
        for(DataComparer curDataComparer : DataComparer.dataDict.values()){

            if(dbType.equals(DbType.LOCAL_OBJ) && curDataComparer.localObj != null){
                res++;
            } else if(dbType.equals(DbType.FIRE_BASE_OBJ) && curDataComparer.fireBaseObj != null) {
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
