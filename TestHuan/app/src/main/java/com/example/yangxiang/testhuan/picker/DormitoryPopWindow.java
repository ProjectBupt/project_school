package com.example.yangxiang.testhuan.picker;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diycoder.citypick.OnWheelChangedListener;
import com.diycoder.citypick.WheelView;
import com.diycoder.citypick.adapters.ArrayWheelAdapter;
import com.example.yangxiang.testhuan.R;
import com.example.yangxiang.testhuan.picker.model.BuildingModel;
import com.example.yangxiang.testhuan.picker.service.MyXmlParserHandler;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by 宽伟 on 2016/10/7.
 */

public class DormitoryPopWindow extends PopupWindow implements OnWheelChangedListener {

    private final Display display;
    private final WindowManager windowManager;
    
    protected String[] mBuildings = {"A","B","C","D","E","F","H","I","J","K"};
    protected String[] mDormitoryNumbers = {"101","102","103","104","105","106","108","109","111","112","113"};
    private View mView;
    private Activity mContext;

    private TextView slected;

    //当前的宿舍信息
    private String mCurrentBuilding;
    private String mCurrentFloor;
    private String mCurrentDormitoryNumber;


    private String mBuilding="";
    private String mDormitoryNumber="";

    private String divider = "-";//分隔符
    private String pickValue = "";//返回数据

    private Button closePopWindow;
    private Button showValue;
    private WheelView mBuildingView;
    private WheelView mDormitoryView;

    private DormitoryListener mDormitoryListener;

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        } else {
            this.dismiss();
        }
    }

    public DormitoryPopWindow(Activity activity){
        super(activity);
        this.mContext = activity;

        initDataFromXML();

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.dormitory_picker2,null);

        this.setContentView(mView);

        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();

        this.setWidth(RelativeLayout.LayoutParams.FILL_PARENT);
        //设置CityPickerPopWindow弹出窗体的高
        this.setHeight((int) (display.getHeight() * 0.3));
        //设置CityPickerPopWindow弹出窗体可点击
        this.setFocusable(true);
        //设置CityPickerPopWindow可触摸
        this.setTouchable(true);
        //设置CityPickerPopWindow弹出窗体动画效果
        this.setAnimationStyle(com.diycoder.citypick.R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        //设置CityPickerPopWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(false);
        setUpListener(mView);//设置监听事件

        setData();//设置数据
    }

    private void initDataFromXML() {
        List<BuildingModel> buildingModels = null;

        AssetManager asset = mContext.getAssets();
        try{
            InputStream input= asset.open("dormitory_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            MyXmlParserHandler handler = new MyXmlParserHandler();
            parser.parse(input, handler);
            input.close();
            buildingModels = handler.getDataList();//得到解析后的数据



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


    }

    private void setData() {
        mBuildingView.setViewAdapter(new ArrayWheelAdapter<>(mContext, mBuildings));
        mBuildingView.setVisibleItems(6);
        //mBuildingView.setCurrentItem(0);
        mDormitoryView.setVisibleItems(6);
        // mViewDistrict.setVisibleItems(7);
        updateDormitoryNumber();
    }

    private void setUpListener(View mView) {
        slected = (TextView) mView.findViewById(R.id.selectValue);
        closePopWindow = (Button) mView.findViewById(R.id.mClosePop);
        showValue = (Button) mView.findViewById(R.id.mShowValue);
        mBuildingView  = (WheelView) mView.findViewById(R.id.id_building);
        mDormitoryView = (WheelView) mView.findViewById(R.id.id_dormitoryNumber);

        mBuildingView.addChangingListener(this);
        mDormitoryView.addChangingListener(this);

        closePopWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        showValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDormitoryListener!=null)
                     mDormitoryListener.pidkValue(pickValue);
                dismiss();
            }
        });
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if(wheel==mBuildingView)
        {
            updateDormitoryNumber();//updateBuilding();
            mBuilding = mBuildings[newValue];
            slected.setText(mBuilding);
        }
        else if(wheel==mDormitoryView)
        {

             mDormitoryNumber = mDormitoryNumbers[newValue];
        }
        pickValue = mBuilding + divider + mDormitoryNumber;
        slected.setText(pickValue);
    }

    private void updateDormitoryNumber() {
        //int pCurrent = mDormitoryView.getCurrentItem();
        mDormitoryView.setViewAdapter(new ArrayWheelAdapter<>(mContext, mDormitoryNumbers));
        mDormitoryView.setCurrentItem(0);
        //pickValue = mBuildings[0] +divider + mDormitoryNumbers[0];
    }

    public interface DormitoryListener{
        void pidkValue(String value);
    }

    public void setDormitoryListener(DormitoryListener dormitoryListener){
        this.mDormitoryListener = dormitoryListener;
    }
}
