package org.jeecg.modules.mzx.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.XComEntityExcelView;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.service.IBizProjectCostService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.jeecg.modules.mzx.vo.ProjectCostQuery;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "项目费用")
@RestController
@RequestMapping("/project/cost")
@Slf4j
public class ProjectCostController {

    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private IBizProjectCostService projectCostService;
    @Autowired
    private ISysDictService sysDictService;
    @Value("${jeecg.path.upload}")
    private String upLoadPath;


    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<JSONObject>> queryPageList(ProjectCostQuery projectCost, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<JSONObject>> result = new Result<IPage<JSONObject>>();
        QueryWrapper<BizProjectCost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if (ObjectUtil.isNotNull(projectCost.getProjectName())) {
            queryWrapper.like("project_name", projectCost.getProjectName());
        }
        if (ObjectUtil.isNotNull(projectCost.getStaff())) {
            queryWrapper.like("staff", projectCost.getStaff());
        }
        if (ObjectUtil.isNotNull(projectCost.getBeginDate())) {
            queryWrapper.ge("create_time", projectCost.getBeginDate());
        }
        if (ObjectUtil.isNotNull(projectCost.getEndDate())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(projectCost.getEndDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.lt("create_time", cal.getTime());
        }
        queryWrapper.select("distinct project_id as projectId, staff_id as staffId, date_format(create_time,'%Y-%m-%d') as createTime").orderByDesc("create_time");
        Page<BizProjectCost> page = new Page<BizProjectCost>(pageNo, pageSize);
        IPage<BizProjectCost> pageList = projectCostService.page(page, queryWrapper);

        IPage<JSONObject> resultList = new Page<>();
        resultList.setPages(pageList.getPages());
        resultList.setSize(pageList.getSize());
        resultList.setTotal(pageList.getTotal());
        resultList.setRecords(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(pageList.getRecords()) && pageList.getRecords().size() > 0) {
            for (BizProjectCost data : pageList.getRecords()) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                queryWrapper.in("project_id", data.getProjectId());
                queryWrapper.in("staff_id", data.getStaffId());
                queryWrapper.ge("create_time", data.getCreateTime());
                Calendar endtime = Calendar.getInstance();
                endtime.setTime(data.getCreateTime());
                endtime.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.lt("create_time", endtime.getTime());
                List<BizProjectCost> dataList = projectCostService.list(queryWrapper);
                JSONObject json = new JSONObject();
                json.put("id", String.format("%s_%s_%s", data.getProjectId(), data.getStaffId(), data.getCreateTime().getTime()));
                json.put("projectId", data.getProjectId());
                json.put("projectName", dataList.stream().findFirst().get().getProjectName());
                json.put("staffId", data.getStaffId());
                json.put("staff", dataList.stream().findFirst().get().getStaff());
                json.put("createTime", data.getCreateTime());
                String costRemark = "";


                Map<String, BigDecimal> costNumByCostKey = dataList.stream()
                        .collect(Collectors.groupingBy(BizProjectCost::getCostKey,
                                Collectors.reducing(BigDecimal.ZERO, BizProjectCost::getCostValue, BigDecimal::add)));
                for (Map.Entry<String, BigDecimal> costData : costNumByCostKey.entrySet()) {
                    json.put(String.format("%s_cost", costData.getKey()), costData.getValue());
                }

                for (BizProjectCost item : dataList) {
                    json.put(String.format("%s_remark", item.getCostKey()), item.getCostRemark());
                    costRemark += String.format("%s;", item.getCostRemark());
                }

                json.put("cost_remark", costRemark);
                resultList.getRecords().add(json);
            }

        }
        result.setSuccess(true);
        result.setResult(resultList);
        return result;
    }


