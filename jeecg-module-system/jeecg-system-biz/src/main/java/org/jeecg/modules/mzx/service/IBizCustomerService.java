package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.UFTAAPartner;
import org.jeecg.modules.mzx.model.CustomerSyncModel;

import java.util.List;


public interface IBizCustomerService extends IService<BizCustomer> {

    /**
     * 获取T+用户
     * @param param
     * @return
     */
    List<UFTAAPartner> getUFTAAPartnerList(CustomerSyncModel param);

    /**
     * 同步T+用户
     */
    void synUFTCustomer(List<UFTAAPartner> dataList);

}
