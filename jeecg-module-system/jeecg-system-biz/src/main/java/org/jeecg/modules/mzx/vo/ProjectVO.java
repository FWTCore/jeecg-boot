package org.jeecg.modules.mzx.vo;

import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

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
    private Integer paymentMethod;

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
    private Date estimatedEndTime;
    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * commission_ratio
     */
    private BigDecimal commissionRatio;
    /**
     * 项目概况
     */
    private String overview;
    /**
     * 项目状态
     */
    private String projectStatus;

}
