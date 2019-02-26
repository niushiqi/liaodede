package com.dyyj.idd.chatmore.ui.user.picker;


import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

/**
 * TODO<json数据源>
 *
 * @author: 小嵩
 * @date: 2017/3/16 15:36
 */

public class JsonBean implements IPickerViewData {


    /**
     * name : 省份
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */

    private String name;
    private String codeId;
    private List<CityBean> city;

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCity() {
        return city;
    }

    public void setCity(List<CityBean> city) {
        this.city = city;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }


    public class CityBean implements IPickerViewData {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */

        private String name;
        private String codeId;
        private List<AreaBean> area;

        public String getCodeId() {
            return codeId;
        }

        public void setCodeId(String codeId) {
            this.codeId = codeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<AreaBean> getArea() {
            return area;
        }

        public void setArea(List<AreaBean> area) {
            this.area = area;
        }

        @Override
        public String getPickerViewText() {
            return getName();
        }
    }

    public static class AreaBean implements IPickerViewData {

        private String name;
        private String codeId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCodeId() {
            return codeId;
        }

        public void setCodeId(String codeId) {
            this.codeId = codeId;
        }

        @Override
        public String getPickerViewText() {
            return getName();
        }
    }
}
