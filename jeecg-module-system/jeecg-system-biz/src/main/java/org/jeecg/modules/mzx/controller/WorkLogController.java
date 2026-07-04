package org.jeecg.modules.mzx.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizCustomerServiceLog;
import org.jeecg.modules.mzx.entity.BizProjectScheduleLog;
import org.jeecg.modules.mzx.entity.BizWorkLog;
import org.jeecg.modules.mzx.service.IBizWorkHoursService;
import org.jeecg.modules.mzx.service.IBizWorkLogService;
import org.jeecg.modules.mzx.vo.WorkLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Api(tags = "工作日志")
@RestController
@RequestMapping("/worklog")
@Slf4j
public class WorkLogController {

    /**
     * 每天工作小时数常量
     */
    private static final BigDecimal WORK_HOURS_PER_DAY = new BigDecimal("8");

    /**
     * 工时校验阈值（容忍精度误差）
     */
    private static final BigDecimal WORK_HOURS_THRESHOLD = new BigDecimal("8.02");

    @Autowired
    private IBizWorkLogService workLogService;
    @Autowired
    private IBizWorkHoursService bizWorkHoursService;


    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizWorkLog>> queryPageList(WorkLogQuery workLog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizWorkLog>> result = new Result<IPage<BizWorkLog>>();
        req.getParameterMap().get("column")[0]="createTime";
        req.getParameterMap().get("order")[0]="DESC";
        LambdaQueryWrapper<BizWorkLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizWorkLog::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (ObjectUtil.isNotNull(workLog.getStaff())) {
            queryWrapper.like(BizWorkLog::getStaff, workLog.getStaff());
        }
        if (ObjectUtil.isNotNull(workLog.getServiceContent())) {
            queryWrapper.like(BizWorkLog::getServiceContent, workLog.getServiceContent());
        }
        if (ObjectUtil.isNotNull(workLog.getBeginDate())) {
            queryWrapper.ge(BizWorkLog::getCreateTime, workLog.getBeginDate());
        }
        if (ObjectUtil.isNotNull(workLog.getEndDate())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(workLog.getEndDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.lt(BizWorkLog::getCreateTime, cal.getTime());
        }
        // 判断是否有权限，无权限只能查看自己的
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isPermitted("office:management")) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            queryWrapper.eq(BizWorkLog::getStaffId, sysUser.getId());
        }
        queryWrapper.orderByDesc(BizWorkLog::getCreateTime);
        Page<BizWorkLog> page = new Page<BizWorkLog>(pageNo, pageSize);
        IPage<BizWorkLog> pageList = workLogService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * @return
     * @功能：
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizWorkLog> add(@RequestBody BizWorkLog workLog) {
        Result<BizWorkLog> result = new Result<BizWorkLog>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            workLog.setStaffId(sysUser.getId());
            workLog.setStaff(sysUser.getRealname());
            workLog.setCreateTime(new Date());
            workLog.setDelFlag(CommonConstant.DEL_FLAG_0);

            // 工时双写：小时转天，保留3位小数
            if (workLog.getWorkHoursHour() != null) {
                BigDecimal workHours = workLog.getWorkHoursHour()
                    .divide(WORK_HOURS_PER_DAY, 3, RoundingMode.HALF_UP);
                workLog.setWorkHours(workHours);
            }

            // 校验工时是否超过每日限制
            validateWorkHoursLimit(sysUser.getId(), workLog.getCreateTime(),
                workLog.getWorkHoursHour(), null);

            workLogService.save(workLog);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * @param workLog
     * @return
     * @功能：编辑
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizWorkLog> edit(@RequestBody BizWorkLog workLog) {
        Result<BizWorkLog> result = new Result<BizWorkLog>();
        BizWorkLog data = workLogService.getById(workLog.getId());
        if (data == null || data.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
            result.error500("未找到对应实体");
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
            if (workLog.getWorkHoursHour() != null) {
                BigDecimal workHours = workLog.getWorkHoursHour()
                    .divide(WORK_HOURS_PER_DAY, 3, RoundingMode.HALF_UP);
                workLog.setWorkHours(workHours);
            }

            // 校验工时是否超过每日限制（编辑时需扣除原记录工时）
            validateWorkHoursLimit(data.getStaffId(), data.getCreateTime(),
                workLog.getWorkHoursHour(), data.getWorkHoursHour());

            data.setServiceContent(workLog.getServiceContent());
            data.setWorkHours(workLog.getWorkHours());
            data.setWorkHoursHour(workLog.getWorkHoursHour());
            data.setNextPlanContent(workLog.getNextPlanContent());
            data.setNextPlanTime(workLog.getNextPlanTime());
            data.setUpdateTime(new Date());
            boolean ok = workLogService.updateById(data);
            if (ok) {
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
                List<BizWorkLog> bizWorkLogs = workLogService.listByIds(idList);
                if (bizWorkLogs.stream().noneMatch(e -> e.getStaffId().equals(sysUser.getId()))) {
                    throw new JeecgBootException("只能删除自己的数据");
                }
            }
            workLogService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizWorkLog data = workLogService.getById(id);
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
            workLogService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 校验工时是否超过每日限制
     *
     * @param staffId              员工ID
     * @param workDate             工作日期
     * @param currentWorkHoursHour 当前工时（小时）
     * @param originalWorkHoursHour 原工时（小时），编辑时传入，新增时传null
     */
    private void validateWorkHoursLimit(String staffId, Date workDate,
                                        BigDecimal currentWorkHoursHour, BigDecimal originalWorkHoursHour) {
        if (currentWorkHoursHour == null || currentWorkHoursHour.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

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