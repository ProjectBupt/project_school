package com.tongxin.youni.picker;

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

import com.diycoder.citypick.OnWheelChangedListener;
import com.diycoder.citypick.WheelView;
import com.diycoder.citypick.adapters.ArrayWheelAdapter;
import com.tongxin.youni.R;
import com.tongxin.youni.picker.model.BuildingModel;
import com.tongxin.youni.picker.model.DormitoryModel;
import com.tongxin.youni.picker.model.FloorModel;
import com.tongxin.youni.picker.service.MyXmlParserHandler;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Created by 宽伟 on 2016/10/9.
 */

public class DormitoryPopWindow2 extends PopupWindow implements OnWheelChangedListener {

    private final Display display;
    private final WindowManager windowManager;

    protected String[] mBuildingsDatas;
    private View mView;
    private Activity mContext;

    private TextView slected;//当前选中的组合

    //当前的宿舍信息
    private String mCurrentBuilding;
    private String mCurrentFloor;
    private String mCurrentDormitoryNumber;

    protected Map<String, String[]> BuildingMap = new HashMap<String, String[]>();
    /**
     */
    protected Map<String, String[]> FloorMap = new HashMap<String, String[]>();

    /**
     */
    protected Map<String, String[]> DormitoryMap = new HashMap<String, String[]>();


    private String mBuilding="";
    private String mFloor = "";
    private String mDormitoryNumber="";

    private String divider = "-";//分隔符
    private String pickValue = "";//返回数据

    private Button closePopWindow;
    private Button showValue;
    private WheelView mBuildingView;
    private WheelView mFloorView;
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

    public DormitoryPopWindow2(Activity activity){
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
            if (buildingModels != null && !buildingModels.isEmpty()) {
                mCurrentBuilding = buildingModels.get(0).getName();
                List<FloorModel> floorList = buildingModels.get(0).getFloorModelList();
                if (floorList != null && !floorList.isEmpty()) {
                    mCurrentFloor = floorList.get(0).getName();
                    List<DormitoryModel> dormitoryList = floorList.get(0).getDormitoryNumberList();
                    mCurrentDormitoryNumber = dormitoryList.get(0).getName();
                }
            }
            //*/
            mBuildingsDatas = new String[buildingModels.size()];
            for (int i = 0; i < buildingModels.size(); i++) {
                mBuildingsDatas[i] = buildingModels.get(i).getName();
                List<FloorModel> floorList = buildingModels.get(i).getFloorModelList();
                String[] floorNames = new String[floorList.size()];
                for (int j = 0; j < floorList.size(); j++) {
                    floorNames[j] = floorList.get(j).getName();
                    List<DormitoryModel> dormiitoryList = floorList.get(j).getDormitoryNumberList();
                    String[] dormitoryNameArray = new String[dormiitoryList.size()];
                    DormitoryModel[] doemitoryArray = new DormitoryModel[dormiitoryList.size()];
                    for (int k = 0; k < dormiitoryList.size(); k++) {
                        DormitoryModel dormitoryModel = new DormitoryModel(dormiitoryList.get(k).getName());
                        doemitoryArray[k] = dormitoryModel;
                        dormitoryNameArray[k] = dormitoryModel.getName();
                    }
                    DormitoryMap.put(floorNames[j], dormitoryNameArray);
                }
                FloorMap.put(buildingModels.get(i).getName(), floorNames);
        }
    }
        catch (SAXException e1) {
            e1.printStackTrace();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }finally {

        }
        }

    private void setData() {
        mBuildingView.setViewAdapter(new ArrayWheelAdapter<>(mContext, mBuildingsDatas));
        mBuildingView.setVisibleItems(6);
        mFloorView.setVisibleItems(6);
        //mBuildingView.setCurrentItem(0);
        mDormitoryView.setVisibleItems(6);
        //mViewDistrict.setVisibleItems(7);
        updateFloor();
        updateDormitoryNumber();

    }

    private void updateFloor() {
        int pCurrent = mBuildingView.getCurrentItem();
        mCurrentBuilding = mBuildingsDatas[pCurrent];
        String[] floors = FloorMap.get(mCurrentBuilding);//得到字符串
        if (floors == null) {
            floors = new String[]{""};
        }
        mFloorView.setViewAdapter(new ArrayWheelAdapter<>(mContext, floors));
        mFloorView.setCurrentItem(0);
        updateDormitoryNumber();
    }

    private void updateDormitoryNumber() {
        int pCurrent = mFloorView.getCurrentItem();
        mCurrentFloor = FloorMap.get(mCurrentBuilding)[pCurrent];
        String[] areas = DormitoryMap.get(mCurrentFloor);
        if (areas == null) {
            areas = new String[]{""};
        }
        //int pCurrent = mDormitoryView.getCurrentItem();
        mDormitoryView.setViewAdapter(new ArrayWheelAdapter<>(mContext, areas));
        mDormitoryView.setCurrentItem(0);
        //pickValue = mBuildings[0] +divider + mDormitoryNumbers[0];
    }


    private void setUpListener(View mView) {
        slected = (TextView) mView.findViewById(R.id.selectValue);
        closePopWindow = (Button) mView.findViewById(R.id.mClosePop);
        showValue = (Button) mView.findViewById(R.id.mShowValue);

        mBuildingView  = (WheelView) mView.findViewById(R.id.id_building);
        mFloorView = (WheelView) mView.findViewById(R.id.id_floor);
        mDormitoryView = (WheelView) mView.findViewById(R.id.id_dormitoryNumber);

        mBuildingView.addChangingListener(this);
        mFloorView.addChangingListener(this);
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
                     mDormitoryListener.pickValue2(pickValue);
                dismiss();
            }
        });
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if(wheel==mBuildingView)
        {
            updateFloor();//updateBuilding();
        }
        else if(wheel == mFloorView){
            updateDormitoryNumber();
        }
        else if(wheel==mDormitoryView)
        {
            mCurrentDormitoryNumber = DormitoryMap.get(mCurrentFloor)[newValue];

             //mDormitoryNumber = mDormitoryNumbers[newValue];
        }
        pickValue = mCurrentBuilding + divider + mCurrentFloor+mCurrentDormitoryNumber;
        slected.setText(pickValue);
    }

    public interface DormitoryListener{
        void pickValue2(String value);
    }

    public void setDormitoryListener(DormitoryListener dormitoryListener){
        this.mDormitoryListener = dormitoryListener;
    }
}
