package com.example.yangxiang.testhuan.picker.service;

import android.util.Log;

import com.example.yangxiang.testhuan.picker.model.BuildingModel;
import com.example.yangxiang.testhuan.picker.model.DormitoryModel;
import com.example.yangxiang.testhuan.picker.model.FloorModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 宽伟 on 2016/10/9.
 *
 * 此类用来存放解析后的XML数据
 *
 */

public class MyXmlParserHandler extends DefaultHandler {

    private static final String TAG = "XmlParser------->";

    private List<BuildingModel> list = new ArrayList<>();

    public MyXmlParserHandler(){
        super();
    }


    public List<BuildingModel> getDataList() {
        return list;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    BuildingModel buildingModel = new BuildingModel();
    FloorModel floorModel = new FloorModel();
    DormitoryModel dormitoryModel =new DormitoryModel();



    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if(qName.equals("building")){
            buildingModel = new BuildingModel();
            buildingModel.setName(attributes.getValue(0));
            buildingModel.setFloorModelList(new ArrayList<FloorModel>());
        }
        else if(qName.equals("floor")){
            floorModel = new FloorModel();
            floorModel.setName(attributes.getValue(0));
            floorModel.setDormitoryNumberList(new ArrayList<DormitoryModel>());
            Log.i(TAG, "startElement: "+"yi tian jia");
        }
        else if (qName.equals("dormitory")){
            dormitoryModel = new DormitoryModel();
            dormitoryModel.setName(attributes.getValue(0));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if(qName.equals("building")){
           list.add(buildingModel);
        }
        else if(qName.equals("floor")){
            buildingModel.getFloorModelList().add(floorModel);
        }
        else if (qName.equals("dormitory")){
            Log.i(TAG, "endElement: "+(floorModel.getDormitoryNumberList() == null));
            floorModel.getDormitoryNumberList().add(dormitoryModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

}
