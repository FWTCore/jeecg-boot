package org.jeecg.modules.mzx.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;
import org.jeecg.modules.mzx.entity.BizEmployeeSalary;
import org.jeecg.modules.mzx.entity.BizProjectBillingCommission;
import org.jeecg.modules.mzx.model.OwnPayrollVO;
import org.jeecg.modules.mzx.service.IBizEmployeePayrollService;
import org.jeecg.modules.mzx.service.IBizProjectBillingCommissionService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 员工工资
 *
 * @author xcom
 * @date 2024/2/6
 */

@Api(tags = "员工工资")
@RestController
@RequestMapping("/employee/payroll")
@Slf4j
public class EmployeePayrollController {


    @Autowired
    private IBizEmployeePayrollService bizEmployeePayrollService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IBizProjectBillingCommissionService bizProjectBillingCommissionService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizEmployeePayroll>> queryPageList(BizEmployeePayroll payroll, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizEmployeePayroll>> result = new Result<IPage<BizEmployeePayroll>>();
        payroll.setPeriod(payroll.generationPeriod());
        QueryWrapper<BizEmployeePayroll> queryWrapper = QueryGenerator.initQueryWrapper(payroll, req.getParameterMap());
        queryWrapper.orderByDesc("period").orderByDesc("update_time");
        Page<BizEmployeePayroll> page = new Page<BizEmployeePayroll>(pageNo, pageSize);
        IPage<BizEmployeePayroll> pageList = bizEmployeePayrollService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param ids
     * @return
     * @功能：批量删除
     */
    @ApiOperation("批量删除")
    @RequestMapping(value = "/bathDelete", method = RequestMethod.DELETE)
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            bizEmployeePayrollService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * @param id
     * @return
     * @功能：删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<String>();
        BizEmployeePayroll data = bizEmployeePayrollService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            bizEmployeePayrollService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * @param ids
     * @return
     * @功能：批量删除
     */
    @ApiOperation("批量删除")
    @RequestMapping(value = "/bathEffect", method = RequestMethod.POST)
    public Result<String> bathEffect(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            List<String> idList = Arrays.asList(ids.split(","));
            LambdaQueryWrapper<BizEmployeePayroll> employeePayrollLambdaQueryWrapper = new LambdaQueryWrapper<>();
            employeePayrollLambdaQueryWrapper.in(BizEmployeePayroll::getId, idList);
            employeePayrollLambdaQueryWrapper.eq(BizEmployeePayroll::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<BizEmployeePayroll> list = bizEmployeePayrollService.list(employeePayrollLambdaQueryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                throw new JeecgBootException("操作数据不存在");
            }
            for (BizEmployeePayroll bizEmployeePayroll : list) {
                if (bizEmployeePayroll.getPayrollStatus().intValue() == 1) {
                    Short payrollStatus = 2;
                    bizEmployeePayroll.setPayrollStatus(payrollStatus);
                    bizEmployeePayrollService.updateById(bizEmployeePayroll);
                }
            }
            result.success("操作成功!");
        }
        return result;
    }

    /**
     * @param id
     * @return
     * @功能：删除
     */
    @RequestMapping(value = "/effect", method = RequestMethod.POST)
    public Result<String> effect(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<String>();
        BizEmployeePayroll data = bizEmployeePayrollService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            if (data.getPayrollStatus().intValue() != 1) {
                result.error500("该数据已生效，不能重复操作");
            } else {
                Short payrollStatus = 2;
                data.setPayrollStatus(payrollStatus);
                bizEmployeePayrollService.updateById(data);
                result.success("操作成功!");
            }
        }
        return result;
    }


