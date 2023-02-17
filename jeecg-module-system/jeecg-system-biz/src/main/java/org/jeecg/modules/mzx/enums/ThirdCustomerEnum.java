package org.jeecg.modules.mzx.enums;

import org.jeecg.common.system.annotation.EnumDict;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.message.enums.RangeDateEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnumDict("customer_data_source")
public enum ThirdCustomerEnum {

    UFT(1, "T+");

    Integer key;

    String title;

    ThirdCustomerEnum(Integer key, String title) {
        this.key = key;
        this.title = title;
    }

    /**
     * 获取字典数据
     *
     * @return
     */
    public static List<DictModel> getDictList() {
        List<DictModel> list = new ArrayList<>();
        DictModel dictModel = null;
        for (ThirdCustomerEnum e : ThirdCustomerEnum.values()) {
            dictModel = new DictModel();
            dictModel.setValue(e.key.toString());
            dictModel.setText(e.title);
            list.add(dictModel);
        }
        return list;
    }

    public Integer getKey() {
        return this.key;
    }

}
