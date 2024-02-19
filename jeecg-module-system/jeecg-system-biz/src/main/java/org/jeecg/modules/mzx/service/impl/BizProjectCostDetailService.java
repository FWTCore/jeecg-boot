package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.mzx.entity.BizEmployeeSalary;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectCostDetail;
import org.jeecg.modules.mzx.entity.BizProjectScheduleLog;
import org.jeecg.modules.mzx.mapper.BizProjectCostDetailMapper;
import org.jeecg.modules.mzx.model.EmployeeProjectCostModel;
import org.jeecg.modules.mzx.service.*;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目人工成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */


@Service
@Slf4j
public class BizProjectCostDetailService extends ServiceImpl<BizProjectCostDetailMapper, BizProjectCostDetail> implements IBizProjectCostDetailService {

    @Autowired
    private BizProjectCostDetailMapper projectCostDetailMapper;
    @Autowired
    private IBizProjectCostService bizProjectCostService;
    @Autowired
    private IBizEmployeeSalaryService bizEmployeeSalaryService;
    @Autowired
    private IBizProjectScheduleLogService projectScheduleLogService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IBizProjectService projectService;


    @Override
    public void initProjectCostDetail(Date startTime, Date endTime) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(startTime);
        // 周期
        Integer period = instance.get(Calendar.YEAR) * 100 + instance.get(Calendar.MONTH) + 1;
        // 全部员工id
        Set<String> employeeIdSet = new HashSet<>();
        // 全部项目id
        Set<String> projectIdSet = new HashSet<>();
        // 获取员工的项目费用
        List<EmployeeProjectCostModel> employeeProjectCostModels = bizProjectCostService.listEmployeeEachProjectCost(startTime, endTime);
        if (CollectionUtil.isNotEmpty(employeeProjectCostModels)) {
            employeeIdSet.addAll(employeeProjectCostModels.stream().map(EmployeeProjectCostModel::getStaffId).collect(Collectors.toList()));
            projectIdSet.addAll(employeeProjectCostModels.stream().map(EmployeeProjectCostModel::getProjectId).collect(Collectors.toList()));
        }

        // 获取项目日志员工，计算工资分摊
        LambdaQueryWrapper<BizProjectScheduleLog> scheduleLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleLogLambdaQueryWrapper.eq(BizProjectScheduleLog::getDelFlag, CommonConstant.DEL_FLAG_0);
        scheduleLogLambdaQueryWrapper.ge(BizProjectScheduleLog::getCreateTime, startTime);
        scheduleLogLambdaQueryWrapper.lt(BizProjectScheduleLog::getCreateTime, endTime);
        List<BizProjectScheduleLog> scheduleLogList = projectScheduleLogService.list(scheduleLogLambdaQueryWrapper);
        List<BizEmployeeSalary> employeeSalaryList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(scheduleLogList)) {
            List<String> employeeIdList = scheduleLogList.stream().map(BizProjectScheduleLog::getStaffId).collect(Collectors.toList());
            // 获取项目员工的薪资
            LambdaQueryWrapper<BizEmployeeSalary> salaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
            salaryLambdaQueryWrapper.eq(BizEmployeeSalary::getDelFlag, CommonConstant.DEL_FLAG_0);
            salaryLambdaQueryWrapper.in(BizEmployeeSalary::getEmployeeId, employeeIdList);
            employeeSalaryList = bizEmployeeSalaryService.list(salaryLambdaQueryWrapper);
            employeeIdSet.addAll(employeeIdList);
            projectIdSet.addAll(scheduleLogList.stream().map(BizProjectScheduleLog::getProjectId).collect(Collectors.toList()));
        }
        // 获取全部员工信息
        List<String> userIdList = new ArrayList<>(employeeIdSet);
        List<SysUser> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userIdList)) {
            LambdaQueryWrapper<SysUser> SysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            SysUserLambdaQueryWrapper.in(SysUser::getId, userIdList);
            userList = sysUserService.list(SysUserLambdaQueryWrapper);
        }
        if (CollectionUtil.isEmpty(userList)) {
            log.info(String.format("无项目人工成本数据生成"));
            return;
        }
        // 获取全部项目
        List<String> projectIdList = new ArrayList<>(projectIdSet);
        List<BizProject> projectList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(projectIdList)) {
            LambdaQueryWrapper<BizProject> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectLambdaQueryWrapper.in(BizProject::getId, projectIdList);
            projectList = projectService.list(projectLambdaQueryWrapper);
        }
        if (CollectionUtil.isEmpty(projectList)) {
            log.info(String.format("无项目人工成本数据生成"));
            return;
        }

        // 项目补助

    }
}