    /**
     * @param employeePayroll
     * @return
     * @功能：新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizEmployeePayroll> add(@RequestBody BizEmployeePayroll employeePayroll) {
        Result<BizEmployeePayroll> result = new Result<BizEmployeePayroll>();
        try {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            employeePayroll.setPeriod(employeePayroll.generationPeriod());
            SysUser sysUser = sysUserService.getById(employeePayroll.getEmployeeId());
            if (sysUser == null || sysUser.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("员工不存在");
            }
            employeePayroll.setEmployeeName(sysUser.getRealname());

            LambdaQueryWrapper<BizEmployeePayroll> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BizEmployeePayroll::getEmployeeId, employeePayroll.getEmployeeId());
            lambdaQueryWrapper.eq(BizEmployeePayroll::getPeriod, employeePayroll.getPeriod());
            lambdaQueryWrapper.eq(BizEmployeePayroll::getDelFlag, CommonConstant.DEL_FLAG_0);
            BizEmployeePayroll payroll = bizEmployeePayrollService.getOne(lambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(payroll)) {
                throw new JeecgBootException("该员工工资已存在，请核对后处理");
            }
            // 综合工资
            employeePayroll.setComprehensivePayroll(calculateComprehensivePayroll(employeePayroll));
            employeePayroll.setCreatedTime(new Date());
            employeePayroll.setDelFlag(CommonConstant.DEL_FLAG_0);
            Short payrollStatus = 1;
            employeePayroll.setPayrollStatus(payrollStatus);
            bizEmployeePayrollService.save(employeePayroll);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }


    /**
     * @param employeePayroll
     * @return
     * @功能：新增
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result<BizEmployeePayroll> edit(@RequestBody BizEmployeePayroll employeePayroll) {
        Result<BizEmployeePayroll> result = new Result<BizEmployeePayroll>();
        try {

            BizEmployeePayroll existEntity = bizEmployeePayrollService.getById(employeePayroll.getId());
            if (ObjectUtils.isEmpty(existEntity)) {
                throw new JeecgBootException("数据不存在");
            }
            SysUser sysUser = sysUserService.getById(employeePayroll.getEmployeeId());
            if (sysUser == null || sysUser.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("员工不存在");
            }
            employeePayroll.setEmployeeName(sysUser.getRealname());

            LambdaQueryWrapper<BizEmployeePayroll> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BizEmployeePayroll::getEmployeeId, employeePayroll.getEmployeeId());
            lambdaQueryWrapper.eq(BizEmployeePayroll::getPeriod, employeePayroll.getPeriod());
            lambdaQueryWrapper.eq(BizEmployeePayroll::getDelFlag, CommonConstant.DEL_FLAG_0);
            lambdaQueryWrapper.ne(BizEmployeePayroll::getId, employeePayroll.getId());
            BizEmployeePayroll payroll = bizEmployeePayrollService.getOne(lambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(payroll)) {
                throw new JeecgBootException("该员工工资已存在，请核对后处理");
            }
            // 综合工资
            employeePayroll.setComprehensivePayroll(calculateComprehensivePayroll(employeePayroll));
            employeePayroll.setUpdateTime(new Date());
            employeePayroll.setDelFlag(CommonConstant.DEL_FLAG_0);
            bizEmployeePayrollService.updateById(employeePayroll);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }


    /**
     * 计算综合工资
     *
     * @param employeePayroll
     * @return
     */
    private BigDecimal calculateComprehensivePayroll(BizEmployeePayroll employeePayroll) {
        // 综合薪资=基本工资+社保+公积金+项目补助
        BigDecimal comprehensivePayroll = BigDecimal.ZERO;
        //基本工资
        if (ObjectUtils.isNotEmpty(employeePayroll.getSalary())) {
            comprehensivePayroll = comprehensivePayroll.add(employeePayroll.getSalary());
        }
        //社保
        if (ObjectUtils.isNotEmpty(employeePayroll.getSocialInsurance())) {
            comprehensivePayroll = comprehensivePayroll.add(employeePayroll.getSocialInsurance());
        }
        //公积金
        if (ObjectUtils.isNotEmpty(employeePayroll.getAccumulationFund())) {
            comprehensivePayroll = comprehensivePayroll.add(employeePayroll.getAccumulationFund());
        }
        //项目补助
        if (ObjectUtils.isNotEmpty(employeePayroll.getProjectSubsidy())) {
            comprehensivePayroll = comprehensivePayroll.add(employeePayroll.getProjectSubsidy());
        }
        return comprehensivePayroll;
    }


