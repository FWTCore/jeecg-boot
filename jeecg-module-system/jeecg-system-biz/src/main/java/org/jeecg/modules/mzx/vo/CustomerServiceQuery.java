package org.jeecg.modules.mzx.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class CustomerServiceQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 员工
     */
    private String staff;

    /**
     * 服务内容
     */
    private String serviceContent;

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

}
