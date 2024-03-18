package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.mzx.entity.*;
import org.jeecg.modules.mzx.mapper.BizProjectCostDetailMapper;
import org.jeecg.modules.mzx.service.*;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        // 确定需要处理的项目数据
        List<String> projectIdList = projectCostDetailMapper.listMonitorCostProject(startTime, endTime);
        if (CollectionUtil.isEmpty(projectIdList)) {
            log.info(String.format("时间【%s-%s】无项目数据变动", DateUtils.date2Str(startTime, DateUtils.yyyymmddhhmmss.get()), DateUtils.date2Str(endTime, DateUtils.yyyymmddhhmmss.get())));
            return;
        }

        // 员工id
        Set<String> employeeIdSet = new HashSet<>();
        // 获取项目全部费用
        List<BizProjectCost> employeeProjectCostList = listEmployeeProjectCost(projectIdList, startTime, endTime);
        if (CollectionUtil.isNotEmpty(employeeProjectCostList)) {
            employeeIdSet.addAll(employeeProjectCostList.stream().map(BizProjectCost::getStaffId).collect(Collectors.toList()));
        }

        // 获取项目全部日志
        List<BizProjectScheduleLog> scheduleLogList = listEmployeeProjectScheduleLog(projectIdList, startTime, endTime);
        if (CollectionUtil.isNotEmpty(scheduleLogList)) {
            employeeIdSet.addAll(scheduleLogList.stream().map(BizProjectScheduleLog::getStaffId).collect(Collectors.toList()));
        }
        // 获取全部员工信息
        List<String> userIdList = new ArrayList<>(employeeIdSet);
        List<SysUser> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userIdList)) {
            userList = listSysUser(userIdList);
        }
        if (CollectionUtil.isEmpty(userList)) {
            log.info("无项目人工成本数据生成");
            return;
        }
        // 获取员工的薪资
        List<BizEmployeeSalary> employeeSalaryList = listEmployeeSalary(userIdList);
        // 获取全部项目
        List<BizProject> projectList = listProject(projectIdList);

        // 项目补助
        List<BizProjectCostDetail> projectCostDetails = new ArrayList<>();
        userList.forEach(user -> {
            // 当前用户项目id
            Set<String> userProjectIdSet = new HashSet<>();
            // 项目补助
            userProjectIdSet.addAll(employeeProjectCostList.stream().filter(e -> e.getStaffId().equals(user.getId())).map(BizProjectCost::getProjectId).collect(Collectors.toList()));
            // 项目日志
            userProjectIdSet.addAll(scheduleLogList.stream().filter(e -> e.getStaffId().equals(user.getId())).map(BizProjectScheduleLog::getProjectId).collect(Collectors.toList()));

            List<String> userProjectIdList = new ArrayList<>(userProjectIdSet);
            if (CollectionUtil.isNotEmpty(userProjectIdList)) {
                userProjectIdList.forEach(projectId -> {
                    Optional<BizProject> projectOptional = projectList.stream().filter(e -> e.getId().equals(projectId)).findFirst();
                    if (projectOptional.isPresent()) {
                        BizProject project = projectOptional.get();
                        BizProjectCostDetail tempData = structureProjectCostDetail(user, project, employeeSalaryList, employeeProjectCostList, scheduleLogList);
                        tempData.setId(UUID.randomUUID().toString().replace("-", ""));
                        tempData.setPeriod(period);
                        projectCostDetails.add(tempData);
                    }
                });
            }
        });

        if (CollectionUtil.isNotEmpty(projectCostDetails)) {
            // 先更新
            projectCostDetailMapper.updateProjectCostDetail(projectCostDetails);
            // 后新增
            projectCostDetailMapper.insertProjectCostDetail(projectCostDetails);
        }
    }

    /**
     * 构造项目人工成本核算
     *
     * @param user
     * @param project
     * @param employeeSalaries
     * @param projectCosts
     * @param projectScheduleLogs
     * @return
     */
    private BizProjectCostDetail structureProjectCostDetail(SysUser user, BizProject project, List<BizEmployeeSalary> employeeSalaries, List<BizProjectCost> projectCosts, List<BizProjectScheduleLog> projectScheduleLogs) {

        BizProjectCostDetail resultData = new BizProjectCostDetail();
        resultData.setProjectId(project.getId());
        resultData.setProjectName(project.getProjectName());
        resultData.setEmployeeId(user.getId());
        resultData.setEmployeeName(user.getRealname());

        // 补助
        if (CollectionUtil.isNotEmpty(projectCosts)) {
            //获取指定用户，指定项目费用集合
            /**
             * 综合补助=交通+住宿+餐补+其他
             * 交通补助  CostKey:2
             * 住宿补助 CostKey:10
             * 餐费  CostKey:3
             * 其他费用 CostKey:4
             */
            List<String> costKeyList = Arrays.asList("2", "10", "3", "4");
            List<BizProjectCost> projectCostCollect = projectCosts.stream().filter(e -> e.getStaffId().equals(user.getId()) && e.getProjectId().equals(project.getId()) && costKeyList.contains(e.getCostKey())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(projectCostCollect)) {
                resultData.setComprehensiveSubsidy(projectCostCollect.stream().map(BizProjectCost::getCostValue).reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                resultData.setComprehensiveSubsidy(BigDecimal.ZERO);
            }
            // 项目补助
            projectCostCollect = projectCosts.stream().filter(e -> e.getStaffId().equals(user.getId()) && e.getProjectId().equals(project.getId()) && e.getCostKey().equals("1")).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(projectCostCollect)) {
                resultData.setProjectSubsidy(projectCostCollect.stream().map(BizProjectCost::getCostValue).reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                resultData.setProjectSubsidy(BigDecimal.ZERO);
            }
        }

        // 员工工资
        BigDecimal perDaySalary = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(employeeSalaries)) {
            // 获取指定用户的薪资
            Optional<BizEmployeeSalary> employeeSalaryOptional = employeeSalaries.stream().filter(e -> e.getEmployeeId().equals(user.getId())).findFirst();
            if (employeeSalaryOptional.isPresent()) {
                BizEmployeeSalary bizEmployeeSalary = employeeSalaryOptional.get();
                if (ObjectUtils.isNotEmpty(bizEmployeeSalary.getSalary())) {
                    //薪资=基本工资+社保+公积金
                    BigDecimal totalSalary = BigDecimal.ZERO;
                    if (ObjectUtils.isNotEmpty(bizEmployeeSalary.getSalary())) {
                        totalSalary = totalSalary.add(bizEmployeeSalary.getSalary());
                    }
                    if (ObjectUtils.isNotEmpty(bizEmployeeSalary.getSocialInsurance())) {
                        totalSalary = totalSalary.add(bizEmployeeSalary.getSocialInsurance());
                    }
                    if (ObjectUtils.isNotEmpty(bizEmployeeSalary.getAccumulationFund())) {
                        totalSalary = totalSalary.add(bizEmployeeSalary.getAccumulationFund());
                    }
                    // 除以固定22天，计算每天的薪资，保留2位小数，四舍五入
                    perDaySalary = totalSalary.divide(new BigDecimal(22), 2, RoundingMode.HALF_UP);
                }
            }
        }

        // 项目日志
        if (CollectionUtil.isNotEmpty(projectScheduleLogs)) {
            // 获取指定用户，指定项目的日志集合
            List<BizProjectScheduleLog> projectScheduleLogCollect = projectScheduleLogs.stream().filter(e -> e.getStaffId().equals(user.getId()) && e.getProjectId().equals(project.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(projectScheduleLogCollect)) {
                // 统计工作时长，单位天
                BigDecimal workDay = projectScheduleLogCollect.stream().map(BizProjectScheduleLog::getWorkHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                resultData.setWorkDays(workDay);
                resultData.setLaborCost(workDay.multiply(perDaySalary));

            } else {
                resultData.setWorkDays(BigDecimal.ZERO);
                resultData.setLaborCost(BigDecimal.ZERO);
            }
        }
        return resultData;
    }


    /**
     * 获取指定项目的项目费用
     *
     * @param projectIds
     * @param startTime
     * @param endTime
     * @return
     */
    private List<BizProjectCost> listEmployeeProjectCost(List<String> projectIds, Date startTime, Date endTime) {
        LambdaQueryWrapper<BizProjectCost> projectCostLambdaQueryWrapper = new LambdaQueryWrapper<>();
        projectCostLambdaQueryWrapper.eq(BizProjectCost::getDelFlag, CommonConstant.DEL_FLAG_0);
        projectCostLambdaQueryWrapper.in(BizProjectCost::getProjectId, projectIds);
        projectCostLambdaQueryWrapper.ge(BizProjectCost::getCreateTime, startTime);
        projectCostLambdaQueryWrapper.lt(BizProjectCost::getCreateTime, endTime);
        return bizProjectCostService.list(projectCostLambdaQueryWrapper);
    }

    /**
     * 获取指定项目的项目日志
     *
     * @param projectIds
     * @param startTime
     * @param endTime
     * @return
     */
    private List<BizProjectScheduleLog> listEmployeeProjectScheduleLog(List<String> projectIds, Date startTime, Date endTime) {
        LambdaQueryWrapper<BizProjectScheduleLog> scheduleLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scheduleLogLambdaQueryWrapper.eq(BizProjectScheduleLog::getDelFlag, CommonConstant.DEL_FLAG_0);
        scheduleLogLambdaQueryWrapper.in(BizProjectScheduleLog::getProjectId, projectIds);
        scheduleLogLambdaQueryWrapper.ge(BizProjectScheduleLog::getCreateTime, startTime);
        scheduleLogLambdaQueryWrapper.lt(BizProjectScheduleLog::getCreateTime, endTime);
        return projectScheduleLogService.list(scheduleLogLambdaQueryWrapper);
    }

    /**
     * 获取指定员工薪资
     *
     * @param employeeIds
     * @return
     */
    private List<BizEmployeeSalary> listEmployeeSalary(List<String> employeeIds) {
        LambdaQueryWrapper<BizEmployeeSalary> salaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salaryLambdaQueryWrapper.eq(BizEmployeeSalary::getDelFlag, CommonConstant.DEL_FLAG_0);
        salaryLambdaQueryWrapper.in(BizEmployeeSalary::getEmployeeId, employeeIds);
        return bizEmployeeSalaryService.list(salaryLambdaQueryWrapper);
    }

    /**
     * 获取指定员工信息
     *
     * @param userIds
     * @return
     */
    private List<SysUser> listSysUser(List<String> userIds) {
        LambdaQueryWrapper<SysUser> SysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        SysUserLambdaQueryWrapper.in(SysUser::getId, userIds);
        return sysUserService.list(SysUserLambdaQueryWrapper);
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