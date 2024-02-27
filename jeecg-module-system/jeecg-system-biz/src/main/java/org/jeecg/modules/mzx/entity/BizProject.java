package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 项目名称
     */
    private String projectName;

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
    private Date estimatedEndTime;
    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 提成比例%
     */
    private BigDecimal commissionRatio;
    /**
     * 项目概况
     */
    private String overview;
    /**
     * 项目状态
     */
    @Dict(dicCode = "project_project_status")
    private String projectStatus;

    /**
     * 综合费用
     */
    private BigDecimal comprehensiveCost;

    /**
     * 费用备注
     */
    private String comprehensiveRemark;

    /**
     * 项目类型id
     */
    private String projectTypeId;

    /**
     * 项目类型名称
     */
    private String projectTypeName;

    /**
     * 项目生命线
     */
    private BigDecimal lifeLine;
    /**
     * 销售提成比例%
     */
    private BigDecimal saleCommissionRatio;
    /**
     * 实施提成比例%
     */
    private BigDecimal implementCommissionRatio;
    /**
     * 信息来源
     */
    @Dict(dicCode = "project_project_source")
    private String source;

    /**
     * 删除状态
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;


}
