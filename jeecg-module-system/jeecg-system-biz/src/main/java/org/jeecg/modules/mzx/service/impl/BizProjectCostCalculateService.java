package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.mzx.entity.*;
import org.jeecg.modules.mzx.mapper.BizProjectCostCalculateMapper;
import org.jeecg.modules.mzx.mapper.BizProjectCostDetailMapper;
import org.jeecg.modules.mzx.model.OvertimeHoursModel;
import org.jeecg.modules.mzx.model.ProjectCostModel;
import org.jeecg.modules.mzx.service.IBizOvertimeRecordService;
import org.jeecg.modules.mzx.service.IBizProjectCostCalculateService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

@Service
@Slf4j
public class BizProjectCostCalculateService extends ServiceImpl<BizProjectCostCalculateMapper, BizProjectCostCalculate> implements IBizProjectCostCalculateService {

    @Autowired
    private BizProjectCostCalculateMapper projectCostCalculateMapper;
    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private BizProjectCostDetailMapper projectCostDetailMapper;
    @Autowired
    private IBizOvertimeRecordService overtimeRecordService;


    @Override
    public void initProjectCostCalculate(Date dateTime) {

        Calendar instance = Calendar.getInstance();
        instance.setTime(dateTime);
        // 周期
        Integer period = instance.get(Calendar.YEAR) * 100 + instance.get(Calendar.MONTH) + 1;
        // 确定需要处理的项目数据
        List<String> projectIdList = projectCostDetailMapper.listMonitorCostProject(period);
        if (CollectionUtil.isEmpty(projectIdList)) {
            log.info(String.format("时间【%s】无项目数据变动", DateUtils.date2Str(dateTime, DateUtils.yyyyMMdd.get())));
            return;
        }

        List<BizProject> projectList = listProject(projectIdList);

        // 获取指定项目的项目人力成本，转换为 Map 提高查找效率
        List<ProjectCostModel> projectCostModels = projectCostCalculateMapper.listProjectCostDetailSum(projectIdList);
        Map<String, ProjectCostModel> projectCostMap = CollectionUtil.isEmpty(projectCostModels)
                ? new HashMap<>()
                : projectCostModels.stream().collect(Collectors.toMap(ProjectCostModel::getProjectId, e -> e));

        // 获取项目加班时长统计（统计已确认的加班记录，不限时间），转换为 Map 提高查找效率
        List<OvertimeHoursModel> overtimeHoursModels = overtimeRecordService.sumOvertimeHoursByProject(projectIdList);
        Map<String, OvertimeHoursModel> overtimeHoursMap = CollectionUtil.isEmpty(overtimeHoursModels)
                ? new HashMap<>()
                : overtimeHoursModels.stream().collect(Collectors.toMap(OvertimeHoursModel::getProjectId, e -> e));

        List<BizProjectCostCalculate> projectCostCalculates = new ArrayList<>();
        projectList.forEach(project -> {
            BizProjectCostCalculate tempDate = structureProjectCostCalculate(project, projectCostMap, overtimeHoursMap);
            tempDate.setId(UUID.randomUUID().toString().replace("-", ""));
            projectCostCalculates.add(tempDate);
        });

        if (CollectionUtil.isNotEmpty(projectCostCalculates)) {
            // 先更新
            projectCostCalculateMapper.updateProjectCostCalculate(projectCostCalculates);
            // 后新增
            projectCostCalculateMapper.insertProjectCostCalculate(projectCostCalculates);
        }

    }

    /**
     * 构造项目成本核算
     *
     * @param project
     * @param projectCostMap    项目人力成本 Map（projectId -> ProjectCostModel）
     * @param overtimeHoursMap  项目加班时长 Map（projectId -> OvertimeHoursModel）
     * @return
     */
    private BizProjectCostCalculate structureProjectCostCalculate(BizProject project, Map<String, ProjectCostModel> projectCostMap, Map<String, OvertimeHoursModel> overtimeHoursMap) {
        BizProjectCostCalculate resultData = new BizProjectCostCalculate();
        resultData.setProjectId(project.getId());
        resultData.setProjectName(project.getProjectName());
        // 项目金额
        if (ObjectUtils.isNotEmpty(project.getContractAmount())) {
            resultData.setProjectAmount(project.getContractAmount());
        } else {
            resultData.setProjectAmount(BigDecimal.ZERO);
        }
        // 综合费用
        if (ObjectUtils.isNotEmpty(project.getComprehensiveCost())) {
            resultData.setComprehensiveCost(project.getComprehensiveCost());
        } else {
            resultData.setComprehensiveCost(BigDecimal.ZERO);
        }
        // 项目成本=项目人力成本集合（使用 Map 查找，O(1)）
        BigDecimal tempValue = BigDecimal.ZERO;
        ProjectCostModel projectCostModel = projectCostMap.get(project.getId());
        if (projectCostModel != null) {
            tempValue = projectCostModel.getTotalCost();
        }
        resultData.setProjectCost(tempValue);

        // 项目费用=项目金额-综合费用
        tempValue = BigDecimal.ZERO;
        BigDecimal projectFees = resultData.getProjectAmount().subtract(resultData.getComprehensiveCost());
        // 销售提成=（项目金额-综合费用）* 销售提成比
        if (ObjectUtils.isNotEmpty(project.getSaleCommissionRatio())) {
            tempValue = projectFees.multiply(project.getSaleCommissionRatio());
            tempValue = tempValue.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        resultData.setSaleCommission(tempValue);

        //实施提成=（项目金额-综合费用）*项目实施提成比例
        tempValue = BigDecimal.ZERO;
        if (ObjectUtils.isNotEmpty(project.getImplementCommissionRatio())) {
            tempValue = projectFees.multiply(project.getImplementCommissionRatio());
            tempValue = tempValue.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        resultData.setImplementCommission(tempValue);

        // 加班时长统计（只统计已确认的加班记录，使用 Map 查找，O(1)）
        BigDecimal overtimeHours = BigDecimal.ZERO;
        OvertimeHoursModel overtimeHoursModel = overtimeHoursMap.get(project.getId());
        if (overtimeHoursModel != null) {
            overtimeHours = overtimeHoursModel.getOvertimeHours();
        }
        resultData.setOvertimeHours(overtimeHours);

        //成本率=（项目成本/（项目金额-综合费用）
        tempValue = BigDecimal.ZERO;
        if (ObjectUtils.isNotEmpty(resultData.getProjectCost())) {
            if (ObjectUtils.isNotEmpty(projectFees) && projectFees.compareTo(BigDecimal.ZERO) != 0) {
                tempValue = resultData.getProjectCost().divide(projectFees, 4, RoundingMode.HALF_UP);
                tempValue = tempValue.multiply(BigDecimal.valueOf(100));
            }
        }
        resultData.setCostRatio(tempValue);

        // 是否超生命线
        if (ObjectUtils.isNotEmpty(project.getLifeLine())) {
            if (resultData.getCostRatio().compareTo(project.getLifeLine()) > 0) {
                resultData.setSuperLifeline(1);
            } else {
                resultData.setSuperLifeline(0);
            }
        } else {
            resultData.setSuperLifeline(0);
        }
        resultData.setProjectStatus(project.getProjectStatus());
        return resultData;
    }

    /**
     * 获取指定项目信息
     *
     * @param projectIds
     * @return
     */
    private List<BizProject> listProject(List<String> projectIds) {
        LambdaQueryWrapper<BizProject> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectLambdaQueryWrapper.in(BizProject::getId, projectIds);
        return projectService.list(projectLambdaQueryWrapper);
    }

}