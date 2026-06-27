package org.jeecg.modules.mzx.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizOvertimeRecord;
import org.jeecg.modules.mzx.service.IBizOvertimeRecordService;
import org.jeecg.modules.mzx.service.IBizProjectScheduleItemUsageService;
import org.jeecg.modules.mzx.vo.OvertimeRecordQuery;
import org.jeecg.modules.mzx.vo.ProjectScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 加班记录管理
 */
@Api(tags = "加班记录管理")
@RestController
@RequestMapping("/overtime/record")
@Slf4j
public class OvertimeRecordController {

    @Autowired
    private IBizOvertimeRecordService overtimeRecordService;

    @Autowired
    private IBizProjectScheduleItemUsageService projectScheduleItemUsageService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizOvertimeRecord>> queryPageList(
            OvertimeRecordQuery query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {

        Result<IPage<BizOvertimeRecord>> result = new Result<>();
        LambdaQueryWrapper<BizOvertimeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizOvertimeRecord::getDelFlag, CommonConstant.DEL_FLAG_0);

        // 查询条件
        if (ObjectUtil.isNotNull(query.getStaffName())) {
            queryWrapper.like(BizOvertimeRecord::getStaffName, query.getStaffName());
        }
        if (ObjectUtil.isNotNull(query.getOvertimeDateBegin())) {
            queryWrapper.ge(BizOvertimeRecord::getOvertimeDate, query.getOvertimeDateBegin());
        }
        if (ObjectUtil.isNotNull(query.getOvertimeDateEnd())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(query.getOvertimeDateEnd());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.lt(BizOvertimeRecord::getOvertimeDate, cal.getTime());
        }
        if (ObjectUtil.isNotNull(query.getProjectId())) {
            queryWrapper.eq(BizOvertimeRecord::getProjectId, query.getProjectId());
        }
        if (ObjectUtil.isNotNull(query.getProjectName())) {
            queryWrapper.like(BizOvertimeRecord::getProjectName, query.getProjectName());
        }
        if (ObjectUtil.isNotNull(query.getConfirmStatus())) {
            queryWrapper.eq(BizOvertimeRecord::getConfirmStatus, query.getConfirmStatus());
        }
        if (ObjectUtil.isNotNull(query.getServiceType())) {
            queryWrapper.eq(BizOvertimeRecord::getServiceType, query.getServiceType());
        }
        if (ObjectUtil.isNotNull(query.getServiceContent())) {
            queryWrapper.like(BizOvertimeRecord::getServiceContent, query.getServiceContent());
        }
        if (ObjectUtil.isNotNull(query.getOvertimeReason())) {
            queryWrapper.like(BizOvertimeRecord::getOvertimeReason, query.getOvertimeReason());
        }

        // 权限控制：无 office:management 权限只能查看自己的
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isPermitted("office:management")) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            queryWrapper.eq(BizOvertimeRecord::getStaffId, sysUser.getId());
        }

        queryWrapper.orderByDesc(BizOvertimeRecord::getCreateTime);
        Page<BizOvertimeRecord> page = new Page<>(pageNo, pageSize);
        IPage<BizOvertimeRecord> pageList = overtimeRecordService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation("新增加班申请")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizOvertimeRecord> add(@RequestBody BizOvertimeRecord overtimeRecord) {
        Result<BizOvertimeRecord> result = new Result<>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            overtimeRecord.setStaffId(sysUser.getId());
            overtimeRecord.setStaffName(sysUser.getRealname());
            overtimeRecord.setCreateBy(sysUser.getUsername());

            overtimeRecordService.submitOvertime(overtimeRecord);
            result.success("提交成功！");
            result.setResult(overtimeRecord);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    @ApiOperation("编辑加班申请")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizOvertimeRecord> edit(@RequestBody BizOvertimeRecord overtimeRecord) {
        Result<BizOvertimeRecord> result = new Result<>();
        BizOvertimeRecord data = overtimeRecordService.getById(overtimeRecord.getId());
        if (data == null || data.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
            result.error500("未找到对应实体");
        } else {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            Subject subject = SecurityUtils.getSubject();

            // 权限控制：只能编辑自己的且未确认的记录
            if (!subject.isPermitted("office:management")) {
                if (!data.getStaffId().equals(sysUser.getId())) {
                    throw new JeecgBootException("只能编辑自己的数据");
                }
            }
            if (data.getConfirmStatus() != 0) {
                throw new JeecgBootException("只能编辑待确认的记录");
            }

            // 校验加班时长
            String validateMsg = overtimeRecordService.validateOvertimeHours(overtimeRecord.getOvertimeHours());
            if (validateMsg != null) {
                throw new JeecgBootException(validateMsg);
            }

            data.setOvertimeDate(overtimeRecord.getOvertimeDate());
            data.setOvertimeHours(overtimeRecord.getOvertimeHours());
            data.setOvertimeReason(overtimeRecord.getOvertimeReason());
            data.setProjectId(overtimeRecord.getProjectId());
            data.setProjectScheduleUsageItemId(overtimeRecord.getProjectScheduleUsageItemId());
            data.setServiceType(overtimeRecord.getServiceType());
            data.setServiceContent(overtimeRecord.getServiceContent());
            data.setUpdateBy(sysUser.getUsername());
            data.setUpdateTime(new Date());

            overtimeRecordService.submitOvertime(data); // 会自动设置项目名称和进度名称
            result.success("编辑成功！");
        }
        return result;
    }

    @ApiOperation("确认加班申请")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public Result<String> confirm(@RequestBody BizOvertimeRecord overtimeRecord) {

        Result<String> result = new Result<>();
        try {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isPermitted("office:management")) {
                throw new JeecgBootException("只有管理人员才能确认加班申请");
            }

            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            overtimeRecordService.confirmOvertime(overtimeRecord.getId(), sysUser.getId(), sysUser.getRealname());
            result.success("确认成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    @ApiOperation("批量删除")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            List<String> idList = Arrays.asList(ids.split(","));
            Subject subject = SecurityUtils.getSubject();

            if (!subject.isPermitted("office:management")) {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                List<BizOvertimeRecord> records = overtimeRecordService.listByIds(idList);
                // 只能删除自己提交的待确认记录
                for (BizOvertimeRecord record : records) {
                    if (!record.getStaffId().equals(sysUser.getId())) {
                        throw new JeecgBootException("只能删除自己的数据");
                    }
                    if (record.getConfirmStatus() != 0) {
                        throw new JeecgBootException("只能删除待确认的记录");
                    }
                }
            }

            overtimeRecordService.removeByIds(idList);
            result.success("删除成功！");
        }
        return result;
    }

    @ApiOperation("删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<>();
        BizOvertimeRecord data = overtimeRecordService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isPermitted("office:management")) {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                if (!data.getStaffId().equals(sysUser.getId())) {
                    throw new JeecgBootException("只能删除自己的数据");
                }
                if (data.getConfirmStatus() != 0) {
                    throw new JeecgBootException("只能删除待确认的记录");
                }
            }
            overtimeRecordService.removeById(id);
            result.success("删除成功！");
        }
        return result;
    }

    @ApiOperation("获取员工加班统计")
    @GetMapping(value = "/statistics")
    public Result<BigDecimal> statistics(
            @RequestParam(name = "staffId", required = false) String staffId,
            @RequestParam(name = "beginDate", required = false) Date beginDate,
            @RequestParam(name = "endDate", required = false) Date endDate) {

        Result<BigDecimal> result = new Result<>();
        Subject subject = SecurityUtils.getSubject();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 无权限只能查看自己的
        if (!subject.isPermitted("office:management")) {
            staffId = sysUser.getId();
        }

        if (staffId == null) {
            staffId = sysUser.getId();
        }

        BigDecimal total = overtimeRecordService.getTotalConfirmedOvertimeHours(staffId, beginDate, endDate);
        result.setSuccess(true);
        result.setResult(total);
        return result;
    }

    @ApiOperation("查询项目进度")
    @RequestMapping(value = "/queryProjectSchedule", method = RequestMethod.GET)
    public Result<List<ProjectScheduleVO>> queryProjectSchedule(
            @RequestParam(name = "projectId", required = true) String projectId) {
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
}