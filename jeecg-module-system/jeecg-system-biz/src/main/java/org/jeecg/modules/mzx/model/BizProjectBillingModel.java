package org.jeecg.modules.mzx.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 项目结算模型
 *
 * @author xcom
 * @date 2024/3/4
 */

@Data
public class BizProjectBillingModel implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