    /**
     * 导出excel
     *
     * @param request
     * @param projectCost
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ProjectCostQuery projectCost) {
        //Step.1 获取数据
        QueryWrapper<BizProjectCost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        if (ObjectUtil.isNotNull(projectCost.getProjectName())) {
            queryWrapper.like("project_name", projectCost.getProjectName());
        }
        if (ObjectUtil.isNotNull(projectCost.getStaff())) {
            queryWrapper.like("staff", projectCost.getStaff());
        }
        if (ObjectUtil.isNotNull(projectCost.getBeginDate())) {
            queryWrapper.ge("create_time", projectCost.getBeginDate());
        }
        if (ObjectUtil.isNotNull(projectCost.getEndDate())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(projectCost.getEndDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.lt("create_time", cal.getTime());
        }
        queryWrapper.select("distinct project_id as projectId, staff_id as staffId, date_format(create_time,'%Y-%m-%d') as createTime").orderByDesc("create_time");
        Page<BizProjectCost> page = new Page<BizProjectCost>(1, 10000);
        IPage<BizProjectCost> pageList = projectCostService.page(page, queryWrapper);

        // 获取字典配置
        List<DictModel> costList = sysDictService.queryDictItemsByCode("project_cost_key");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> resultList = new ArrayList<>();
//        List<JSONObject> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pageList.getRecords()) && pageList.getRecords().size() > 0) {
            for (BizProjectCost data : pageList.getRecords()) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                queryWrapper.in("project_id", data.getProjectId());
                queryWrapper.in("staff_id", data.getStaffId());
                queryWrapper.ge("create_time", data.getCreateTime());
                Calendar endtime = Calendar.getInstance();
                endtime.setTime(data.getCreateTime());
                endtime.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.lt("create_time", endtime.getTime());
                List<BizProjectCost> dataList = projectCostService.list(queryWrapper);

//                JSONObject json = new JSONObject();
                Map<String, Object> mp = new HashMap<>();
//                mp.put("id", String.format("%s_%s_%s", data.getProjectId(), data.getStaffId(), data.getCreateTime().getTime()));
                mp.put("projectId", data.getProjectId());
                mp.put("projectName", dataList.stream().findFirst().get().getProjectName());
                mp.put("staffId", data.getStaffId());
                mp.put("staff", dataList.stream().findFirst().get().getStaff());
                mp.put("createTime", dateFormat.format(data.getCreateTime()));
                String costRemark = "";
                for (DictModel dict : costList) {
                    Boolean mapFlag = false;
                    if (dataList.stream().filter(e -> e.getCostKey().equals(dict.getValue())).count() > 0) {
                        BizProjectCost item = dataList.stream().filter(e -> e.getCostKey().equals(dict.getValue())).findFirst().orElseGet(null);
                        if (ObjectUtil.isNotNull(item)) {
                            mp.put(String.format("%s_cost", dict.getValue()), item.getCostValue());
                            mp.put(String.format("%s_remark", dict.getValue()), item.getCostRemark());
                            mapFlag = true;
                        }
                    }
                    if (!mapFlag) {
                        mp.put(String.format("%s_cost", dict.getValue()), "");
                        mp.put(String.format("%s_remark", dict.getValue()), "");
                    }
                }
                resultList.add(mp);
            }
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new XComEntityExcelView());
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "项目费用列表");
        List<ExcelExportEntity> mapList = new ArrayList<>();
//        mapList.add(new ExcelExportEntity("项目Id", "projectId"));
        mapList.add(new ExcelExportEntity("项目名称", "projectName"));
//        mapList.add(new ExcelExportEntity("服务人Id", "staffId"));
        mapList.add(new ExcelExportEntity("服务人", "staff"));
        mapList.add(new ExcelExportEntity("时间", "createTime"));
        for (DictModel dict : costList) {
            mapList.add(new ExcelExportEntity(dict.getText(), String.format("%s_cost", dict.getValue())));
            mapList.add(new ExcelExportEntity(String.format("%s_备注", dict.getText()), String.format("%s_remark", dict.getValue())));
        }
        mv.addObject("exportEntity", mapList);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("项目费用列表", "导出人:" + user.getRealname(), "导出信息");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, resultList);
        return mv;

    }

    /**
     * @return
     * @功能：
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public Result<Boolean> addOrEdit(@RequestBody JSONObject json) {
        Result<Boolean> result = new Result<Boolean>();
        try {
            if (json == null) {
                result.error500("参数错误");
            } else {
                String id = "";
                if (json.containsKey("id")) {
                    id = json.getString("id").trim();
                }
                String projectId = json.getString("projectId").trim();
                String staffId = "";
                if (json.containsKey("staffId")) {
                    staffId = json.getString("staffId").trim();
                }
                String staff = "";
                if (json.containsKey("staff")) {
                    staff = json.getString("staff").trim();
                }
                Calendar time = Calendar.getInstance();
                Calendar idTime = Calendar.getInstance();
                // 有id，必须有服务人
                if (!StringUtil.isNullOrEmpty(id)) {
                    if (StringUtil.isNullOrEmpty(staffId) || StringUtil.isNullOrEmpty(staff)) {
                        throw new JeecgBootException("请注意，服务人未确定");
                    }
                    // 获取创建时间
                    String createTimeStr = json.getString("createTime").trim();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date createTime = formatter.parse(createTimeStr);
                    time.setTime(createTime);

                    //通过id验证数据
                    String[] idData = id.split("_");
                    if (idData.length != 3) {
                        throw new JeecgBootException("请刷新页面提交");
                    }
                    if (!projectId.equals(idData[0])) {
                        throw new JeecgBootException("请刷新页面提交");
                    }
                    if (!staffId.equals(idData[1])) {
                        throw new JeecgBootException("请刷新页面提交");
                    }
                    Date idDate = new Date(Long.parseLong(idData[2]));
                    idTime.setTime(idDate);
                    idTime.set(Calendar.HOUR, 0);
                    idTime.set(Calendar.MINUTE, 0);
                    idTime.set(Calendar.SECOND, 0);
                    idTime.set(Calendar.MILLISECOND, 0);
                    if (time.getTime().getTime() != idTime.getTime().getTime()) {
                        throw new JeecgBootException("请刷新页面提交");
                    }
                } else {
                    // 无id，是为新增，默认未当前登录人
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    staffId = sysUser.getId();
                    staff = sysUser.getRealname();
                }
                // 设置创建时间的时分秒为0
                time.set(Calendar.HOUR, 0);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.SECOND, 0);
                time.set(Calendar.MILLISECOND, 0);

                BizProject project = projectService.getById(projectId);
                if (project == null || project.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                    throw new JeecgBootException("请注意，未找到对应实体");
                } else {
                    // 获取当前登录人该项目 指定时间录入有效的花费
                    QueryWrapper<BizProjectCost> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("project_id", project.getId());
                    queryWrapper.eq("staff_id", staffId);
                    queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                    queryWrapper.ge("create_time", time.getTime());
                    time.add(Calendar.DAY_OF_MONTH, 1);
                    queryWrapper.lt("create_time", time.getTime());
                    List<BizProjectCost> existCost = projectCostService.list(queryWrapper);
                    // 获取字典配置
                    List<DictModel> costList = sysDictService.queryDictItemsByCode("project_cost_key");
                    if (CollectionUtils.isEmpty(costList) || costList.size() == 0) {
                        throw new JeecgBootException("请注意，未配置费用类型");
                    }
                    List<BizProjectCost> saveDataList = new ArrayList<>();
                    List<BizProjectCost> updateDataList = new ArrayList<>();
                    for (DictModel cost : costList) {
                        String costValueStr = "0";
                        // 未设置，不处理
                        if (json.containsKey(String.format("%s_cost", cost.getValue()))) {
                            costValueStr = json.getString(String.format("%s_cost", cost.getValue())).trim();
                        } else {
                            continue;
                        }
                        // 花费值
                        BigDecimal costValue = new BigDecimal("0");
                        if (!StringUtil.isNullOrEmpty(costValueStr)) {
                            costValue = new BigDecimal(costValueStr);
                        }
                        String costRemark = "";
                        if (json.containsKey(String.format("%s_remark", cost.getValue()))) {
                            costRemark = json.getString(String.format("%s_remark", cost.getValue())).trim();
                        }
                        BizProjectCost tempData = new BizProjectCost();
                        // 确保一个类型的花费只有要给值
                        if (!CollectionUtils.isEmpty(existCost) && existCost.size() > 0
                                && existCost.stream().filter(e -> e.getCostKey().equals(cost.getValue())).count() == 1) {
                            tempData = existCost.stream().filter(e -> e.getCostKey().equals(cost.getValue())).findFirst().get();
                            tempData.setCostValue(costValue);
                            tempData.setCostRemark(costRemark);
                            tempData.setUpdateTime(new Date());
                            updateDataList.add(tempData);
                        } else {
                            tempData.setProjectId(project.getId());
                            tempData.setProjectName(project.getProjectName());
                            tempData.setCostKey(cost.getValue());
                            tempData.setCostValue(costValue);
                            tempData.setCostRemark(costRemark);
                            tempData.setStaffId(staffId);
                            tempData.setStaff(staff);
                            tempData.setDelFlag(CommonConstant.DEL_FLAG_0);
                            tempData.setCreateTime(idTime.getTime());
                            saveDataList.add(tempData);
                        }
                    }
                    projectCostService.saveBatch(saveDataList);
                    projectCostService.updateBatchById(updateDataList);
                    result.success("保存成功！");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param ids
     * @return
     * @功能：批量删除
     */
    @ApiOperation("批量删除")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            List<String> idData = Arrays.asList(ids.split(","));
            if (CollectionUtils.isNotEmpty(idData)) {
                for (String id : idData) {
                    deleteCostData(id);
                }
            }
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
        deleteCostData(id);
        result.success("删除成功!");
        return result;
    }

    private void deleteCostData(String id) {
        String[] idData = id.split("_");
        if (idData.length != 3) {
            throw new JeecgBootException("请刷新页面提交");
        }
        Date idDate = new Date(Long.parseLong(idData[2]));
        Calendar idTime = Calendar.getInstance();
        idTime.setTime(idDate);
        idTime.set(Calendar.HOUR, 0);
        idTime.set(Calendar.MINUTE, 0);
        idTime.set(Calendar.SECOND, 0);
        idTime.set(Calendar.MILLISECOND, 0);
        LambdaQueryWrapper<BizProjectCost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BizProjectCost::getProjectId, idData[0]);
        lambdaQueryWrapper.eq(BizProjectCost::getStaffId, idData[1]);
        lambdaQueryWrapper.ge(BizProjectCost::getCreateTime, idTime.getTime());
        idTime.add(Calendar.DAY_OF_MONTH, 1);
        lambdaQueryWrapper.lt(BizProjectCost::getCreateTime, idTime.getTime());
        projectCostService.remove(lambdaQueryWrapper);
    }


    @ApiOperation("项目成本核算")
    @RequestMapping(value = "/calculateList", method = RequestMethod.GET)
    public Result<IPage<JSONObject>> calculateList(ProjectCostQuery projectCost, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<JSONObject>> result = new Result<IPage<JSONObject>>();

        result.setSuccess(true);
        result.setResult(null);
        return result;
    }

}
