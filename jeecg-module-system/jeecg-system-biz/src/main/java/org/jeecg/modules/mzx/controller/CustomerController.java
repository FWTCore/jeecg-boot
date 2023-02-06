package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemTemplate;
import org.jeecg.modules.mzx.entity.UFTAAPartner;
import org.jeecg.modules.mzx.model.CustomerSyncModel;
import org.jeecg.modules.mzx.service.IBizCustomerService;
import org.jeecg.modules.system.entity.SysDepartRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "客户管理")
@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private IBizCustomerService customerService;


    /**
     * @param customer
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     * @功能：查询字典数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizCustomer>> queryPageList(BizCustomer customer, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizCustomer>> result = new Result<IPage<BizCustomer>>();
        QueryWrapper<BizCustomer> queryWrapper = QueryGenerator.initQueryWrapper(customer, req.getParameterMap());
        Page<BizCustomer> page = new Page<BizCustomer>(pageNo, pageSize);
        IPage<BizCustomer> pageList = customerService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @return
     * @功能：同步
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/syncuft", method = RequestMethod.PUT)
    public Result<?> syncuft(HttpServletRequest req) {
        List<UFTAAPartner> dataList = customerService.getUFTAAPartnerList(null);
        customerService.synUFTCustomer(dataList);
        return Result.ok("同步成功!");
    }

    /**
     * @return
     * @功能：新增
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizCustomer> add(@RequestBody BizCustomer customer) {
        Result<BizCustomer> result = new Result<BizCustomer>();
        try {
            customer.setCreateTime(new Date());
            customer.setDelFlag(CommonConstant.DEL_FLAG_0);
            customerService.save(customer);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param customer
     * @return
     * @功能：编辑
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizCustomer> edit(@RequestBody BizCustomer customer) {
        Result<BizCustomer> result = new Result<BizCustomer>();
        BizCustomer data = customerService.getById(customer.getId());
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            customer.setUpdateTime(new Date());
            boolean ok = customerService.updateById(customer);
            if (ok) {
                result.success("编辑成功!");
            }
        }
        return result;
    }


}
