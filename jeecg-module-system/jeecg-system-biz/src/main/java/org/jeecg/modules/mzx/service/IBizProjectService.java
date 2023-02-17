package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.vo.ProjectVO;

public interface IBizProjectService extends IService<BizProject> {

    ProjectVO CreateProject(ProjectVO projectVO);

}
