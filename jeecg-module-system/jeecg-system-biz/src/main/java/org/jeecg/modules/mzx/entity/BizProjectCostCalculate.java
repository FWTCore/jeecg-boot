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

/**
 * 项目成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectCostCalculate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 项目id
     */
    private String projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目金额
     */
    private BigDecimal projectAmount;
    /**
     * 综合费用
     */
    private BigDecimal comprehensiveCost;
    /**
     * 项目成本
     */
    private BigDecimal projectCost;
    /**
     * 销售提成
     */
    private BigDecimal saleCommission;
    /**
     * 实施提成
     */
    private BigDecimal implementCommission;
    /**
     * 成本率
     */
    private BigDecimal costRatio;
    /**
     * 是否超生命线
     */
    private Integer superLifeline;
    /**
     * 项目状态
     */
    @Dict(dicCode = "project_project_status")
    private String projectStatus;
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
    private Date createdTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;

}
