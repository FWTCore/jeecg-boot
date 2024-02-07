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
 * 员工工资表
 *
 * @author xcom
 * @date 2024/2/6
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizEmployeePayroll implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 员工id
     */
    private String employeeId;
    /**
     * 员工名称
     */
    private String employeeName;
    /**
     * 基本工资
     */
    private BigDecimal salary;
    /**
     * 社保
     */
    private BigDecimal socialInsurance;
    /**
     * 公积金
     */
    private BigDecimal accumulationFund;
    /**
     * 项目补助收集
     */
    private BigDecimal collectProjectSubsidy;
    /**
     * 项目补助
     */
    private BigDecimal projectSubsidy;
    /**
     * 项目补助备注
     */
    private String projectSubsidyRemark;
    /**
     * 交通补助收集
     */
    private BigDecimal collectTrafficSubsidy;
    /**
     * 交通补助
     */
    private BigDecimal trafficSubsidy;
    /**
     * 交通补助备注
     */
    private String trafficSubsidyRemark;
    /**
     * 住宿补助收集
     */
    private BigDecimal collectAccommodationSubsidy;
    /**
     * 住宿补助
     */
    private BigDecimal accommodationSubsidy;
    /**
     * 住宿补助备注
     */
    private String accommodationSubsidyRemark;
    /**
     * 用餐补助收集
     */
    private BigDecimal collectDiningSubsidy;
    /**
     * 用餐补助
     */
    private BigDecimal diningSubsidy;
    /**
     * 用餐补助备注
     */
    private String diningSubsidyRemark;
    /**
     * 其他补助收集
     */
    private BigDecimal collectOtherSubsidy;
    /**
     * 其他补助
     */
    private BigDecimal otherSubsidy;
    /**
     * 其他补助备注
     */
    private String otherSubsidyRemark;
    /**
     * 周期
     */
    private Integer period;
    /**
     * 工资状态
     */
    private Short payrollStatus;
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
