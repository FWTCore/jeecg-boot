package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.mzx.entity.BizProjectType;
import org.jeecg.modules.mzx.service.IBizProjectTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 项目类型
 *
 * @author xcom
 * @date 2024/1/31
 */

@Api(tags = "项目类型")
@RestController
@RequestMapping("/project/type")
@Slf4j
public class ProjectTypeController {

    @Autowired
    private IBizProjectTypeService projectTypeService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/queryall", method = RequestMethod.GET)
    public Result<List<BizProjectType>> queryall(@RequestParam(name = "id", required = false) String id, HttpServletRequest req) {
        Result<List<BizProjectType>> result = new Result<List<BizProjectType>>();
        LambdaQueryWrapper<BizProjectType> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BizProjectType::getDelFlag, CommonConstant.DEL_FLAG_0);
        if (StringUtils.isNotBlank(id)) {
            lambdaQueryWrapper.eq(BizProjectType::getId, id);
        }
        List<BizProjectType> list = projectTypeService.list(lambdaQueryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }
}
