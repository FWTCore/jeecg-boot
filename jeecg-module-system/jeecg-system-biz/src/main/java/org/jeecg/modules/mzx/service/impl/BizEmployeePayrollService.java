package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.jeewx.api.core.common.JSONHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public void initLastMonthEmployeePayroll(String date) {

        // 获取员工薪资
        LambdaQueryWrapper<BizEmployeeSalary> salaryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        salaryLambdaQueryWrapper.eq(BizEmployeeSalary::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<BizEmployeeSalary> employeeSalaryList = bizEmployeeSalaryService.list(salaryLambdaQueryWrapper);

        // 上个月时间
        Calendar instance = Calendar.getInstance();
        if (StringUtils.isNotBlank(date)) {
            DateTime parseDate = DateUtil.parse(date);
            instance.setTime(parseDate);
        }
        instance.set(Calendar.DAY_OF_MONTH, 1);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        Integer endPeriod = instance.get(Calendar.YEAR) * 10000 + (instance.get(Calendar.MONTH) + 1) * 100 + instance.get(Calendar.DAY_OF_MONTH);
        instance.add(Calendar.MONTH, -1);
        Integer startPeriod = instance.get(Calendar.YEAR) * 10000 + (instance.get(Calendar.MONTH) + 1) * 100 + instance.get(Calendar.DAY_OF_MONTH);

        // 周期
        Integer period = instance.get(Calendar.YEAR) * 100 + instance.get(Calendar.MONTH) + 1;

        // 获取员工的项目费用
        List<EmployeeProjectCostModel> employeeProjectCostModels = bizProjectCostService.listEmployeeProjectCost(startPeriod, endPeriod);

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
            else {
                tempData.setSalary(BigDecimal.ZERO);
                tempData.setSocialInsurance(BigDecimal.ZERO);
                tempData.setAccumulationFund(BigDecimal.ZERO);
            }
            List<EmployeeProjectCostModel> employeeProjectCostList = employeeProjectCostModels.stream().filter(projectCost -> projectCost.getStaffId().equals(user.getId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(employeeProjectCostList)) {
                // CostKey:1 补助
                Optional<EmployeeProjectCostModel> projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("1")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectProjectSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setProjectSubsidy(projectCostOptional.get().getCostValue());
                }
                else {
                    tempData.setCollectProjectSubsidy(BigDecimal.ZERO);
                    tempData.setProjectSubsidy(BigDecimal.ZERO);
                }
                // CostKey:2 交通补助
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("2")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectTrafficSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setTrafficSubsidy(projectCostOptional.get().getCostValue());
                }
                else {
                    tempData.setCollectTrafficSubsidy(BigDecimal.ZERO);
                    tempData.setTrafficSubsidy(BigDecimal.ZERO);
                }
                // CostKey:10 住宿补助
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("10")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectAccommodationSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setAccommodationSubsidy(projectCostOptional.get().getCostValue());
                }
                else {
                    tempData.setCollectAccommodationSubsidy(BigDecimal.ZERO);
                    tempData.setAccommodationSubsidy(BigDecimal.ZERO);
                }
                // CostKey:3 餐费
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("3")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectDiningSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setDiningSubsidy(projectCostOptional.get().getCostValue());
                }
                else {
                    tempData.setCollectDiningSubsidy(BigDecimal.ZERO);
                    tempData.setDiningSubsidy(BigDecimal.ZERO);
                }
                // CostKey:4 其他费用
                projectCostOptional = employeeProjectCostList.stream().filter(e -> e.getCostKey().equals("4")).findFirst();
                if (projectCostOptional.isPresent()) {
                    tempData.setCollectOtherSubsidy(projectCostOptional.get().getCostValue());
                    tempData.setOtherSubsidy(projectCostOptional.get().getCostValue());
                }
                else {
                    tempData.setCollectOtherSubsidy(BigDecimal.ZERO);
                    tempData.setOtherSubsidy(BigDecimal.ZERO);
                }
            }
            // 综合薪资=基本工资+社保+公积金+项目补助
            BigDecimal comprehensivePayroll = BigDecimal.ZERO;
            //基本工资
            if (ObjectUtils.isNotEmpty(tempData.getSalary())) {
                comprehensivePayroll = comprehensivePayroll.add(tempData.getSalary());
            }
            //社保
            if (ObjectUtils.isNotEmpty(tempData.getSocialInsurance())) {
                comprehensivePayroll = comprehensivePayroll.add(tempData.getSocialInsurance());
            }
            //公积金
            if (ObjectUtils.isNotEmpty(tempData.getAccumulationFund())) {
                comprehensivePayroll = comprehensivePayroll.add(tempData.getAccumulationFund());
            }
            //项目补助
            if (ObjectUtils.isNotEmpty(tempData.getProjectSubsidy())) {
                comprehensivePayroll = comprehensivePayroll.add(tempData.getProjectSubsidy());
            }
            tempData.setComprehensivePayroll(comprehensivePayroll);
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
                log.info(String.format("更新数据对象：【%s】", JSONHelper.toJSONString(employeePayrollUpdateList)));
                employeePayrollMapper.updateEmployeePayroll(employeePayrollUpdateList);
            } catch (Exception exception) {
                log.warn("员工工资更新异常", exception);
            }
        }
        if (CollectionUtil.isNotEmpty(employeePayrollInsertList)) {
            try {
                log.info(String.format("新增数据对象：【%s】", JSONHelper.toJSONString(employeePayrollInsertList)));
                this.saveBatch(employeePayrollInsertList);
            } catch (Exception exception) {
                log.warn("员工工资新增异常", exception);
            }
        }
    }


}