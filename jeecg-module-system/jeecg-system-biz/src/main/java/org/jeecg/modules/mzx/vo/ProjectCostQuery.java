package org.jeecg.modules.mzx.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectCostQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 服务人
     */
    private String staff;


}
