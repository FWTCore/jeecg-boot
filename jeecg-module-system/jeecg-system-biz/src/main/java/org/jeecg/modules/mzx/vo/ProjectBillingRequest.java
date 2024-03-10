package org.jeecg.modules.mzx.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TODO
 *
 * @author xcom
 * @date 2024/3/10
 */

@Data
public class ProjectBillingRequest  implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 项目id
     */
    private String projectId;
    /**
     * 阶段id
     */
    private String projectScheduleUsageItemId;

    /**
     * 修改提成比例key
     */
    private String key;

    /**
     * 修改提成比例值
     */
    private BigDecimal value;

}
