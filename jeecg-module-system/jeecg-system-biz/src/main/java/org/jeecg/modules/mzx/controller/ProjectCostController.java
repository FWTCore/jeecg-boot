package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.message.enums.RangeDateEnum;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.BizCustomerServiceLog;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.service.IBizCustomerServiceLogService;
import org.jeecg.modules.mzx.service.IBizProjectCostService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.jeecg.modules.mzx.vo.CostModel;
import org.jeecg.modules.mzx.vo.ProjectCostModel;
import org.jeecg.modules.system.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "项目费用")
@RestController
@RequestMapping("/project/Cost")
@Slf4j
public class ProjectCostController {

    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private IBizProjectCostService projectCostService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectCost>> queryPageList(BizProjectCost projectCost, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectCost>> result = new Result<IPage<BizProjectCost>>();
        QueryWrapper<BizProjectCost> queryWrapper = QueryGenerator.initQueryWrapper(projectCost, req.getParameterMap());
        Page<BizProjectCost> page = new Page<BizProjectCost>(pageNo, pageSize);
        IPage<BizProjectCost> pageList = projectCostService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * @return
     * @功能：
     */
    @RequestMapping(value = "/addOrEdit", method = RequestMethod.POST)
    public Result<Boolean> addOrEdit(@RequestBody ProjectCostModel projectCost) {
        Result<Boolean> result = new Result<Boolean>();
        try {
            BizProject project = projectService.getById(projectCost.getProjectId());
            if (project == null || project.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                result.error500("未找到对应实体");
            } else {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                // 获取当前登录人该项目今天录入有效的花费
                QueryWrapper<BizProjectCost> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("project_id", project.getId());
                queryWrapper.eq("staff_id", sysUser.getId());
                queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0);
                //时间
                Date[] dates = RangeDateEnum.getRangeArray(RangeDateEnum.JT.getKey());
                queryWrapper.ge("begin_date", dates[0]);
                queryWrapper.le("end_date", dates[1]);
                List<BizProjectCost> todayExistCost = projectCostService.list(queryWrapper);
                List<BizProjectCost> saveDataList = new ArrayList<>();
                List<BizProjectCost> updateDataList = new ArrayList<>();

                for (CostModel cost : projectCost.getCostModels()) {
                    BizProjectCost tempData = new BizProjectCost();
                    // 确保一个类型的花费只有要给值
                    if (!CollectionUtils.isEmpty(todayExistCost) && todayExistCost.size() > 0
                            && todayExistCost.stream().filter(e -> e.getCostKey().equals(cost.getCostKey())).count() == 1) {
                        tempData = todayExistCost.stream().filter(e -> e.getCostKey().equals(cost.getCostKey())).findFirst().get();
                        tempData.setCostValue(cost.getCostValue());
                        tempData.setCostRemark(cost.getCostRemark());
                        tempData.setUpdateTime(new Date());
                        updateDataList.add(tempData);
                    } else {
                        tempData.setProjectId(project.getId());
                        tempData.setProjectName(project.getProjectName());
                        tempData.setCostKey(cost.getCostKey());
                        tempData.setCostValue(cost.getCostValue());
                        tempData.setCostRemark(cost.getCostRemark());
                        tempData.setStaffId(sysUser.getId());
                        tempData.setStaff(sysUser.getRealname());
                        tempData.setDelFlag(CommonConstant.DEL_FLAG_0);
                        saveDataList.add(tempData);
                    }
                }
                projectCostService.saveBatch(saveDataList);
                projectCostService.updateBatchById(updateDataList);
                result.success("保存成功！");
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
            projectCostService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizProjectCost data = projectCostService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            projectCostService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }


}
