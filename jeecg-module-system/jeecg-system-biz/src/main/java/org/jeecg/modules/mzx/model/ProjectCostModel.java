package org.jeecg.modules.mzx.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 项目人力累计成本
 *
 * @author xcom
 * @date 2024/2/28
 */

@Data
public class ProjectCostModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 员工Id
     */
    private String projectId;

    /**
     * 累计成本
     */
    private BigDecimal totalCost;

}
