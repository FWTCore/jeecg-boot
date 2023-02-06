package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectMember;
import org.jeecg.modules.mzx.mapper.BizProjectMemberMapper;
import org.jeecg.modules.mzx.service.IBizProjectMemberService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizProjectMemberService extends ServiceImpl<BizProjectMemberMapper, BizProjectMember> implements IBizProjectMemberService {
}
