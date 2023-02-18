package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizWorkLog;
import org.jeecg.modules.mzx.mapper.BizWorkLogMapper;
import org.jeecg.modules.mzx.service.IBizWorkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizWorkLogService extends ServiceImpl<BizWorkLogMapper, BizWorkLog> implements IBizWorkLogService {

    @Autowired
    private BizWorkLogMapper workLogMapper;

}
