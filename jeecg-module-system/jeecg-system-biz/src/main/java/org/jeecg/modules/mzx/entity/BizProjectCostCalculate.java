package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

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
    @Excel(name = "项目名称", width = 70)
    private String projectName;
    /**
     * 项目金额
     */
    @Excel(name = "项目金额", width = 15)
    private BigDecimal projectAmount;
    /**
     * 综合费用
     */
    @Excel(name = "综合费用", width = 15)
    private BigDecimal comprehensiveCost;
    /**
     * 项目成本
     */
    @Excel(name = "项目成本", width = 15)
    private BigDecimal projectCost;
    /**
     * 销售提成
     */
    @Excel(name = "销售提成", width = 15)
    private BigDecimal saleCommission;
    /**
     * 实施提成
     */
    @Excel(name = "实施提成", width = 15)
    private BigDecimal implementCommission;
    /**
     * 成本率
     */
    @Excel(name = "成本率", width = 15)
    private BigDecimal costRatio;
    /**
     * 是否超生命线
     */
    @Excel(name = "是否超生命线（1：是；0：否）", width = 30)
    private Integer superLifeline;
    /**
     * 项目状态
     */
    @Excel(name = "项目状态", width = 15, dicCode = "project_project_status")
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
