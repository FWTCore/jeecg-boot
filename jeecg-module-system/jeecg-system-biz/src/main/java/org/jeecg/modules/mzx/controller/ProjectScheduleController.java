package org.jeecg.modules.mzx.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.*;
import org.jeecg.modules.mzx.service.*;
import org.jeecg.modules.mzx.vo.ProjectScheduleQuery;
import org.jeecg.modules.mzx.vo.ProjectScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Api(tags = "项目进度管理")
@RestController
@RequestMapping("/project/schedule")
@Slf4j
public class ProjectScheduleController {

    /**
     * 每天工作小时数常量
     */
    private static final BigDecimal WORK_HOURS_PER_DAY = new BigDecimal("8");

    /**
     * 工时校验阈值（容忍精度误差）
     */
    private static final BigDecimal WORK_HOURS_THRESHOLD = new BigDecimal("8.02");

    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private IBizProjectScheduleUsageService projectScheduleUsageService;
    @Autowired
    private IBizProjectScheduleItemUsageService projectScheduleItemUsageService;
    @Autowired
    private IBizProjectScheduleLogService projectScheduleLogService;
    @Autowired
    private IBizWorkHoursService bizWorkHoursService;
    @Autowired
    private IBizProjectChangeDetailService bizProjectChangeDetailService;


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
    public Result<IPage<BizProjectScheduleLog>> scheduleLoglist(ProjectScheduleQuery serviceLog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {

        Result<IPage<BizProjectScheduleLog>> result = new Result<IPage<BizProjectScheduleLog>>();
        LambdaQueryWrapper<BizProjectScheduleLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizProjectScheduleLog::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (ObjectUtil.isNotNull(serviceLog.getProjectName())) {
            queryWrapper.like(BizProjectScheduleLog::getProjectName, serviceLog.getProjectName());
        }
        if (ObjectUtil.isNotNull(serviceLog.getProjectId())) {
            queryWrapper.eq(BizProjectScheduleLog::getProjectId, serviceLog.getProjectId());
        }
        if (ObjectUtil.isNotNull(serviceLog.getScheduleName())) {
            queryWrapper.like(BizProjectScheduleLog::getScheduleName, serviceLog.getScheduleName());
        }
        if (ObjectUtil.isNotNull(serviceLog.getStaff())) {
            queryWrapper.like(BizProjectScheduleLog::getStaff, serviceLog.getStaff());
        }
        if (ObjectUtil.isNotNull(serviceLog.getBeginDate())) {
            queryWrapper.ge(BizProjectScheduleLog::getCreateTime, serviceLog.getBeginDate());
        }
        if (ObjectUtil.isNotNull(serviceLog.getEndDate())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(serviceLog.getEndDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.lt(BizProjectScheduleLog::getCreateTime, cal.getTime());
        }
        if (ObjectUtil.isNotNull(serviceLog.getServiceType())) {
            queryWrapper.eq(BizProjectScheduleLog::getServiceType, serviceLog.getServiceType());
        }
        if (ObjectUtil.isNotNull(serviceLog.getServiceContent())) {
            queryWrapper.like(BizProjectScheduleLog::getServiceContent, serviceLog.getServiceContent());
        }
        if (ObjectUtil.isNotNull(serviceLog.getProblem())) {
            queryWrapper.like(BizProjectScheduleLog::getProblem, serviceLog.getProblem());
        }
        if (ObjectUtil.isNotNull(serviceLog.getSolution())) {
            queryWrapper.like(BizProjectScheduleLog::getSolution, serviceLog.getSolution());
        }
        if (ObjectUtil.isNotNull(serviceLog.getOvertimeFlag())) {
            queryWrapper.eq(BizProjectScheduleLog::getOvertimeFlag, serviceLog.getOvertimeFlag());
        }
        if (ObjectUtil.isNotNull(serviceLog.getDoneFlag())) {
            queryWrapper.eq(BizProjectScheduleLog::getDoneFlag, serviceLog.getDoneFlag());
        }
        if (ObjectUtil.isNotNull(serviceLog.getArchiveFlag())) {
            queryWrapper.eq(BizProjectScheduleLog::getArchiveFlag, serviceLog.getArchiveFlag());
        }
        // 判断是否有权限，无权限只能查看自己的
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isPermitted("office:management")) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            queryWrapper.eq(BizProjectScheduleLog::getStaffId, sysUser.getId());
        }

        queryWrapper.orderByDesc(BizProjectScheduleLog::getCreateTime);
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
                if (!project.getProjectStatus().equals("1") && !project.getProjectStatus().equals("10")) {
                    throw new JeecgBootException("进行中或已完成的项目才能填写服务日志");
                }
                String scheduleName = projectScheduleItemUsageService.getItemFullNameByItemId(projectScheduleLog.getProjectScheduleUsageItemId());
                if (StringUtil.isNullOrEmpty(scheduleName)) {
                    throw new JeecgBootException("项目进度不存在");
                } else {
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

                    projectScheduleLog.setProjectName(project.getProjectName());
                    projectScheduleLog.setScheduleName(scheduleName);
                    projectScheduleLog.setStaffId(sysUser.getId());
                    projectScheduleLog.setStaff(sysUser.getRealname());

                    // 工时双写：小时转天，保留3位小数
                    if (projectScheduleLog.getWorkHoursHour() != null) {
                        BigDecimal workHours = projectScheduleLog.getWorkHoursHour()
                            .divide(WORK_HOURS_PER_DAY, 3, RoundingMode.HALF_UP);
                        projectScheduleLog.setWorkHours(workHours);
                    }

                    if (ObjectUtil.isNull(projectScheduleLog.getOvertimeFlag()) || projectScheduleLog.getOvertimeFlag() != 1) {
                        projectScheduleLog.setOvertime(null);
                    }
                    if (ObjectUtils.isNotEmpty(projectScheduleLog.getCreateTime())) {
                        // 7 天
                        Long intervalImpl = projectScheduleLog.getCreateTime().getTime() - Calendar.getInstance().getTime().getTime();
                        if (intervalImpl / (24 * 60 * 60 * 1000) >= 0 && intervalImpl / (24 * 60 * 60 * 1000) <= 8) {
                            projectScheduleLog.setCreateTime(projectScheduleLog.getCreateTime());
                        } else {
                            result.error500("只能补录前7天的项目服务日志");
                        }
                    } else {
                        projectScheduleLog.setCreateTime(new Date());
                    }

                    // 校验工时是否超过每日限制
                    validateWorkHoursLimit(sysUser.getId(), projectScheduleLog.getCreateTime(),
                        projectScheduleLog.getWorkHoursHour(), null);

                    projectScheduleLog.setDelFlag(CommonConstant.DEL_FLAG_0);
                    if (StringUtils.isBlank(projectScheduleLog.getServiceType())) {
                        projectScheduleLog.setServiceType(null);
                    }
                    if (!checkNumber(projectScheduleLog.getServiceContent())) {
                        throw new JeecgBootException("服务内容需要有具体的工作量描述");
                    }
                    projectScheduleLogService.save(projectScheduleLog);
                }
                result.success("保存成功！");

                bizProjectChangeDetailService.insertOrUpdateData(projectScheduleLog.getProjectId(), projectScheduleLog.getCreateTime());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
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
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                // 判断是否有权限，无权限，只能编辑自己的
                Subject subject = SecurityUtils.getSubject();
                if (!subject.isPermitted("office:management")) {
                    if (!data.getStaffId().equals(sysUser.getId())) {
                        throw new JeecgBootException("只能编辑自己的数据");
                    }
                }

                // 工时双写：小时转天，保留3位小数（必须在校验之前执行）
                if (projectScheduleLog.getWorkHoursHour() != null) {
                    BigDecimal workHours = projectScheduleLog.getWorkHoursHour()
                        .divide(WORK_HOURS_PER_DAY, 3, RoundingMode.HALF_UP);
                    projectScheduleLog.setWorkHours(workHours);
                }

                // 校验工时是否超过每日限制（编辑时需扣除原记录工时）
                validateWorkHoursLimit(data.getStaffId(), data.getCreateTime(),
                    projectScheduleLog.getWorkHoursHour(), data.getWorkHoursHour());

                data.setScheduleName(scheduleName);
                if (StringUtils.isBlank(projectScheduleLog.getServiceType())) {
                    data.setServiceType(null);
                } else {
                    data.setServiceType(projectScheduleLog.getServiceType());
                }
                data.setServiceContent(projectScheduleLog.getServiceContent());
                if (!checkNumber(projectScheduleLog.getServiceContent())) {
                    throw new JeecgBootException("服务内容需要有具体的工作量描述");
                }

                // 更新工时字段
                data.setWorkHours(projectScheduleLog.getWorkHours());
                data.setWorkHoursHour(projectScheduleLog.getWorkHoursHour());

                data.setOvertimeFlag(projectScheduleLog.getOvertimeFlag());
                if (ObjectUtil.isNull(projectScheduleLog.getOvertimeFlag()) || projectScheduleLog.getOvertimeFlag() != 1) {
                    data.setOvertime(null);
                } else {
                    data.setOvertime(projectScheduleLog.getOvertime());
                }
                data.setDoneFlag(projectScheduleLog.getDoneFlag());
                data.setArchiveFlag(projectScheduleLog.getArchiveFlag());
                data.setNextPlanContent(projectScheduleLog.getNextPlanContent());
                data.setNextPlanTime(projectScheduleLog.getNextPlanTime());
                data.setProblem(projectScheduleLog.getProblem());
                data.setSolution(projectScheduleLog.getSolution());
                data.setUpdateTime(new Date());
                projectScheduleLogService.updateById(data);
                bizProjectChangeDetailService.insertOrUpdateData(projectScheduleLog.getProjectId(), data.getCreateTime());
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
            List<String> idList = Arrays.asList(ids.split(","));
            // 判断是否有权限，无权限只能查看自己的
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isPermitted("office:management")) {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                List<BizProjectScheduleLog> bizProjectScheduleLogs = projectScheduleLogService.listByIds(idList);
                if (bizProjectScheduleLogs.stream().noneMatch(e -> e.getStaffId().equals(sysUser.getId()))) {
                    throw new JeecgBootException("只能删除自己的数据");
                }
            }
            projectScheduleLogService.removeByIds(idList);

