package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;
import org.jeecg.modules.mzx.entity.BizEmployeeSalary;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.mapper.BizEmployeePayrollMapper;
import org.jeecg.modules.mzx.model.EmployeeProjectCostModel;
import org.jeecg.modules.mzx.service.IBizEmployeePayrollService;
import org.jeecg.modules.mzx.service.IBizEmployeeSalaryService;
import org.jeecg.modules.mzx.service.IBizProjectCostService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 员工工资表
 *
 * @author xcom
 * @date 2024/2/6
 */

@Service
@Slf4j
public class BizEmployeePayrollService extends ServiceImpl<BizEmployeePayrollMapper, BizEmployeePayroll> implements IBizEmployeePayrollService {

    @Autowired
    private BizEmployeePayrollMapper employeePayrollMapper;

    @Autowired
    private IBizEmployeeSalaryService bizEmployeeSalaryService;
    @Autowired
    private IBizProjectCostService bizProjectCostService;
    @Autowired
    private ISysUserService sysUserService;


    @Override
    public void initLastMonthEmployeePayroll() {

        // 获取员工薪资
        LambdaQueryWrapper<BizEmployeeSalary> salaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salaryLambdaQueryWrapper.eq(BizEmployeeSalary::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<BizEmployeeSalary> employeeSalaryList = bizEmployeeSalaryService.list(salaryLambdaQueryWrapper);

        // 上个月时间
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        Date endTime = instance.getTime();
        instance.add(Calendar.MONTH, -1);
        Date startTime = instance.getTime();
        // 周期
        Integer period = instance.get(Calendar.YEAR) * 100 + instance.get(Calendar.MONTH) + 1;

        // 获取员工的项目费用
        List<EmployeeProjectCostModel> employeeProjectCostModels = bizProjectCostService.listEmployeeProjectCost(startTime, endTime);

        // 获取全部员工的id
        Set<String> employeeIdSet = new HashSet<>();
        if (CollectionUtil.isNotEmpty(employeeSalaryList)) {
            employeeIdSet.addAll(employeeSalaryList.stream().map(BizEmployeeSalary::getEmployeeId).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(employeeProjectCostModels)) {
            employeeIdSet.addAll(employeeProjectCostModels.stream().map(EmployeeProjectCostModel::getStaffId).collect(Collectors.toList()));
        }
        List<String> employeeIdList = new ArrayList<>(employeeIdSet);
        // 获取全部员工信息
        List<SysUser> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(employeeIdList)) {
            LambdaQueryWrapper<SysUser> SysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            SysUserLambdaQueryWrapper.in(SysUser::getId, employeeIdList);
            userList = sysUserService.list(SysUserLambdaQueryWrapper);
        }
        if (CollectionUtil.isEmpty(userList)) {
            log.info(String.format("无员工薪资数据生成"));
            return;
        }

        // 获取本周期内的员工工资
        LambdaQueryWrapper<BizEmployeePayroll> employeePayrollLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeePayrollLambdaQueryWrapper.eq(BizEmployeePayroll::getPeriod, period);
        employeePayrollLambdaQueryWrapper.eq(BizEmployeePayroll::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<BizEmployeePayroll> employeePayrollExistList = this.list(employeePayrollLambdaQueryWrapper);

        List<BizEmployeePayroll> employeePayrollInsertList = new ArrayList<>();
        List<BizEmployeePayroll> employeePayrollUpdateList = new ArrayList<>();

        userList.forEach(user -> {
            BizEmployeePayroll tempData = new BizEmployeePayroll();
            tempData.setEmployeeId(user.getId());
            tempData.setEmployeeName(user.getRealname());
            Optional<BizEmployeeSalary> employeeSalaryOptional = employeeSalaryList.stream().filter(salary -> salary.getEmployeeId().equals(user.getId())).findFirst();
            if (employeeSalaryOptional.isPresent()) {
                BizEmployeeSalary employeeSalary = employeeSalaryOptional.get();
                tempData.setSalary(employeeSalary.getSalary());
                tempData.setSocialInsurance(employeeSalary.getSocialInsurance());
                tempData.setAccumulationFund(employeeSalary.getAccumulationFund());
            }
            List<EmployeeProjectCostModel> employeeProjectCostList = employeeProjectCostModels.stream().filter(projectCost -> projectCost.getStaffId().equals(user.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(employeeProjectCostList)) {
                // CostKey:1 补助
                Optional<EmployeeProjectCostModel> projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("1")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectProjectSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setProjectSubsidy(projectCostOptional.get().getCostValue());
                }
                // CostKey:2 交通补助
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("2")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectTrafficSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setTrafficSubsidy(projectCostOptional.get().getCostValue());
                }
                // CostKey:10 住宿补助
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("10")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectAccommodationSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setAccommodationSubsidy(projectCostOptional.get().getCostValue());
                }
                // CostKey:3 餐费
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("3")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectDiningSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setDiningSubsidy(projectCostOptional.get().getCostValue());
                }
                // CostKey:4 其他费用
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("4")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectOtherSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setOtherSubsidy(projectCostOptional.get().getCostValue());
                }
            }
            tempData.setPeriod(period);
            // 待生效
            tempData.setPayrollStatus((short) 1);
            // 获取登录用户信息
            String loginName;
            try {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (ObjectUtils.isNotEmpty(sysUser)) {
                    loginName = sysUser.getRealname();
                } else {
                    loginName = "job";
                }
            } catch (Exception exception) {
                loginName = "job";
            }
            tempData.setDelFlag(CommonConstant.DEL_FLAG_0);
            tempData.setCreateBy(loginName);
            tempData.setCreatedTime(new Date());
            if (employeePayrollExistList.stream().anyMatch(e -> e.getEmployeeId().equals(user.getId()))) {
                employeePayrollUpdateList.add(tempData);
            } else {
                employeePayrollInsertList.add(tempData);
            }
        });

        if (CollectionUtil.isNotEmpty(employeePayrollUpdateList)) {
            try {
                employeePayrollMapper.updateEmployeePayroll(employeePayrollUpdateList);
            } catch (Exception exception) {
                log.warn("员工工资更新异常", exception);
            }
        }
        if (CollectionUtil.isNotEmpty(employeePayrollInsertList)) {
            try {
                this.saveBatch(employeePayrollInsertList);
            } catch (Exception exception) {
                log.warn("员工工资新增异常", exception);
            }
        }
    }


}