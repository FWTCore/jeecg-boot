package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
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
     * 综合薪资
     */
    private BigDecimal comprehensivePayroll;
    /**
     * 周期
     */
    private Integer period;
    /**
     * 周期
     */
    @DateTimeFormat(pattern = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @TableField(exist = false)
    private Date periodDate;
    /**
     * 工资状态
     */
    private Short payrollStatus;

    /**
     * 工资状态 文本
     */
    @TableField(exist = false)
    private String payrollStatusDesc;

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


    public String getPayrollStatusDesc() {
        if (ObjectUtils.isEmpty(this.payrollStatus)) {
            return "";
        } else if (this.payrollStatus.intValue() == 1) {
            return "待生效";
        } else if (this.payrollStatus.intValue() == 2) {
            return "已生效";
        }
        return "";
    }

    public Date getPeriodDate() {
        if (ObjectUtils.isEmpty(this.period)) {
            return this.periodDate;
        } else {
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.YEAR, this.period / 100);
            instance.set(Calendar.MONTH, (this.period % 100) - 1);
            instance.set(Calendar.DAY_OF_MONTH, 1);
            return instance.getTime();
        }
    }

    public Integer generationPeriod() {
        if (ObjectUtils.isEmpty(this.periodDate)) {
            return null;
        } else {
            Calendar instance = Calendar.getInstance();
            instance.setTime(this.periodDate);
            return instance.get(Calendar.YEAR) * 100 + instance.get(Calendar.MONTH) + 1;
        }
    }

}
