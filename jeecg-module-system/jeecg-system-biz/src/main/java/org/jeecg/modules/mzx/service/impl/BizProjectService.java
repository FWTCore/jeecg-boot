package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.mapper.BizProjectMapper;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizProjectService extends ServiceImpl<BizProjectMapper, BizProject> implements IBizProjectService {
}
