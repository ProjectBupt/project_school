package com.hyphenate.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.avos.avoscloud.AVUser;

/**
 * Created by Administrator on 2016/4/17.
 */
public class User extends AVUser implements Parcelable {
    private String nick;
    private String avatar;
    private String mCampus;
    private String sex;
    private String mAddress;
    private String roomID;
    private String studentID;
    private String InstallationId;

    public User() {
    }


    protected User(Parcel in) {
        this.avatar = in.readString();
        this.mCampus = in.readString();
        this.sex = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.mCampus);
        dest.writeString(this.sex);
    }

    public String getLoverAvatar() {
        return getString(UserDao.LOVERAVATAR);
    }

    public void setLoverAvatar(String loverAvatar) {
        put(UserDao.LOVERAVATAR, loverAvatar);
    }

    public String getLoverNick() {
        return getString(UserDao.LOVERNICK);
    }

    public void setLoverNick(String loverNick) {
        put(UserDao.LOVERNICK, loverNick);
    }

    public String getBackground() {
        return getString(UserDao.BACKGROUND);
    }

    public void setBackground(String background) {
        put(UserDao.BACKGROUND, background);
    }


    public String getNick() {
        return getString(UserDao.NICK);
    }

    public void setNick(String nick) {
        put(UserDao.NICK, nick);
    }

    public String getAvatar() {
        return getString(UserDao.AVATARURL);
    }

    public void setAvatar(String avatar) {
        put(UserDao.AVATARURL, avatar);
    }

    public String getInstallationId() {
        return getString(UserDao.INSTALLATIONID);
    }

    public void setInstallationId(String installationId) {
        put(UserDao.INSTALLATIONID, installationId);
    }

    public String getRoomID() {
        return getString(UserDao.ROOM_ID);
    }

    public void setRoomID(String roomID) {
        put(UserDao.ROOM_ID,roomID);
    }

    public String getStudentID() {
        return getString(UserDao.STUDENT_ID);
    }

    public void setStudentID(String studentID) {
        put(UserDao.STUDENT_ID,studentID);
    }

    public String getmAddress() {
        return getString(UserDao.ADDRESS);
    }

    public void setmAddress(String mAddress) {
        put(UserDao.ADDRESS,mAddress);
    }
}
