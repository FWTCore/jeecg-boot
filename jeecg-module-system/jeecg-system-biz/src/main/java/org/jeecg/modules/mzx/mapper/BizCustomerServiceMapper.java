package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.UFTAAPartner;
import org.jeecg.modules.mzx.model.CustomerSyncModel;

import java.util.List;

public interface BizCustomerServiceMapper extends BaseMapper<BizCustomer> {

    List<UFTAAPartner> getUFTAAPartnerList(@Param("param") CustomerSyncModel param);

    List<BizCustomer> getCustomerList(@Param("thirdIds") List<String> thirdIds,@Param("dataSource") Integer dataSource);


}
