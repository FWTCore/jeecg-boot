package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.mzx.entity.BizLeaveRecord;
import org.jeecg.modules.mzx.service.IBizLeaveRecordService;
import org.jeecg.modules.mzx.vo.EmployeeLeaveStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 员工调休管理
 *
 * @author xcom
 * @date 2024/06/28
 */
@Api(tags = "员工调休管理")
@RestController
@RequestMapping("/employee/leave")
@Slf4j
public class EmployeeLeaveController {

    @Autowired
    private IBizLeaveRecordService leaveRecordService;

    @ApiOperation("获取员工调休统计列表（分页）")
    @GetMapping(value = "/statistics/list")
    public Result<IPage<EmployeeLeaveStatisticsVO>> getStatisticsList(
            @RequestParam(name = "employeeId", required = false) String employeeId,
            @RequestParam(name = "employeeName", required = false) String employeeName,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        Result<IPage<EmployeeLeaveStatisticsVO>> result = new Result<>();
        IPage<EmployeeLeaveStatisticsVO> pageList = leaveRecordService.getLeaveStatisticsPage(pageNo, pageSize, employeeId, employeeName);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation("调休使用记录列表")
    @GetMapping(value = "/list")
    public Result<IPage<BizLeaveRecord>> queryPageList(
            @RequestParam(name = "staffId", required = false) String staffId,
            @RequestParam(name = "staffName", required = false) String staffName,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        Result<IPage<BizLeaveRecord>> result = new Result<>();
        LambdaQueryWrapper<BizLeaveRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizLeaveRecord::getDelFlag, CommonConstant.DEL_FLAG_0);

        if (staffId != null && !staffId.isEmpty()) {
            queryWrapper.eq(BizLeaveRecord::getStaffId, staffId);
        }
        if (staffName != null && !staffName.isEmpty()) {
            queryWrapper.like(BizLeaveRecord::getStaffName, staffName);
        }

        queryWrapper.orderByDesc(BizLeaveRecord::getCreateTime);
        Page<BizLeaveRecord> page = new Page<>(pageNo, pageSize);
        IPage<BizLeaveRecord> pageList = leaveRecordService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation("新增调休使用记录")
    @PostMapping(value = "/add")
    public Result<BizLeaveRecord> add(@RequestBody BizLeaveRecord leaveRecord) {
        Result<BizLeaveRecord> result = new Result<>();
        try {
            leaveRecord.setCreateTime(new Date());
            leaveRecordService.addLeaveRecord(leaveRecord);
            result.success("添加成功！");
            result.setResult(leaveRecord);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    @ApiOperation("删除调休使用记录")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<>();
        BizLeaveRecord data = leaveRecordService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            leaveRecordService.removeById(id);
            result.success("删除成功！");
        }
        return result;
    }

    @ApiOperation("批量删除调休使用记录")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<>();
        if (ids == null || ids.isEmpty()) {
            result.error500("参数不识别！");
        } else {
            List<String> idList = Arrays.asList(ids.split(","));
            leaveRecordService.removeByIds(idList);
            result.success("删除成功！");
        }
        return result;
    }
}