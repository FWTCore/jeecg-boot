package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目人工成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectCostDetail implements Serializable {

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
     * 周期
     */
    private Integer period;
    /**
     * 员工id
     */
    private String employeeId;
    /**
     * 员工名称
     */
    private String employeeName;
    /**
     * 工作时长
     */
    private BigDecimal workHours;
    /**
     * 工作天数
     */
    private BigDecimal workDays;
    /**
     * 综合补助
     */
    private BigDecimal comprehensiveSubsidy;
    /**
     * 人力成本
     */
    private BigDecimal laborCost;
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean delFlag;
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
