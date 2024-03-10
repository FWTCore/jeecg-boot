package org.jeecg.modules.mzx.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.*;
import org.jeecg.modules.mzx.model.BizProjectBillingVO;
import org.jeecg.modules.mzx.service.*;
import org.jeecg.modules.mzx.vo.ProjectBillingRequest;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目提成管理
 *
 * @author xcom
 * @date 2024/3/6
 */

@Api(tags = "项目提成管理")
@RestController
@RequestMapping("/project/billing")
@Slf4j
public class ProjectBillingController {


    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private IBizProjectBillingService projectBillingService;
    @Autowired
    private IBizProjectBillingDetailService bizProjectBillingDetailService;
    @Autowired
    private IBizProjectBillingCommissionService bizProjectBillingCommissionService;
    @Autowired
    private IBizProjectScheduleItemUsageService projectScheduleItemUsageService;


    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectBillingVO>> queryPageList(BizProjectBilling projectBilling, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectBillingVO>> result = new Result<IPage<BizProjectBillingVO>>();
        Page<BizProjectBilling> bizProjectBillingPage = new Page<>(pageNo, pageSize);
        IPage<BizProjectBillingVO> pageList = projectBillingService.pageProjectBilling(bizProjectBillingPage, projectBilling);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation("获取明细列表")
    @GetMapping(value = "/detailList")
    public Result<List<JSONObject>> detailList(@RequestParam("projectId") String projectId) {
        List<JSONObject> result = new ArrayList<>();
        if (StringUtils.isNotBlank(projectId)) {
            LambdaQueryWrapper<BizProjectScheduleItemUsage> bizProjectScheduleItemUsageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            bizProjectScheduleItemUsageLambdaQueryWrapper.eq(BizProjectScheduleItemUsage::getProjectId, projectId);
            List<BizProjectScheduleItemUsage> scheduleItemList = projectScheduleItemUsageService.list(bizProjectScheduleItemUsageLambdaQueryWrapper);

            LambdaQueryWrapper<BizProjectBillingDetail> bizProjectBillingLambdaQueryWrapper = new LambdaQueryWrapper<>();
            bizProjectBillingLambdaQueryWrapper.eq(BizProjectBillingDetail::getProjectId, projectId);
            bizProjectBillingLambdaQueryWrapper.eq(BizProjectBillingDetail::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<BizProjectBillingDetail> projectBillingDetailList = bizProjectBillingDetailService.list(bizProjectBillingLambdaQueryWrapper);
            if (CollectionUtil.isNotEmpty(scheduleItemList) && CollectionUtils.isNotEmpty(projectBillingDetailList)) {
                scheduleItemList.sort(Comparator.comparingInt(BizProjectScheduleItemUsage::getSortOrder));
                scheduleItemList.forEach(scheduleItem -> {
                    JSONObject json = new JSONObject();

                    List<BizProjectBillingDetail> projectBillingDetails = projectBillingDetailList.stream().filter(e -> e.getProjectScheduleUsageItemId().equals(scheduleItem.getId())).collect(Collectors.toList());

                    json.put("projectId", scheduleItem.getProjectId());
                    json.put("projectScheduleUsageItemId", scheduleItem.getId());
                    json.put("scheduleName", scheduleItem.getItemName());
                    json.put("itemCommission", scheduleItem.getCommission());

                    projectBillingDetails.sort(Comparator.comparing(BizProjectBillingDetail::getId));
                    projectBillingDetails.forEach(detail -> {
                        json.put("commission_" + detail.getStaffId(), detail.getCommission());
                        json.put("serviceFlag_" + detail.getStaffId(), detail.getServiceFlag());
                    });
                    result.add(json);
                });
            }
        }
        return Result.ok(result);
    }


    @ApiOperation("获取参与人员")
    @GetMapping(value = "/getParticipants")
    public Result<List<JSONObject>> getParticipants(@RequestParam("projectId") String projectId) {
        List<JSONObject> result = new ArrayList<>();
        if (StringUtils.isNotBlank(projectId)) {
            LambdaQueryWrapper<BizProjectBillingDetail> bizProjectBillingLambdaQueryWrapper = new LambdaQueryWrapper<>();
            bizProjectBillingLambdaQueryWrapper.eq(BizProjectBillingDetail::getProjectId, projectId);
            bizProjectBillingLambdaQueryWrapper.eq(BizProjectBillingDetail::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<BizProjectBillingDetail> projectBillingDetailList = bizProjectBillingDetailService.list(bizProjectBillingLambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(projectBillingDetailList)) {
                Map<String, List<BizProjectBillingDetail>> collect = projectBillingDetailList.stream().collect(Collectors.groupingBy(BizProjectBillingDetail::getStaffId));
                for (Map.Entry<String, List<BizProjectBillingDetail>> detail : collect.entrySet()) {
                    JSONObject json = new JSONObject();
                    json.put("staffId", detail.getKey());
                    json.put("staff", detail.getValue().get(0).getStaff());
                    result.add(json);
                }
            }
        }
        return Result.ok(result);
    }


    /**
     * @param projectBillingRequest
     * @return
     * @功能：新增
     */
    @PostMapping(value = "/save")
    public Result<Boolean> add(@RequestBody ProjectBillingRequest projectBillingRequest) {
        Result<Boolean> result = new Result<Boolean>();
        if (ObjectUtils.isEmpty(projectBillingRequest)
                || StringUtils.isBlank(projectBillingRequest.getProjectId())
                || StringUtils.isBlank(projectBillingRequest.getProjectScheduleUsageItemId())
                || StringUtils.isBlank(projectBillingRequest.getKey())
        ) {
            throw new JeecgBootException("参数错误");
        }

        LambdaQueryWrapper<BizProjectScheduleItemUsage> bizProjectScheduleItemUsageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bizProjectScheduleItemUsageLambdaQueryWrapper.eq(BizProjectScheduleItemUsage::getProjectId, projectBillingRequest.getProjectId());
        bizProjectScheduleItemUsageLambdaQueryWrapper.eq(BizProjectScheduleItemUsage::getId, projectBillingRequest.getProjectScheduleUsageItemId());
        BizProjectScheduleItemUsage projectSchedule = projectScheduleItemUsageService.getOne(bizProjectScheduleItemUsageLambdaQueryWrapper);
        if (ObjectUtils.isEmpty(projectSchedule)) {
            throw new JeecgBootException("工作进度不存在");
        }
        if (ObjectUtils.isEmpty(projectSchedule.getCommission())) {
            projectSchedule.setCommission(BigDecimal.ZERO);
        }

        if (ObjectUtils.isEmpty(projectBillingRequest.getValue())) {
            projectBillingRequest.setValue(BigDecimal.ZERO);
        }
        List<String> keys = Arrays.stream(projectBillingRequest.getKey().split("_")).collect(Collectors.toList());
        if (keys.size() != 2) {
            throw new JeecgBootException("参数错误");
        }


        LambdaQueryWrapper<BizProjectBillingDetail> bizProjectBillingDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bizProjectBillingDetailLambdaQueryWrapper.eq(BizProjectBillingDetail::getProjectId, projectBillingRequest.getProjectId());
        bizProjectBillingDetailLambdaQueryWrapper.eq(BizProjectBillingDetail::getProjectScheduleUsageItemId, projectBillingRequest.getProjectScheduleUsageItemId());
        bizProjectBillingDetailLambdaQueryWrapper.eq(BizProjectBillingDetail::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<BizProjectBillingDetail> list = bizProjectBillingDetailService.list(bizProjectBillingDetailLambdaQueryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            throw new JeecgBootException("修改工作进度数据不存在");
        }
        Optional<BizProjectBillingDetail> first = list.stream().filter(e -> e.getStaffId().equals(keys.get(1))).findFirst();
        if (!first.isPresent()) {
            throw new JeecgBootException("修改员工数据不存在");
        }
        BigDecimal total = list.stream().map(BizProjectBillingDetail::getCommission).reduce(BigDecimal.ZERO, BigDecimal::add);
        BizProjectBillingDetail bizProjectBillingDetail = first.get();
        if (ObjectUtils.isEmpty(bizProjectBillingDetail.getCommission())) {
            bizProjectBillingDetail.setCommission(BigDecimal.ZERO);
        }
        total = total.subtract(bizProjectBillingDetail.getCommission());
        total = total.add(projectBillingRequest.getValue());

        if (total.compareTo(projectSchedule.getCommission()) > 0) {
            throw new JeecgBootException(bizProjectBillingDetail.getScheduleName() + "人员设置分配比例超过了最大值[" + projectSchedule.getCommission() + "]%]");
        }
        bizProjectBillingDetail.setCommission(projectBillingRequest.getValue());
        bizProjectBillingDetailService.updateById(bizProjectBillingDetail);
        result.success("保存成功！");
        return result;
    }

    /**
     * @param id
     * @return
     */
    @ApiOperation("审批")
    @PostMapping(value = "/audit")
    public Result<String> audit(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<String>();
        BizProjectBilling data = projectBillingService.getById(id);
        if (ObjectUtils.isEmpty(data)) {
            result.error500("未找到对应实体");
        } else {
            if (!data.getBillingStatus().equals(1)) {
                result.error500("该项目已审核通过，请不要重复审核");
            }
            data.setBillingStatus(20);
            projectBillingService.updateById(data);
            // 生成项目结算提成
            bizProjectBillingCommissionService.generateProjectBillingCommission(data.getProjectId());
            result.success("审批成功!");
        }
        return result;
    }

    /**
     * @param ids
     * @return
     */
    @ApiOperation("批量提成发放")
    @RequiresPermissions("project:payment")
    @PostMapping(value = "/batchPaymentData")
    public Result<String> batchPaymentData(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (StringUtils.isBlank(ids)) {
            throw new JeecgBootException("参数错误");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        LambdaQueryWrapper<BizProjectBilling> bizProjectBillingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        bizProjectBillingLambdaQueryWrapper.in(BizProjectBilling::getId, idList);
        bizProjectBillingLambdaQueryWrapper.eq(BizProjectBilling::getDelFlag, CommonConstant.DEL_FLAG_0);
        bizProjectBillingLambdaQueryWrapper.eq(BizProjectBilling::getBillingStatus, 20);
        List<BizProjectBilling> dataList = projectBillingService.list(bizProjectBillingLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(dataList)) {
            throw new JeecgBootException("数据不存在");
        }
        if (dataList.size() != idList.size()) {
            throw new JeecgBootException("选中数据中存在状态不是待发放的项目");
        }
        List<String> projectIdList = dataList.stream().map(BizProjectBilling::getProjectId).collect(Collectors.toList());
        projectBillingService.updateProjectBillingFinish(projectIdList);
        projectService.updateProjectFinish(projectIdList);
        bizProjectBillingCommissionService.updateProjectBillingCommissionFinish(projectIdList);
        result.success("发放成功!");
        return result;
    }


}
