package org.jeecg.modules.mzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.mzx.entity.BizCustomer;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.entity.BizProjectScheduleTemplate;
import org.jeecg.modules.mzx.service.IBizCustomerService;
import org.jeecg.modules.mzx.service.IBizProjectScheduleTemplateService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.jeecg.modules.mzx.vo.ProjectScheduleVO;
import org.jeecg.modules.mzx.vo.ProjectVO;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api(tags = "项目管理")
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {
    @Autowired
    private IBizProjectService projectService;
    @Autowired
    private IBizCustomerService customerService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private IBizProjectScheduleTemplateService projectScheduleTemplateService;

    @ApiOperation("获取列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<BizProject>> queryPageList(BizProject project, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<BizProject>> result = new Result<IPage<BizProject>>();
        QueryWrapper<BizProject> queryWrapper = QueryGenerator.initQueryWrapper(project, req.getParameterMap());
        Page<BizProject> page = new Page<BizProject>(pageNo, pageSize);
        IPage<BizProject> pageList = projectService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @param projectVO
     * @return
     * @功能：新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<ProjectVO> add(@RequestBody ProjectVO projectVO) {
        Result<ProjectVO> result = new Result<ProjectVO>();
        try {
            BizProjectScheduleTemplate template = projectScheduleTemplateService.getById(projectVO.getProjectScheduleTemplateId());
            if (template == null || template.getDelFlag().equals(CommonConstant.DEL_FLAG_1) || template.getStatus().equals("0")) {
                throw new JeecgBootException("项目进度模板不存在");
            }
            BizCustomer customerEntity = customerService.getById(projectVO.getCustomerId());
            if (customerEntity == null || customerEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("客户不存在");
            }
            projectVO.setCustomerName(customerEntity.getCustomerName());
            SysUser sysUserEntity = sysUserService.getById(projectVO.getSignPersonId());
            if (sysUserEntity == null || sysUserEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("签单人不存在");
            }
            projectVO.setSignPerson(sysUserEntity.getRealname());
            sysUserEntity = sysUserService.getById(projectVO.getLeaderId());
            if (sysUserEntity == null || sysUserEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("负责人不存在");
            }
            projectVO.setLeaderName(sysUserEntity.getRealname());
            projectService.CreateProject(projectVO);
            result.success("保存成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @param projectVO
     * @return
     * @功能：编辑
     */
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<ProjectVO> edit(@RequestBody ProjectVO projectVO) {
        Result<ProjectVO> result = new Result<ProjectVO>();
        BizProject data = projectService.getById(projectVO.getId());
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
//            BizCustomer customerEntity = customerService.getById(projectVO.getCustomerId());
//            if (customerEntity == null || customerEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
//                throw new JeecgBootException("客户不存在");
//            }
//            data.setCustomerId(projectVO.getCustomerId());
//            data.setCustomerId(customerEntity.getCustomerName());

            SysUser sysUserEntity = sysUserService.getById(projectVO.getSignPersonId());
            if (sysUserEntity == null || sysUserEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                throw new JeecgBootException("签单人不存在");
            }
            data.setSignPersonId(projectVO.getSignPersonId());
            data.setSignPerson(sysUserEntity.getRealname());
            sysUserEntity = sysUserService.getById(projectVO.getLeaderId());
            if (sysUserEntity == null || sysUserEntity.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                 throw new JeecgBootException("负责人不存在");
            }
            data.setLeaderId(projectVO.getLeaderId());
            data.setLeaderName(sysUserEntity.getRealname());
            data.setContractAmount(projectVO.getContractAmount());
            data.setPaymentMethod(projectVO.getPaymentMethod());
            data.setEstimatedEndTime(projectVO.getEstimatedEndTime());
            data.setEndTime(projectVO.getEndTime());
//            data.setCommissionRatio(projectVO.getCommissionRatio());
            data.setOverview(projectVO.getOverview());
            data.setComprehensiveRemark(projectVO.getComprehensiveRemark());
            data.setComprehensiveCost(projectVO.getComprehensiveCost());
            data.setImplementCommissionRatio(projectVO.getImplementCommissionRatio());
            data.setSaleCommissionRatio(projectVO.getSaleCommissionRatio());
            data.setSource(projectVO.getSource());
            data.setUpdateTime(new Date());
            boolean ok = projectService.updateById(data);
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
            projectService.removeByIds(Arrays.asList(ids.split(",")));
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
        BizProject data = projectService.getById(id);
        if (data == null) {
            result.error500("未找到对应实体");
        } else {
            projectService.removeById(id);
            result.success("删除成功!");
        }
        return result;
    }

    @RequestMapping(value = "/queryall", method = RequestMethod.GET)
    public Result<List<BizProject>> queryall() {
        Result<List<BizProject>> result = new Result<>();
        QueryWrapper<BizProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", CommonConstant.DEL_FLAG_0.toString());
        List<BizProject> list = projectService.list(queryWrapper);
        if (list == null || list.size() <= 0) {
            result.error500("未找到项目信息");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }




}
