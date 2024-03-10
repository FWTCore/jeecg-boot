package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mzx.entity.BizProjectBilling;
import org.jeecg.modules.mzx.mapper.BizProjectBillingMapper;
import org.jeecg.modules.mzx.model.BizProjectBillingModel;
import org.jeecg.modules.mzx.model.BizProjectBillingVO;
import org.jeecg.modules.mzx.service.IBizProjectBillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目结算
 *
 * @author xcom
 * @date 2024/3/4
 */

@Service
@Slf4j
public class BizProjectBillingService extends ServiceImpl<BizProjectBillingMapper, BizProjectBilling> implements IBizProjectBillingService {

    @Autowired
    private BizProjectBillingMapper projectBillingMapper;


    @Override
    public List<BizProjectBillingModel> listBizProjectBillingModel() {
        return projectBillingMapper.listBizProjectBillingModel();
    }

    @Override
    public void batchInsertBizProjectBilling(List<BizProjectBillingModel> data) {
        if (CollectionUtil.isEmpty(data)) {
            return;
        }
        projectBillingMapper.batchInsertBizProjectBilling(data);
    }

    @Override
    public IPage<BizProjectBillingVO> pageProjectBilling(Page<BizProjectBilling> page, BizProjectBilling query) {
        return projectBillingMapper.pageProjectBilling(page, query);
    }

    @Override
    public void updateProjectBillingFinish(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        projectBillingMapper.updateProjectBillingFinish(ids, user.getRealname());
    }
}
