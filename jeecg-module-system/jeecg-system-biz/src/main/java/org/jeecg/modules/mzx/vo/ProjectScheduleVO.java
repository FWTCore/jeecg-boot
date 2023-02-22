package org.jeecg.modules.mzx.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectScheduleVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 阶段id
     */
    private String id;
    /**
     * 阶段名称
     */
    private String scheduleName;
    /**
     * 选项值
     */
    private String defaultText;
    /**
     * 阶段说明
     */
    private String directions;
    /**
     * 排序
     */
    private Integer sortOrder;


}
