package org.jeecg.modules.mzx.vo;

import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CostModel implements Serializable {
    private static final long serialVersionUID = 1L;


    private String id;

    /**
     * 费用key
     */
    @Dict(dicCode = "project_cost_key")
    private String costKey;

    /**
     * 费用值
     */
    private BigDecimal costValue;
    /**
     * 费用备注
     */
    private String costRemark;
}
