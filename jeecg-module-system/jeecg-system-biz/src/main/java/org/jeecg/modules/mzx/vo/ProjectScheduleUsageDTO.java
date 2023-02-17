package org.jeecg.modules.mzx.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectScheduleUsageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目Id
     */
    private String projectId;

    /**
     * 模板Id
     */
    private String projectScheduleTemplateId;

    /**
     * 用户
     */
    private String userName;

    /**
     * 应用模板Id
     */
    private String projectScheduleUsageId;


}
