package org.jeecg.modules.mzx.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目提成
 *
 * @author xcom
 * @date 2024/3/6
 */

@Data
public class BizProjectBillingVO  implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 参与人id
     */
    private String participantsId;
    /**
     * 参与人
     */
    private String participants;

}
