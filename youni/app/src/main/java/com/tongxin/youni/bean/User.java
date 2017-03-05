package com.tongxin.youni.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/17.
 */
public class User extends AVUser implements Parcelable {
    private String avatar;
    private String mCampus;
    private String sex;
    private String mAddress;
    private String roomID;
    private String studentID;
    private String InstallationId;
    private int QuantityOfOrder=0;
    private int QuantityOfAsking=0;
    private int Credit=0;
    private List<Express> ask_expresses=new ArrayList<>();

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

    public String getSex() {
        return getString(UserDao.SEX);
    }

    public void setSex(String sex) {
        put(UserDao.SEX,sex);
    }

    public String getBackground() {
        return getString(UserDao.BACKGROUND);
    }

    public void setBackground(String background) {
        put(UserDao.BACKGROUND, background);
    }

    public String getAvatar() {
        return getString(UserDao.AVATARURL);
    }

    public void setAvatar(String avatar) {
        put(UserDao.AVATARURL, avatar);
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

    public int getQuantityOfOrder() {
        return (int) getNumber(UserDao.QUANTITY_OF_ORDER);
    }

    public void setQuantityOfOrder(int quantityOfOrder) {
        put(UserDao.QUANTITY_OF_ORDER,quantityOfOrder);
    }

    public int getQuantityOfAsking() {
        return (int) getNumber(UserDao.QUANTITY_OF_ASKING);
    }

    public void setQuantityOfAsking(int quantityOfAsking) {
        put(UserDao.QUANTITY_OF_ASKING,quantityOfAsking);
    }

    public int getCredit() {
        return (int) getNumber(UserDao.CREDIT);
    }

    public void setCredit(int credit) {
        put(UserDao.CREDIT,credit);
    }

//    public List<Express> getAskExpress(){
//        List<Express> ask_expresses;
//        AVRelation<Express> relation=this.getRelation("add");
//        AVQuery<Express> query=relation.getQuery();
//        query.findInBackground(new FindCallback<Express>() {
//            @Override
//            public void done(List<Express> list, AVException e) {
//                if (e==null){
//                    if (list.size()!=0){
//                        ask_expresses=list;
//                    }else{
//                        Log.d("User.getAskExpress","Has no ask_express");
//                    }
//                }else{
//                    Log.e("User.getAskExpress","Error:"+e);
//                }
//            }
//        });
//        return ask_expresses;
//    }
//
//    public List<Express> getFetchExpress(){
//        final List<Express> express_list=new ArrayList<>();
//        AVRelation<Express> relation=this.getRelation("fetch");
//        AVQuery<Express> query=relation.getQuery();
//        query.findInBackground(new FindCallback<Express>() {
//            @Override
//            public void done(List<Express> list, AVException e) {
//                if (e==null){
//                    if (list.size()!=0) {
//                        for (Express express : list) {
//                            express_list.add(express);
//                        }
//                    }
//                }else{
//                    Log.e("User.getFetchExpress","Error:"+e);
//                }
//            }
//        });
//        return express_list;
//    }
}
