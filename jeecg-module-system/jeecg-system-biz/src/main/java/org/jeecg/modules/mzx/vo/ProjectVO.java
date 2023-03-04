package org.jeecg.modules.mzx.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProjectVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目模板id
     */
    private String projectScheduleTemplateId;
    /**
     * 项目模板名称
     */
    private String scheduleTemplateName;

    /**
     * 客户Id
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 付款方式
     */
    private String paymentMethod;

    /**
     * 签单人id
     */
    private String signPersonId;
    /**
     * 签单人
     */
    private String signPerson;
    /**
     * 负责人Id
     */
    private String leaderId;
    /**
     * 负责人
     */
    private String leaderName;

    /**
     * 预计结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date estimatedEndTime;
    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /**
     * commission_ratio
     */
    private BigDecimal commissionRatio;
    /**
     * sale_commission_ratio
     */
    private BigDecimal saleCommissionRatio;
    /**
     * implement_commission_ratio
     */
    private BigDecimal implementCommissionRatio;
    /**
     * 信息来源
     */
    private BigDecimal source;
    /**
     * 项目概况
     */
    private String overview;
    /**
     * 项目状态
     */
    private String projectStatus;
    /**
     * 综合费用
     */
    private BigDecimal comprehensiveCost;
    /**
     * 综合费用备注
     */
    private String comprehensiveRemark;

}
