package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.vo.ProjectVO;

import java.util.List;

public interface IBizProjectService extends IService<BizProject> {

    /**
     * 创建项目
     * @param projectVO
     * @return
     */
    ProjectVO CreateProject(ProjectVO projectVO);

    /**
     * 更新项目已回款
     * @param ids
     */
    void updateProjectPayment(List<String> ids);

    /**
     * 更新项目结算中
     * @param ids
     */
    void updateProjectBilling(List<String> ids);
    /**
     * 更新项目已完成
     * @param ids
     */
    void updateProjectFinish(List<String> ids);

}
