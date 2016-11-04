package com.example.yangxiang.testhuan.picker.model;

import java.util.List;

/**
 * Created by 宽伟 on 2016/10/9.
 */

public class FloorModel {
    private String name;
    private List<DormitoryModel> dormitoryNumberList;

    public FloorModel(){
        super();
    }

    public FloorModel(String name, List<DormitoryModel> list){
        super();
        this.name = name;
        this.dormitoryNumberList =list;
    }

    public List<DormitoryModel> getDormitoryNumberList() {
        return dormitoryNumberList;
    }

    public void setDormitoryNumberList(List<DormitoryModel> dormitoryNumberList) {
        this.dormitoryNumberList = dormitoryNumberList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FloorModel[name="+name+",dormitoryNumberList"+dormitoryNumberList+"]";
    }
}
