package org.jeecg.modules.mzx.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 加班记录查询对象
 */
@Data
public class OvertimeRecordQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工姓名（模糊查询）
     */
    private String staffName;

    /**
     * 加班日期-开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date overtimeDateBegin;

    /**
     * 加班日期-结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date overtimeDateEnd;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 项目名称（模糊查询）
     */
    private String projectName;

    /**
     * 确认状态
     */
    private Integer confirmStatus;

    /**
     * 服务方式
     */
    private Integer serviceType;

    /**
     * 服务内容（模糊查询）
     */
    private String serviceContent;

    /**
     * 加班原因（模糊查询）
     */
    private String overtimeReason;
}