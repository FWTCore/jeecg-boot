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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizCustomerServiceLog;
import org.jeecg.modules.mzx.entity.BizWorkLog;
import org.jeecg.modules.mzx.service.IBizWorkHoursService;
import org.jeecg.modules.mzx.service.IBizWorkLogService;
import org.jeecg.modules.mzx.vo.WorkLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


@Api(tags = "工作日志")
@RestController
@RequestMapping("/worklog")
@Slf4j
public class WorkLogController {

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

            // 校验总时长
            Calendar instance = Calendar.getInstance();
            Date startTime = DateUtil.beginOfDay(workLog.getCreateTime());
            instance.setTime(startTime);
            instance.add(Calendar.DAY_OF_MONTH, 1);
            Date endTime = instance.getTime();
            BigDecimal workHours = bizWorkHoursService.getTotalWorkHours(sysUser.getId(), startTime, endTime);
            BigDecimal totalWorkHours = workHours.add(workLog.getWorkHours());
            // 工时大于1
            if (totalWorkHours.compareTo(BigDecimal.ONE) > 0) {
                throw new JeecgBootException(String.format("日期：%s 填写工时累计大于1天，剩余【%s】天可填", DateUtil.format(startTime, "yyyy-MM-dd“"), BigDecimal.ONE.subtract(workHours)));
            }

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
            // 校验总时长
            Calendar instance = Calendar.getInstance();
            Date startTime = DateUtil.beginOfDay(data.getCreateTime());
            instance.setTime(startTime);
            instance.add(Calendar.DAY_OF_MONTH, 1);
            Date endTime = instance.getTime();
            BigDecimal workHours = bizWorkHoursService.getTotalWorkHours(sysUser.getId(), startTime, endTime);
            BigDecimal totalWorkHours = workHours.add(workLog.getWorkHours());
            totalWorkHours = totalWorkHours.subtract(data.getWorkHours());
            // 工时大于1
            if (totalWorkHours.compareTo(BigDecimal.ONE) > 0) {
                throw new JeecgBootException(String.format("日期：%s 填写工时累计大于1天，剩余【%s】天可填", DateUtil.format(startTime, "yyyy-MM-dd“"), BigDecimal.ONE.subtract(workHours).add(data.getWorkHours())));
            }

            data.setServiceContent(workLog.getServiceContent());
            data.setWorkHours(workLog.getWorkHours());
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
            workLogService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }


}
