package org.jeecg.modules.mzx.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 员工项目费用
 *
 * @author xcom
 * @date 2024/2/8
 */

@Data
public class EmployeeProjectCostModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工Id
     */
    private String staffId;
    /**
     * 费用key，字典
     */
    private String costKey;

    /**
     * 费用值
     */
    private BigDecimal costValue;

}
