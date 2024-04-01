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
    public Result<IPage<Map<String, Object>>> queryPageList(ProjectCostQuery projectCost, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Map<String, Object>>> result = new Result<IPage<Map<String, Object>>>();

        IPage<BizProjectCost> pageList = getProjectCostSubject(projectCost, pageNo, pageSize);

        IPage<Map<String, Object>> resultList = new Page<>();
        resultList.setPages(pageList.getPages());
        resultList.setSize(pageList.getSize());
        resultList.setTotal(pageList.getTotal());
        resultList.setRecords(getProjectCostDetail(pageList.getRecords()));
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
        IPage<BizProjectCost> pageList = getProjectCostSubject(projectCost, 1, 5000);

        // 获取字典配置
        List<DictModel> costList = sysDictService.queryDictItemsByCode("project_cost_key");

        List<Map<String, Object>> resultList = getProjectCostDetail(pageList.getRecords());

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new XComEntityExcelView());
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "项目费用列表");
        List<ExcelExportEntity> mapList = new ArrayList<>();
        mapList.add(new ExcelExportEntity("项目名称", "projectName"));
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
     * 获取项目费用主体
     * @param projectCost
     * @param pageNo
     * @param pageSize
     * @return
     */
    private IPage<BizProjectCost> getProjectCostSubject(ProjectCostQuery projectCost, long pageNo, long pageSize) {
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
        queryWrapper.select("distinct project_id as projectId, staff_id as staffId, `period` as period").orderByDesc("create_time");
        Page<BizProjectCost> page = new Page<BizProjectCost>(pageNo, pageSize);
        return projectCostService.page(page, queryWrapper);
    }

    /**
     * 获取项目费用明细
     *
     * @param dataList
     * @return
     */
    private List<Map<String, Object>> getProjectCostDetail(List<BizProjectCost> dataList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(dataList)) {
            return resultList;
        }
        // 获取字典配置
        List<DictModel> costList = sysDictService.queryDictItemsByCode("project_cost_key");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 批量获取数据
        QueryWrapper<BizProjectCost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
        queryWrapper.in("project_id", dataList.stream().map(BizProjectCost::getProjectId).collect(Collectors.toList()));
        queryWrapper.in("staff_id", dataList.stream().map(BizProjectCost::getStaffId).collect(Collectors.toList()));
        queryWrapper.in("period", dataList.stream().map(BizProjectCost::getPeriod).collect(Collectors.toList()));
        List<BizProjectCost> detailDataList = projectCostService.list(queryWrapper);


        for (BizProjectCost data : dataList) {
            // 确定当前 项目+服务人+周期 的补助
            List<BizProjectCost> collect = detailDataList.stream().filter(e -> e.getProjectId().equals(data.getProjectId()) && e.getStaffId().equals(data.getStaffId()) && e.getPeriod().equals(data.getPeriod())).collect(Collectors.toList());
            Map<String, Object> mp = new HashMap<>();
            mp.put("id", String.format("%s_%s_%s", data.getProjectId(), data.getStaffId(), data.getPeriod()));
            mp.put("projectId", data.getProjectId());
            mp.put("projectName", collect.stream().findFirst().get().getProjectName());
            mp.put("staffId", data.getStaffId());
            mp.put("staff", collect.stream().findFirst().get().getStaff());
            mp.put("period", data.getPeriod());
            mp.put("createTime", dateFormat.format(collect.stream().findFirst().get().getCreateTime()));
            String costRemark = "";
            for (DictModel dict : costList) {
                boolean mapFlag = false;
                if (collect.stream().anyMatch(e -> e.getCostKey().equals(dict.getValue()))) {
                    List<BizProjectCost> itemList = collect.stream().filter(e -> e.getCostKey().equals(dict.getValue())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(itemList)) {
                        mp.put(String.format("%s_cost", dict.getValue()), itemList.stream().map(BizProjectCost::getCostValue).reduce(BigDecimal.ZERO, BigDecimal::add));
                        mp.put(String.format("%s_remark", dict.getValue()),  String.join(",", itemList.stream().map(BizProjectCost::getCostRemark).collect(Collectors.toList())));
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
        return resultList;
    }

    /**
     * @return
     * @功能：
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public Result<Boolean> addOrEdit(@RequestBody JSONObject json) {
        Result<Boolean> result = new Result<Boolean>();
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

            Calendar currentTime = Calendar.getInstance();
            // yyyyMMdd 当前周期
            int period = currentTime.get(Calendar.YEAR) * 10000 + (currentTime.get(Calendar.MONTH) + 1) * 100 + currentTime.get(Calendar.DAY_OF_MONTH);

            // 有id编辑
            if (!StringUtil.isNullOrEmpty(id)) {
                if (StringUtil.isNullOrEmpty(staffId)) {
                    throw new JeecgBootException("请注意，服务人未确定");
                }
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
                int currentPeriod = period;
                period = Integer.parseInt(idData[2]);
                // 编辑月份
                int editPeriodMonth = period / 100;
                // 本月
                int currentPeriodMonth = currentPeriod / 100;
                // 本月数据允许修改
                if (currentPeriodMonth - editPeriodMonth != 0) {
                    // 非本月，只允许本月1号修改上1个月的数据
                    if (currentPeriodMonth - editPeriodMonth == 1) {
                        if (currentPeriodMonth * 100 + 1 != currentPeriod) {
                            throw new JeecgBootException("只能本月1号修改上一个月数据");
                        }
                    } else {
                        throw new JeecgBootException("只能本月1号修改上一个月数据");
                    }
                }
            } else {
                // 无id，是为新增，默认未当前登录人
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                staffId = sysUser.getId();
                staff = sysUser.getRealname();
            }
            // 获取项目信息
            BizProject project = projectService.getById(projectId);
            if (project == null || project.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("请注意，未找到对应实体");
            } else {
                // 获取当前登录人该项目 指定时间录入有效的花费
                QueryWrapper<BizProjectCost> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id", project.getId());
                queryWrapper.eq("staff_id", staffId);
                queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                queryWrapper.ge("period", period);
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
                    if (CollectionUtils.isNotEmpty(existCost) && existCost.stream().anyMatch(e -> e.getCostKey().equals(cost.getValue()))) {
                        if (existCost.stream().filter(e -> e.getCostKey().equals(cost.getValue())).count() != 1) {
                            throw new JeecgBootException("历史数据存在多条，请联系技术人员处理");
                        }
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
                        tempData.setCreateTime(new Date());
                        tempData.setPeriod(period);
                        saveDataList.add(tempData);
                    }
                }
                projectCostService.saveBatch(saveDataList);
                projectCostService.updateBatchById(updateDataList);
                result.success("保存成功！");
            }
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
        Integer period = Integer.parseInt(idData[2]);
        LambdaQueryWrapper<BizProjectCost> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BizProjectCost::getProjectId, idData[0]);
        lambdaQueryWrapper.eq(BizProjectCost::getStaffId, idData[1]);
        lambdaQueryWrapper.eq(BizProjectCost::getPeriod, period);
        projectCostService.remove(lambdaQueryWrapper);
    }


}
