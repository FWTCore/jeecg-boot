package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.mapper.BizProjectMapper;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleItemUsageMapper;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleUsageMapper;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.jeecg.modules.mzx.vo.ProjectScheduleUsageDTO;
import org.jeecg.modules.mzx.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class BizProjectService extends ServiceImpl<BizProjectMapper, BizProject> implements IBizProjectService {

    @Autowired
    private BizProjectMapper projectMapper;

    @Autowired
    private BizProjectScheduleUsageMapper projectScheduleUsageMapper;
    @Autowired
    private BizProjectScheduleItemUsageMapper projectScheduleItemUsageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectVO CreateProject(ProjectVO projectVO) {
        BizProject projectEntity = new BizProject();
        projectEntity.setProjectName(projectVO.getProjectName());
        projectEntity.setCustomerId(projectVO.getCustomerId());
        projectEntity.setCustomerName(projectVO.getCustomerName());
        projectEntity.setContractAmount(projectVO.getContractAmount());
        projectEntity.setPaymentMethod(projectVO.getPaymentMethod());
        projectEntity.setSignPersonId(projectVO.getSignPersonId());
        projectEntity.setSignPerson(projectVO.getSignPerson());
        projectEntity.setLeaderId(projectVO.getLeaderId());
        projectEntity.setLeaderName(projectVO.getLeaderName());
        projectEntity.setEstimatedEndTime(projectVO.getEstimatedEndTime());
        projectEntity.setEndTime(projectVO.getEndTime());

        projectEntity.setProjectTypeId(projectVO.getProjectTypeId());
        projectEntity.setProjectTypeName(projectVO.getProjectTypeName());
        projectEntity.setLifeLine(projectVO.getLifeLine());

        // 固定8%
        // projectEntity.setCommissionRatio(projectVO.getCommissionRatio());
        projectEntity.setCommissionRatio(new BigDecimal(8));
        projectEntity.setImplementCommissionRatio(projectVO.getImplementCommissionRatio());
        projectEntity.setSaleCommissionRatio(projectVO.getSaleCommissionRatio());
        projectEntity.setSource(projectVO.getSource());
        projectEntity.setOverview(projectVO.getOverview());
        projectEntity.setComprehensiveCost(projectVO.getComprehensiveCost());
        projectEntity.setComprehensiveRemark(projectVO.getComprehensiveRemark());
        projectEntity.setProjectStatus("1");
        projectEntity.setDelFlag(CommonConstant.DEL_FLAG_0);
        projectEntity.setCreateTime(new Date());
        this.save(projectEntity);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ProjectScheduleUsageDTO projectScheduleUsageDTO=new ProjectScheduleUsageDTO();
        projectScheduleUsageDTO.setProjectId(projectEntity.getId());
        projectScheduleUsageDTO.setProjectScheduleTemplateId(projectVO.getProjectScheduleTemplateId());
        projectScheduleUsageDTO.setUserName(sysUser.getRealname());
        projectScheduleUsageDTO.setProjectScheduleUsageId(UUIDGenerator.generate());
        projectScheduleUsageMapper.CreateScheduleUsage(projectScheduleUsageDTO);
        projectScheduleItemUsageMapper.CreateScheduleItemUsage(projectScheduleUsageDTO);
        return projectVO;

    }
}
