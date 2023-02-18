package org.jeecg.modules.mzx.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mchange.v2.lang.StringUtils;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.*;
import org.jeecg.modules.mzx.service.IBizProjectScheduleItemUsageService;
import org.jeecg.modules.mzx.service.IBizProjectScheduleLogService;
import org.jeecg.modules.mzx.service.IBizProjectScheduleUsageService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;


@Api(tags = "项目进度管理")
@RestController
@RequestMapping("/project/schedule")
@Slf4j
public class ProjectScheduleController {

    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private IBizProjectScheduleUsageService projectScheduleUsageService;
    @Autowired
    private IBizProjectScheduleItemUsageService projectScheduleItemUsageService;
    @Autowired
    private IBizProjectScheduleLogService projectScheduleLogService;


    @RequestMapping(value = "/queryByProjectId", method = RequestMethod.GET)
    public Result<BizProjectScheduleUsage> queryByProjectId(@RequestParam(name = "projectId", required = true) String projectId) {
        Result<BizProjectScheduleUsage> result = new Result<>();
        BizProjectScheduleUsage resultData = projectScheduleUsageService.getOne(new QueryWrapper<BizProjectScheduleUsage>().lambda().eq(BizProjectScheduleUsage::getProjectId, projectId));
        if (resultData == null) {
            result.error500("未找到模板数据");
        } else {
            result.setSuccess(true);
            result.setResult(resultData);
        }
        return result;
    }


    @ApiOperation("获取列表")
    @RequestMapping(value = "/scheduleLoglist", method = RequestMethod.GET)
    public Result<IPage<BizProjectScheduleLog>> scheduleLoglist(BizProjectScheduleLog serviceLog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectScheduleLog>> result = new Result<IPage<BizProjectScheduleLog>>();
        QueryWrapper<BizProjectScheduleLog> queryWrapper = QueryGenerator.initQueryWrapper(serviceLog, req.getParameterMap());
        Page<BizProjectScheduleLog> page = new Page<BizProjectScheduleLog>(pageNo, pageSize);
        IPage<BizProjectScheduleLog> pageList = projectScheduleLogService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * @return
     * @功能：
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizProjectScheduleLog> add(@RequestBody BizProjectScheduleLog projectScheduleLog) {
        Result<BizProjectScheduleLog> result = new Result<BizProjectScheduleLog>();
        try {
            BizProject project = projectService.getById(projectScheduleLog.getProjectId());
            if (project == null || project.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                result.error500("未找到对应实体");
            } else {
                String scheduleName = projectScheduleItemUsageService.getItemFullNameByItemId(projectScheduleLog.getProjectScheduleUsageItemId());
                if (StringUtil.isNullOrEmpty(scheduleName)) {
                    result.error500("项目进度不存在");
                } else {
                    projectScheduleLog.setProjectName(project.getProjectName());
                    projectScheduleLog.setScheduleName(scheduleName);
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    projectScheduleLog.setStaffId(sysUser.getId());
                    projectScheduleLog.setStaff(sysUser.getRealname());
                    projectScheduleLog.setCreateTime(new Date());
                    projectScheduleLog.setDelFlag(CommonConstant.DEL_FLAG_0);
                    projectScheduleLogService.save(projectScheduleLog);
                }
                result.success("保存成功！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param projectScheduleLog
     * @return
     * @功能：编辑
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizProjectScheduleLog> edit(@RequestBody BizProjectScheduleLog projectScheduleLog) {
        Result<BizProjectScheduleLog> result = new Result<BizProjectScheduleLog>();
        BizProjectScheduleLog data = projectScheduleLogService.getById(projectScheduleLog.getId());
        if (data == null || data.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
            result.error500("未找到对应实体");
        } else {
            String scheduleName = projectScheduleItemUsageService.getItemFullNameByItemId(projectScheduleLog.getProjectScheduleUsageItemId());
            if (StringUtil.isNullOrEmpty(scheduleName)) {
                result.error500("项目进度不存在");
            } else {
                data.setScheduleName(scheduleName);
                data.setServiceType(projectScheduleLog.getServiceType());
                data.setServiceContent(projectScheduleLog.getServiceContent());
                data.setWorkHours(projectScheduleLog.getWorkHours());
                data.setOvertimeFlag(projectScheduleLog.getOvertimeFlag());
                data.setOverTime(projectScheduleLog.getOverTime());
                data.setDoneFlag(projectScheduleLog.getDoneFlag());
                data.setArchiveFlag(projectScheduleLog.getArchiveFlag());
                data.setNextPlanContent(projectScheduleLog.getNextPlanContent());
                data.setNextPlanTime(projectScheduleLog.getNextPlanTime());
                data.setUpdateTime(new Date());
                projectScheduleLogService.updateById(data);
                result.success("编辑成功!");
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
            projectScheduleLogService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizProjectScheduleLog data = projectScheduleLogService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            projectScheduleLogService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }


}
