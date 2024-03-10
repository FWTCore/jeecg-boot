package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mzx.entity.BizProjectBillingCommission;
import org.jeecg.modules.mzx.mapper.BizProjectBillingCommissionMapper;
import org.jeecg.modules.mzx.service.IBizProjectBillingCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.List;

/**
 * 项目结算提成
 *
 * @author xcom
 * @date 2024/3/4
 */

@Service
@Slf4j
public class BizProjectBillingCommissionService extends ServiceImpl<BizProjectBillingCommissionMapper, BizProjectBillingCommission> implements IBizProjectBillingCommissionService {

    @Autowired
    private BizProjectBillingCommissionMapper projectBillingCommissionMapper;

    @Override
    public void generateProjectBillingCommission(String projectId) {
        if (StringUtils.isBlank(projectId)) {
            return;
        }
        projectBillingCommissionMapper.generateProjectBillingCommission(projectId);
    }

    @Override
    public void updateProjectBillingCommissionFinish(List<String> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return;
        }
        Calendar instance = Calendar.getInstance();
        Integer period = instance.get(Calendar.YEAR) * 100 + instance.get(Calendar.MONTH) + 1;
        // 20号发提成，20号之后，算到下个月
        if (instance.get(Calendar.DAY_OF_MONTH) > 20) {
            period += 1;
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        projectBillingCommissionMapper.updateProjectBillingCommissionFinish(projectIds, period, instance.getTime(), user.getRealname());
    }
}