    @ApiOperation("获取员工自己工资列表")
    @RequestMapping(value = "/ownList", method = RequestMethod.GET)
    public Result<IPage<OwnPayrollVO>> queryOwnPageList(BizEmployeePayroll payroll, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<OwnPayrollVO>> result = new Result<IPage<OwnPayrollVO>>();
        payroll.setPeriod(payroll.generationPeriod());
        QueryWrapper<BizEmployeePayroll> queryWrapper = QueryGenerator.initQueryWrapper(payroll, req.getParameterMap());

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        queryWrapper.eq("employee_id", user.getId());
        queryWrapper.orderByDesc("period").orderByDesc("update_time");
        Page<BizEmployeePayroll> page = new Page<BizEmployeePayroll>(pageNo, pageSize);
        IPage<BizEmployeePayroll> pageList = bizEmployeePayrollService.page(page, queryWrapper);

        List<BizEmployeePayroll> records = pageList.getRecords();
        IPage<OwnPayrollVO> resultData = new Page<>();
        resultData.setCurrent(pageList.getCurrent());
        resultData.setPages(pageList.getPages());
        resultData.setSize(pageList.getSize());
        resultData.setTotal(pageList.getTotal());
        if (CollectionUtil.isNotEmpty(records)) {
            List<Integer> periodList = records.stream().map(BizEmployeePayroll::getPeriod).collect(Collectors.toList());

            LambdaQueryWrapper<BizProjectBillingCommission> bizProjectBillingCommissionLambdaQueryWrapper = new LambdaQueryWrapper<BizProjectBillingCommission>();
            bizProjectBillingCommissionLambdaQueryWrapper.eq(BizProjectBillingCommission::getStaffId, user.getId());
            bizProjectBillingCommissionLambdaQueryWrapper.in(BizProjectBillingCommission::getPeriod, periodList);
            bizProjectBillingCommissionLambdaQueryWrapper.eq(BizProjectBillingCommission::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<BizProjectBillingCommission> projectBillingCommissionList = bizProjectBillingCommissionService.list(bizProjectBillingCommissionLambdaQueryWrapper);
            Map<Integer, BigDecimal> commissionByPeriod = new HashMap<>();
            if (CollectionUtil.isNotEmpty(projectBillingCommissionList)) {
                commissionByPeriod = projectBillingCommissionList.stream()
                        .collect(Collectors.groupingBy(BizProjectBillingCommission::getPeriod,
                                Collectors.reducing(BigDecimal.ZERO, BizProjectBillingCommission::getImplementCommission, BigDecimal::add)));


            }
            Map<Integer, BigDecimal> finalCommissionByPeriod = commissionByPeriod;

            List<OwnPayrollVO> resultRecords = new ArrayList<>();
            records.forEach(data -> {
                OwnPayrollVO temp = new OwnPayrollVO();
                temp.setEmployeeId(data.getEmployeeId());
                temp.setEmployeeName(data.getEmployeeName());
                temp.setSalary(data.getSalary());
                temp.setSocialInsurance(data.getSocialInsurance());
                temp.setAccumulationFund(data.getAccumulationFund());

                temp.setCollectProjectSubsidy(data.getCollectProjectSubsidy());
                temp.setProjectSubsidy(data.getProjectSubsidy());
                temp.setProjectSubsidyRemark(data.getProjectSubsidyRemark());

                temp.setCollectTrafficSubsidy(data.getCollectTrafficSubsidy());
                temp.setTrafficSubsidy(data.getTrafficSubsidy());
                temp.setTrafficSubsidyRemark(data.getTrafficSubsidyRemark());

                temp.setCollectAccommodationSubsidy(data.getCollectAccommodationSubsidy());
                temp.setAccommodationSubsidy(data.getAccommodationSubsidy());
                temp.setAccommodationSubsidyRemark(data.getAccommodationSubsidyRemark());

                temp.setCollectDiningSubsidy(data.getCollectDiningSubsidy());
                temp.setDiningSubsidy(data.getDiningSubsidy());
                temp.setDiningSubsidyRemark(data.getDiningSubsidyRemark());

                temp.setCollectOtherSubsidy(data.getCollectOtherSubsidy());
                temp.setOtherSubsidy(data.getOtherSubsidy());
                temp.setOtherSubsidyRemark(data.getOtherSubsidyRemark());

                temp.setPeriod(data.getPeriod());
                temp.setImplementCommission(finalCommissionByPeriod.get(data.getPeriod()));
                resultRecords.add(temp);
            });
            resultData.setRecords(resultRecords);
        }
        result.setSuccess(true);
        result.setResult(resultData);
        return result;
    }

}
