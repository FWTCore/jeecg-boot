package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectType;
import org.jeecg.modules.mzx.mapper.BizProjectTypeMapper;
import org.jeecg.modules.mzx.service.IBizProjectTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目类型服务
 *
 * @author xcom
 * @date 2024/1/31
 */

@Service
@Slf4j
public class BizProjectTypeService extends ServiceImpl<BizProjectTypeMapper, BizProjectType> implements IBizProjectTypeService {


    @Autowired
    private BizProjectTypeMapper projectTypeMapper;







}
