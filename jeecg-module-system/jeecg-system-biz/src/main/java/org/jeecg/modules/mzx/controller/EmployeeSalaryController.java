package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.record.DVALRecord;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.*;
import org.jeecg.modules.mzx.service.IBizEmployeeSalaryService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * 员工薪资
 *
 * @author xcom
 * @date 2024/2/6
 */

@Api(tags = "员工薪资")
@RestController
@RequestMapping("/employee/salary")
@Slf4j
public class EmployeeSalaryController {

    @Autowired
    private IBizEmployeeSalaryService bizEmployeeSalaryService;
    @Autowired
    private ISysUserService sysUserService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizEmployeeSalary>> queryPageList(BizEmployeeSalary payroll, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizEmployeeSalary>> result = new Result<IPage<BizEmployeeSalary>>();
        QueryWrapper<BizEmployeeSalary> queryWrapper = QueryGenerator.initQueryWrapper(payroll, req.getParameterMap());
        Page<BizEmployeeSalary> page = new Page<BizEmployeeSalary>(pageNo, pageSize);
        IPage<BizEmployeeSalary> pageList = bizEmployeeSalaryService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param employeeSalary
     * @return
     * @功能：新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizEmployeeSalary> add(@RequestBody BizEmployeeSalary employeeSalary) {
        Result<BizEmployeeSalary> result = new Result<BizEmployeeSalary>();
        try {

            SysUser sysUser = sysUserService.getById(employeeSalary.getEmployeeId());
            if (sysUser == null || sysUser.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("员工不存在");
            }
            employeeSalary.setEmployeeName(sysUser.getRealname());

            LambdaQueryWrapper<BizEmployeeSalary> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BizEmployeeSalary::getEmployeeId, employeeSalary.getEmployeeId());
            lambdaQueryWrapper.eq(BizEmployeeSalary::getDelFlag, CommonConstant.DEL_FLAG_0);
            BizEmployeeSalary salary = bizEmployeeSalaryService.getOne(lambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(salary)) {
                throw new JeecgBootException("该员工薪资已存在，请核对后处理");
            }
            employeeSalary.setCreatedTime(new Date());
            employeeSalary.setDelFlag(CommonConstant.DEL_FLAG_0);
            bizEmployeeSalaryService.save(employeeSalary);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * @param employeeSalary
     * @return
     * @功能：新增
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result<BizEmployeeSalary> edit(@RequestBody BizEmployeeSalary employeeSalary) {
        Result<BizEmployeeSalary> result = new Result<BizEmployeeSalary>();
        try {

            BizEmployeeSalary existEntity = bizEmployeeSalaryService.getById(employeeSalary.getId());
            if(ObjectUtils.isEmpty(existEntity)){
                throw new JeecgBootException("数据不存在");
            }
            SysUser sysUser = sysUserService.getById(employeeSalary.getEmployeeId());
            if (sysUser == null || sysUser.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("员工不存在");
            }
            employeeSalary.setEmployeeName(sysUser.getRealname());

            LambdaQueryWrapper<BizEmployeeSalary> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BizEmployeeSalary::getEmployeeId, employeeSalary.getEmployeeId());
            lambdaQueryWrapper.eq(BizEmployeeSalary::getDelFlag, CommonConstant.DEL_FLAG_0);
            lambdaQueryWrapper.ne(BizEmployeeSalary::getId, employeeSalary.getId());
            BizEmployeeSalary salary = bizEmployeeSalaryService.getOne(lambdaQueryWrapper);
            if (ObjectUtils.isNotEmpty(salary)) {
                throw new JeecgBootException("该员工薪资已存在，请核对后处理");
            }
            employeeSalary.setUpdateTime(new Date());
            employeeSalary.setDelFlag(CommonConstant.DEL_FLAG_0);
            bizEmployeeSalaryService.updateById(employeeSalary);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
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
            bizEmployeeSalaryService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizEmployeeSalary data = bizEmployeeSalaryService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            bizEmployeeSalaryService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }



}
