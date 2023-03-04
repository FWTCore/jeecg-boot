package org.jeecg.modules.mzx.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.BizCustomerServiceLog;
import org.jeecg.modules.mzx.entity.BizProjectScheduleLog;
import org.jeecg.modules.mzx.service.IBizCustomerService;
import org.jeecg.modules.mzx.service.IBizCustomerServiceLogService;
import org.jeecg.modules.mzx.vo.CustomerServiceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Api(tags = "客户服务")
@RestController
@RequestMapping("/customer/service")
@Slf4j
public class CustomerServiceController {

    @Autowired
    private IBizCustomerService customerService;

    @Autowired
    private IBizCustomerServiceLogService serviceLogService;


    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizCustomerServiceLog>> queryPageList(CustomerServiceQuery serviceLog, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizCustomerServiceLog>> result = new Result<IPage<BizCustomerServiceLog>>();
        LambdaQueryWrapper<BizCustomerServiceLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizCustomerServiceLog::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (ObjectUtil.isNotNull(serviceLog.getCustomerName())) {
            queryWrapper.like(BizCustomerServiceLog::getCustomerName, serviceLog.getCustomerName());
        }
        if (ObjectUtil.isNotNull(serviceLog.getStaff())) {
            queryWrapper.like(BizCustomerServiceLog::getStaff, serviceLog.getStaff());
        }
        if (ObjectUtil.isNotNull(serviceLog.getServiceContent())) {
            queryWrapper.like(BizCustomerServiceLog::getServiceContent, serviceLog.getServiceContent());
        }
        if (ObjectUtil.isNotNull(serviceLog.getBeginDate())) {
            queryWrapper.ge(BizCustomerServiceLog::getCreateTime, serviceLog.getBeginDate());
        }
        if (ObjectUtil.isNotNull(serviceLog.getEndDate())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(serviceLog.getEndDate());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.lt(BizCustomerServiceLog::getCreateTime, cal.getTime());
        }
        queryWrapper.orderByDesc(BizCustomerServiceLog::getCreateTime);
        Page<BizCustomerServiceLog> page = new Page<BizCustomerServiceLog>(pageNo, pageSize);
        IPage<BizCustomerServiceLog> pageList = serviceLogService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * @return
     * @功能：
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizCustomerServiceLog> add(@RequestBody BizCustomerServiceLog serviceLog) {
        Result<BizCustomerServiceLog> result = new Result<BizCustomerServiceLog>();
        try {
            BizCustomer customer = customerService.getById(serviceLog.getCustomerId());
            if (customer == null || customer.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                result.error500("未找到对应实体");
            } else {
                serviceLog.setCustomerName(customer.getCustomerName());
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                serviceLog.setStaffId(sysUser.getId());
                serviceLog.setStaff(sysUser.getRealname());
                serviceLog.setCreateTime(new Date());
                serviceLog.setDelFlag(CommonConstant.DEL_FLAG_0);
                serviceLogService.save(serviceLog);
                result.success("保存成功！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param serviceLog
     * @return
     * @功能：编辑
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizCustomerServiceLog> edit(@RequestBody BizCustomerServiceLog serviceLog) {
        Result<BizCustomerServiceLog> result = new Result<BizCustomerServiceLog>();
        BizCustomerServiceLog data = serviceLogService.getById(serviceLog.getId());
        if (data == null || data.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
            result.error500("未找到对应实体");
        } else {
            data.setServiceContent(serviceLog.getServiceContent());
            data.setWorkHours(serviceLog.getWorkHours());
            data.setNextPlanContent(serviceLog.getNextPlanContent());
            data.setNextPlanTime(serviceLog.getNextPlanTime());
            data.setUpdateTime(new Date());
            boolean ok = serviceLogService.updateById(data);
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
            serviceLogService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizCustomerServiceLog data = serviceLogService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            serviceLogService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }


}