            bizProjectChangeDetailService.insertOrUpdateData(idList);
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
            // 判断是否有权限，无权限只能查看自己的
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isPermitted("office:management")) {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (!data.getStaffId().equals(sysUser.getId())) {
                    throw new JeecgBootException("只能删除自己的数据");
                }
            }
            projectScheduleLogService.removeById(id);
            bizProjectChangeDetailService.insertOrUpdateData(id);
            result.success("删除成功!");
        }
        return result;
    }

    @RequestMapping(value = "/queryusageschedule", method = RequestMethod.GET)
    public Result<List<ProjectScheduleVO>> queryUsageSchedule(@RequestParam(name = "projectId", required = true) String projectId) {
        Result<List<ProjectScheduleVO>> result = new Result<>();
        List<ProjectScheduleVO> list = projectScheduleItemUsageService.queryUsageSchedule(projectId);
        if (list == null || list.size() <= 0) {
            result.error500("未找到项目进度数据");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    private boolean checkNumber(String logContent) {
        if (StringUtils.isBlank(logContent)) {
            return false;
        }
        return logContent.matches(".*\\d+.*");
    }

    /**
     * 校验工时是否超过每日限制
     *
     * @param staffId             员工ID
     * @param workDate            工作日期
     * @param currentWorkHoursHour 当前工时（小时）
     * @param originalWorkHoursHour 原工时（小时），编辑时传入，新增时传null
     */
    private void validateWorkHoursLimit(String staffId, Date workDate,
                                        BigDecimal currentWorkHoursHour, BigDecimal originalWorkHoursHour) {
        // 计算当日时间范围
        Date startTime = DateUtil.beginOfDay(workDate);
        Calendar instance = Calendar.getInstance();
        instance.setTime(startTime);
        instance.add(Calendar.DAY_OF_MONTH, 1);
        Date endTime = instance.getTime();

        // 查询当日已填工时（单位：天），转换为小时统一计算
        BigDecimal existWorkHoursInHour = bizWorkHoursService.getTotalWorkHours(staffId, startTime, endTime);

        // 计算总工时（单位：小时）
        BigDecimal totalWorkHoursInHour = existWorkHoursInHour.add(currentWorkHoursHour);

        // 编辑时需扣除原记录工时
        if (originalWorkHoursHour != null) {
            totalWorkHoursInHour = totalWorkHoursInHour.subtract(originalWorkHoursHour);
        }

        // 工时超过限制（容忍精度误差）
        if (totalWorkHoursInHour.compareTo(WORK_HOURS_THRESHOLD) > 0) {
            // 计算剩余可填工时：8小时 - 其他记录工时
            BigDecimal otherRecordsHours = existWorkHoursInHour;
            if (originalWorkHoursHour != null) {
                otherRecordsHours = existWorkHoursInHour.subtract(originalWorkHoursHour);
            }
            BigDecimal remainHours = WORK_HOURS_PER_DAY.subtract(otherRecordsHours);
            throw new JeecgBootException(String.format("日期：%s 填写工时累计超过8小时，剩余【%s】小时可填",
                DateUtil.format(startTime, "yyyy-MM-dd"), remainHours.setScale(1, RoundingMode.HALF_UP)));
        }
    }

}