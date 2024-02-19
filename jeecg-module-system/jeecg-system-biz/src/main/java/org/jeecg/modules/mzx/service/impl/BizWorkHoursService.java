package org.jeecg.modules.mzx.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.modules.mzx.mapper.BizWorkHoursMapper;
import org.jeecg.modules.mzx.service.IBizWorkHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 工时服务
 *
 * @author xcom
 * @date 2024/2/18
 */

@Service
@Slf4j
public class BizWorkHoursService implements IBizWorkHoursService {


    @Autowired
    private BizWorkHoursMapper bizWorkHoursMapper;


    @Override
    public BigDecimal getTotalWorkHours(String staffId, Date startTime, Date endTime) {
        if (ObjectUtils.isEmpty(startTime) || ObjectUtils.isEmpty(endTime)) {
            return BigDecimal.ZERO;
        }
        return bizWorkHoursMapper.getTotalWorkHours(staffId, startTime, endTime);
    }
}
