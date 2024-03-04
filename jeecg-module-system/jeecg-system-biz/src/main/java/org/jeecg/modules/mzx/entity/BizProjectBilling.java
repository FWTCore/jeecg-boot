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
 * 项目结算
 *
 * @author xcom
 * @date 2024/3/4
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectBilling implements Serializable {

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
     * 实施提成
     */
    private BigDecimal implementCommission;
    /**
     * 负责id
     */
    private String leaderId;
    /**
     * 负责人
     */
    private String leaderName;
    /**
     * 结算状态
     */
    @Dict(dicCode = "project_billing_status")
    private Integer billingStatus;
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
