package org.jeecg.modules.mzx.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.UFTAAPartner;
import org.jeecg.modules.mzx.enums.ThirdCustomerEnum;
import org.jeecg.modules.mzx.mapper.BizCustomerMapper;
import org.jeecg.modules.mzx.model.CustomerSyncModel;
import org.jeecg.modules.mzx.service.IBizCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BizCustomerService extends ServiceImpl<BizCustomerMapper, BizCustomer> implements IBizCustomerService {

    @Autowired
    private BizCustomerMapper customerServiceMapper;

    @Override
    @DS("multi-datasource1")
    public List<UFTAAPartner> getUFTAAPartnerList(CustomerSyncModel param) {
        return customerServiceMapper.getUFTAAPartnerList(param);
    }

    @Override
    @DS("master")
    public void synUFTCustomer(List<UFTAAPartner> dataList) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (!CollectionUtils.isEmpty(dataList) && dataList.size() > 0) {
            List<String> thirdIds = dataList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<BizCustomer> existData = customerServiceMapper.getCustomerList(thirdIds, ThirdCustomerEnum.UFT.getKey());
            // 更新
            List<BizCustomer> updateData = existData.stream().map(e -> {
                UFTAAPartner updateTemp = dataList.stream().filter(t -> e.getThirdId().equals(t.getId()))
                        .findFirst().get();
                e.setCustomerCode(updateTemp.getCode());
                e.setCustomerName(updateTemp.getName());
                e.setUpdateBy(sysUser.getUsername());
                e.setUpdateTime(new Date());
                return e;
            }).collect(Collectors.toList());
            try {
                this.updateBatchById(updateData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 新增
            List<String> existThirdIds = existData.stream().map(e -> e.getThirdId()).collect(Collectors.toList());
            List<BizCustomer> insertData = dataList.stream().filter(e -> !existThirdIds.contains(e.getId()))
                    .map(item -> {
                        BizCustomer tempData = new BizCustomer();
                        tempData.setId(UUID.randomUUID().toString().replace("-", ""));
                        tempData.setCustomerCode(item.getCode());
                        tempData.setCustomerName(item.getName());
                        tempData.setDataSource(ThirdCustomerEnum.UFT.getKey());
                        tempData.setDelFlag(CommonConstant.DEL_FLAG_0);
                        tempData.setCreateTime(new Date());
                        tempData.setCreateBy(sysUser.getUsername());
                        tempData.setThirdId(item.getId());
                        tempData.setUpdateBy(sysUser.getUsername());
                        return tempData;
                    }).collect(Collectors.toList());
            try {
                this.saveBatch(insertData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
