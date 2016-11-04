package com.example.yangxiang.testhuan.picker.model;

import java.util.List;

/**
 * Created by 宽伟 on 2016/10/9.
 */

public class BuildingModel {
    private String name;
    private List<FloorModel> floorModelList;

    public BuildingModel(){
        super();
    }

    public BuildingModel(String name,List<FloorModel> floorModelList){
        super();
        this.name =name;
        this.floorModelList = floorModelList;
    }


    public List<FloorModel> getFloorModelList() {
        return floorModelList;
    }

    public void setFloorModelList(List<FloorModel> floorModelList) {
        this.floorModelList = floorModelList;
    }

    @Override
    public String toString() {
        //return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
        return "BuildingModel[name="+name+",floorModelList"+floorModelList+"]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
