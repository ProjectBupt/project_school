package com.tongxin.youni.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by yangxiang on 2016/9/28.
 */
@AVClassName("Express")
public class Express extends AVObject {
    private String UserID;
    private String deliverID;
    private String InstallationId;
    private String userName;
    private String expressCompany;
    private String roomID;
    private String phone;
    private int state;
    private int size;
    private boolean isFragile;
    public String extra;
    private String time;
    private String  type;
    private int money;

    public Express(){
    }

    public Express(String userID, String installationId, String userName, String expressCompany, String roomID, String phone, String extra, int size, boolean isFragile) {
        setUserID(userID);
        setInstallationId(installationId);
        setUserName(userName);
        setExpressCompany(expressCompany);
        setRoomID(roomID);
        setPhone(phone);
        setState(ExpressDao.isWaiting);
        setSize(size);
        setExtra(extra);
        setFragile(isFragile);
    }

    public String getUserName() {
        return getString(ExpressDao.username);
    }

    public void setUserName(String userName) {
        put(ExpressDao.username,userName);
}

    public String getExpressCompany() {
        return getString(ExpressDao.company);
    }

    public void setExpressCompany(String expressCompany) {
        put(ExpressDao.company,expressCompany);
    }

    public String getRoomID() {
        return getString(ExpressDao.roomId);
    }

    public void setRoomID(String roomID) {
        put(ExpressDao.roomId,roomID);
    }

    public String getPhone() {
        return getString(ExpressDao.phone);
    }

    public void setPhone(String phone) {
        put(ExpressDao.phone,phone);
    }

    public String getInstallationId() {
        return getString(ExpressDao.InstallationId);
    }

    public void setInstallationId(String installationId) {
        put(ExpressDao.InstallationId,installationId);
    }

    public int getState() {
        return getInt(ExpressDao.state);
    }

    public void setState(int state) {
        put(ExpressDao.state, state);
    }

    public boolean isFragile() {
        return getBoolean(ExpressDao.isFragile);
    }

    public void setFragile(boolean fragile) {
        put(ExpressDao.isFragile,fragile);
    }

    public int getSize() {
        return getInt(ExpressDao.size);
    }

    public void setSize(int size) {
        put(ExpressDao.size,size);
    }

    public String getExtra() {
        return getString(ExpressDao.extra);
    }

    public void setExtra(String extra) {
        put(ExpressDao.extra,extra);
    }

    public String getUserID() {
        return getString(ExpressDao.userID);
    }

    public void setUserID(String userID) {
        put(ExpressDao.userID,userID);
    }

    public String getTime() {
        return getString(ExpressDao.time);
    }

    public void setTime(String time) {
        put(ExpressDao.time,time);
    }

    public String getDeliverID() {
        return getString(UserDao.diliverID);
    }

    public void setDeliverID(String deliverID) {
        put(UserDao.diliverID,deliverID);
    }

    public int getMoney() {
        return getInt(ExpressDao.money);
    }

    public void setMoney(int money) {
        put(ExpressDao.money,money);
    }

    public String getType() {
        return getString(ExpressDao.type);
    }

    public void setType(String type) {
        put(ExpressDao.type,type);
    }
}
