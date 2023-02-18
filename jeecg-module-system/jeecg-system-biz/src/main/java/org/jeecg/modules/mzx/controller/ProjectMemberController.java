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
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.BizProjectMember;
import org.jeecg.modules.mzx.service.IBizProjectMemberService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

@Api(tags = "项目成员")
@RestController
@RequestMapping("/project/member")
@Slf4j
public class ProjectMemberController {

    @Autowired
    private IBizProjectMemberService projectMemberService;
    @Autowired
    private ISysUserService sysUserService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProjectMember>> queryPageList(BizProjectMember projectMember, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProjectMember>> result = new Result<IPage<BizProjectMember>>();
        QueryWrapper<BizProjectMember> queryWrapper = QueryGenerator.initQueryWrapper(projectMember, req.getParameterMap());
        Page<BizProjectMember> page = new Page<BizProjectMember>(pageNo, pageSize);
        IPage<BizProjectMember> pageList = projectMemberService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param projectMember
     * @return
     * @功能：新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<BizProjectMember> add(@RequestBody BizProjectMember projectMember) {
        Result<BizProjectMember> result = new Result<BizProjectMember>();
        try {
            SysUser sysUserEntity = sysUserService.getById(projectMember.getStaffId());
            if (sysUserEntity == null || sysUserEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                result.error500("员工不存在");
            }
            projectMember.setStaff(sysUserEntity.getRealname());
            projectMember.setCreateTime(new Date());
            projectMember.setDelFlag(CommonConstant.DEL_FLAG_0);
            projectMemberService.save(projectMember);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param projectMember
     * @return
     * @功能：编辑
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<BizProjectMember> edit(@RequestBody BizProjectMember projectMember) {
        Result<BizProjectMember> result = new Result<BizProjectMember>();
        BizProjectMember data = projectMemberService.getById(projectMember.getId());
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            data.setUpdateTime(new Date());
            boolean ok = projectMemberService.updateById(projectMember);
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
            projectMemberService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizProjectMember data = projectMemberService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            projectMemberService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }


}
