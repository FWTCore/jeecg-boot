package org.jeecg.modules.mzx.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProjectScheduleQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目名称
     */
    private String projectId;

    /**
     * 条目名称
     */
    private String scheduleName;

    /**
     * 服务人
     */
    private String staff;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date beginDate;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    /**
     * 服务方式
     */
    private Integer serviceType;

    /**
     * 服务内容
     */
    private String serviceContent;

    /**
     * 是否加班
     */
    private Integer overtimeFlag;

    /**
     * 是否完成
     */
    private Integer doneFlag;

    /**
     * 是否归档
     */
    private Integer archiveFlag;


}
