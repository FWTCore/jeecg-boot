package org.jeecg.modules.mzx.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 员工个人工资
 *
 * @author xcom
 * @date 2024/3/10
 */

@Data
public class OwnPayrollVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工id
     */
    private String employeeId;
    /**
     * 员工名称
     */
    private String employeeName;

    /**
     * 共计收入
     */
    private BigDecimal totalIncome;

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
     * 项目提成
     */
    private BigDecimal implementCommission;
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

    public BigDecimal getTotalIncome() {
        BigDecimal result = BigDecimal.ZERO;
        if (ObjectUtils.isNotEmpty(this.salary)) {
            result = result.add(this.salary);
        }
        if (ObjectUtils.isNotEmpty(this.socialInsurance)) {
            result = result.add(this.socialInsurance);
        }
        if (ObjectUtils.isNotEmpty(this.accumulationFund)) {
            result = result.add(this.accumulationFund);
        }
        if (ObjectUtils.isNotEmpty(this.projectSubsidy)) {
            result = result.add(this.projectSubsidy);
        }
        if (ObjectUtils.isNotEmpty(this.trafficSubsidy)) {
            result = result.add(this.trafficSubsidy);
        }
        if (ObjectUtils.isNotEmpty(this.accommodationSubsidy)) {
            result = result.add(this.accommodationSubsidy);
        }
        if (ObjectUtils.isNotEmpty(this.diningSubsidy)) {
            result = result.add(this.diningSubsidy);
        }
        if (ObjectUtils.isNotEmpty(this.otherSubsidy)) {
            result = result.add(this.otherSubsidy);
        }
        if (ObjectUtils.isNotEmpty(this.implementCommission)) {
            result = result.add(this.implementCommission);
        }
        return result;
    }
}
