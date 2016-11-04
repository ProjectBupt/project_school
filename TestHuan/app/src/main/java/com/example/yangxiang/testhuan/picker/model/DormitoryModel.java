package com.example.yangxiang.testhuan.picker.model;
/**
 * Created by 宽伟 on 2016/10/9.
 */

public class DormitoryModel {
    private String name;

    public DormitoryModel(){
        super();
    }

    public DormitoryModel(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String dormitoryNumber) {
        this.name = dormitoryNumber;
    }

    @Override
    public String toString() {
        return "DormitoryModel[name="+name+"]";
        //return  "DistrictModel [name=" + name + ", zipcode=" + zipcode + "]";
    }
}
