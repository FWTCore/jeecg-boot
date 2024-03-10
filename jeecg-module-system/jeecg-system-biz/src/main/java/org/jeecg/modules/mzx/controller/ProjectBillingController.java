package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectBilling;
import org.jeecg.modules.mzx.model.BizProjectBillingVO;
import org.jeecg.modules.mzx.service.IBizProjectBillingService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
    private IBizProjectBillingService projectBillingService;


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





}
