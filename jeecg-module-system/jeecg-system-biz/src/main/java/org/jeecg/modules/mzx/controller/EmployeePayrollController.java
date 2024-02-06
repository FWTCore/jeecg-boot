package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;
import org.jeecg.modules.mzx.service.IBizEmployeePayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizEmployeePayroll>> queryPageList(BizEmployeePayroll payroll, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizEmployeePayroll>> result = new Result<IPage<BizEmployeePayroll>>();
        QueryWrapper<BizEmployeePayroll> queryWrapper = QueryGenerator.initQueryWrapper(payroll, req.getParameterMap());
        Page<BizEmployeePayroll> page = new Page<BizEmployeePayroll>(pageNo, pageSize);
        IPage<BizEmployeePayroll> pageList = bizEmployeePayrollService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


}
