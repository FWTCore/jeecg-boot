package org.jeecg.modules.mzx.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.mzx.entity.BizProjectScheduleUsage;
import org.jeecg.modules.mzx.service.IBizProjectScheduleUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "项目进度管理")
@RestController
@RequestMapping("/project/schedule")
@Slf4j
public class ProjectScheduleController {

    @Autowired
    private IBizProjectScheduleUsageService projectScheduleUsageService;


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



}
