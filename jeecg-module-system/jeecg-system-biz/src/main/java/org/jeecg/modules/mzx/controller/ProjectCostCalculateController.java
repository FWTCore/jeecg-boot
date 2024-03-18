package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectCostCalculate;
import org.jeecg.modules.mzx.entity.BizProjectCostDetail;
import org.jeecg.modules.mzx.service.IBizProjectCostCalculateService;
import org.jeecg.modules.mzx.service.IBizProjectCostDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 项目成本管理
 *
 * @author xcom
 * @date 2024/2/18
 */

@Api(tags = "项目成本管理")
@RestController
@RequestMapping("/project/cost/calculate")
@Slf4j
public class ProjectCostCalculateController {


    @Autowired
    private IBizProjectCostCalculateService projectCostCalculateService;

    @Autowired
    private IBizProjectCostDetailService projectCostDetailService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectCostCalculate>> queryPageList(BizProjectCostCalculate projectCostCalculate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectCostCalculate>> result = new Result<IPage<BizProjectCostCalculate>>();
        QueryWrapper<BizProjectCostCalculate> queryWrapper = QueryGenerator.initQueryWrapper(projectCostCalculate, req.getParameterMap());
        Page<BizProjectCostCalculate> page = new Page<BizProjectCostCalculate>(pageNo, pageSize);
        IPage<BizProjectCostCalculate> pageList = projectCostCalculateService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    @ApiOperation("获取列表")
    @RequestMapping(value = "/detailList", method = RequestMethod.GET)
    public Result<IPage<BizProjectCostDetail>> queryPageDetailList(BizProjectCostDetail projectCostDetail, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectCostDetail>> result = new Result<IPage<BizProjectCostDetail>>();
        QueryWrapper<BizProjectCostDetail> queryWrapper = QueryGenerator.initQueryWrapper(projectCostDetail, req.getParameterMap());
        queryWrapper.orderByDesc("period").orderByDesc("employee_id");
        Page<BizProjectCostDetail> page = new Page<BizProjectCostDetail>(pageNo, pageSize);
        IPage<BizProjectCostDetail> pageList = projectCostDetailService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

}
