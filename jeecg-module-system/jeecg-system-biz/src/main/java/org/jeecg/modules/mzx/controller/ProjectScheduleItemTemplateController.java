package org.jeecg.modules.mzx.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mchange.v2.lang.ObjectUtils;
import com.mchange.v2.lang.StringUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemTemplate;
import org.jeecg.modules.mzx.service.IBizProjectScheduleItemTemplateService;
import org.jeecg.modules.system.entity.SysDictItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api(tags = "项目进度条目模板")
@RestController
@RequestMapping("/project/schedule/item/template")
@Slf4j
public class ProjectScheduleItemTemplateController {

    @Autowired
    private IBizProjectScheduleItemTemplateService projectScheduleItemTemplateService;

    /**
     * @param projectScheduleItemTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     * @功能：查询字典数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectScheduleItemTemplate>> queryPageList(BizProjectScheduleItemTemplate projectScheduleItemTemplate, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectScheduleItemTemplate>> result = new Result<IPage<BizProjectScheduleItemTemplate>>();
        req.getParameterMap().get("column")[0]="sortOrder";
        req.getParameterMap().get("order")[0]="ASC";
        QueryWrapper<BizProjectScheduleItemTemplate> queryWrapper = QueryGenerator.initQueryWrapper(projectScheduleItemTemplate, req.getParameterMap());
        if (ObjectUtil.isNull(projectScheduleItemTemplate.getParentId())) {
            queryWrapper.isNull("parent_id");
        }
        Page<BizProjectScheduleItemTemplate> page = new Page<BizProjectScheduleItemTemplate>(pageNo, pageSize);
        IPage<BizProjectScheduleItemTemplate> pageList = projectScheduleItemTemplateService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * @return
     * @功能：新增
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizProjectScheduleItemTemplate> add(@RequestBody BizProjectScheduleItemTemplate projectScheduleItemTemplate) {
        Result<BizProjectScheduleItemTemplate> result = new Result<BizProjectScheduleItemTemplate>();
        try {
            projectScheduleItemTemplate.setCreateTime(new Date());
            projectScheduleItemTemplate.setDelFlag(CommonConstant.DEL_FLAG_0);
            projectScheduleItemTemplateService.save(projectScheduleItemTemplate);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param projectScheduleItemTemplate
     * @return
     * @功能：编辑
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizProjectScheduleItemTemplate> edit(@RequestBody BizProjectScheduleItemTemplate projectScheduleItemTemplate) {
        Result<BizProjectScheduleItemTemplate> result = new Result<BizProjectScheduleItemTemplate>();
        BizProjectScheduleItemTemplate data = projectScheduleItemTemplateService.getById(projectScheduleItemTemplate.getId());
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            data.setItemName(projectScheduleItemTemplate.getItemName());
            data.setDefaultText(projectScheduleItemTemplate.getDefaultText());
            data.setDirections(projectScheduleItemTemplate.getDirections());
            data.setSortOrder(projectScheduleItemTemplate.getSortOrder());
            data.setStatus(projectScheduleItemTemplate.getStatus());
            data.setUpdateTime(new Date());
            boolean ok = projectScheduleItemTemplateService.updateById(data);
            if (ok) {
                result.success("编辑成功!");
            }
        }
        return result;
    }

    /**
     * @param id
     * @return
     * @功能：删除字典数据
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        Result<String> result = new Result<String>();
        BizProjectScheduleItemTemplate data = projectScheduleItemTemplateService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            List<String> ids = new ArrayList<>();
            ids.add(id);
            projectScheduleItemTemplateService.deleteBatchWithChildren(ids);
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * @param ids
     * @return
     * @功能：批量删除字典数据
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<String>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            projectScheduleItemTemplateService.deleteBatchWithChildren(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

}
