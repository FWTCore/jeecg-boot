package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizProjectScheduleTemplate;
import org.jeecg.modules.mzx.service.IBizProjectScheduleTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 模板表 前端控制器
 * </p>
 *
 * @Author
 * @since
 */

@Api(tags = "项目进度模板")
@RestController
@RequestMapping("/project/schedule/template")
@Slf4j
public class ProjectScheduleTemplateController {

    @Autowired
    private IBizProjectScheduleTemplateService projectScheduleTemplateService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectScheduleTemplate>> queryPageList(BizProjectScheduleTemplate bizProjectScheduleTemplate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectScheduleTemplate>> result = new Result<IPage<BizProjectScheduleTemplate>>();
        QueryWrapper<BizProjectScheduleTemplate> queryWrapper = QueryGenerator.initQueryWrapper(bizProjectScheduleTemplate, req.getParameterMap());
        Page<BizProjectScheduleTemplate> page = new Page<BizProjectScheduleTemplate>(pageNo, pageSize);
        IPage<BizProjectScheduleTemplate> pageList = projectScheduleTemplateService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param projectScheduleTemplate
     * @return
     * @功能：新增
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizProjectScheduleTemplate> add(@RequestBody BizProjectScheduleTemplate projectScheduleTemplate) {
        Result<BizProjectScheduleTemplate> result = new Result<BizProjectScheduleTemplate>();
        try {
            projectScheduleTemplate.setCreateTime(new Date());
            projectScheduleTemplate.setDelFlag(CommonConstant.DEL_FLAG_0);
            projectScheduleTemplateService.save(projectScheduleTemplate);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param projectScheduleTemplate
     * @return
     * @功能：编辑
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizProjectScheduleTemplate> edit(@RequestBody BizProjectScheduleTemplate projectScheduleTemplate) {
        Result<BizProjectScheduleTemplate> result = new Result<BizProjectScheduleTemplate>();
        BizProjectScheduleTemplate data = projectScheduleTemplateService.getById(projectScheduleTemplate.getId());
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            data.setUpdateTime(new Date());
            boolean ok = projectScheduleTemplateService.updateById(projectScheduleTemplate);
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
//    @CacheEvict(value= {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
    public Result<BizProjectScheduleTemplate> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<BizProjectScheduleTemplate> result = new Result<BizProjectScheduleTemplate>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        } else {
            projectScheduleTemplateService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * @param id
     * @return
     * @功能：删除
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
//    @CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
    public Result<BizProjectScheduleTemplate> delete(@RequestParam(name = "id", required = true) String id) {
        Result<BizProjectScheduleTemplate> result = new Result<BizProjectScheduleTemplate>();
        BizProjectScheduleTemplate data = projectScheduleTemplateService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            List<String> ids = new ArrayList<>();
            ids.add(id);
            projectScheduleTemplateService.deleteBatchWithChildren(ids);
            result.success("删除成功!");
        }
        return result;
    }


    @RequestMapping(value = "/queryall", method = RequestMethod.GET)
    public Result<List<BizProjectScheduleTemplate>> queryall() {
        Result<List<BizProjectScheduleTemplate>> result = new Result<>();
        QueryWrapper<BizProjectScheduleTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0.toString());
        queryWrapper.eq("status", "1");
        List<BizProjectScheduleTemplate> list = projectScheduleTemplateService.list(queryWrapper);
        if (list == null || list.size() <= 0) {
            result.error500("未找到模板信息");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }


}
