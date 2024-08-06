package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectCostCalculate;
import org.jeecg.modules.mzx.entity.BizProjectCostDetail;
import org.jeecg.modules.mzx.service.IBizProjectCostCalculateService;
import org.jeecg.modules.mzx.service.IBizProjectCostDetailService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectCostCalculate>> queryPageList(BizProjectCostCalculate projectCostCalculate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectCostCalculate>> result = new Result<IPage<BizProjectCostCalculate>>();
        QueryWrapper<BizProjectCostCalculate> queryWrapper = QueryGenerator.initQueryWrapper(projectCostCalculate, req.getParameterMap());
        queryWrapper.orderByDesc("project_id").orderByDesc("project_status");
        Page<BizProjectCostCalculate> page = new Page<BizProjectCostCalculate>(pageNo, pageSize);
        IPage<BizProjectCostCalculate> pageList = projectCostCalculateService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param projectCostCalculate
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BizProjectCostCalculate projectCostCalculate) {
        // Step.1 组装查询条件
        QueryWrapper<BizProjectCostCalculate> queryWrapper = QueryGenerator.initQueryWrapper(projectCostCalculate, request.getParameterMap());
        queryWrapper.orderByDesc("project_id").orderByDesc("project_status");

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

        List<BizProjectCostCalculate> list = projectCostCalculateService.list(queryWrapper);

        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "项目成本看板");
        mv.addObject(NormalExcelConstants.CLASS, BizProjectCostCalculate.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("项目成本看板", "导出人:"+user.getRealname(), "导出信息");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;

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
