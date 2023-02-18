package org.jeecg.modules.mzx.vo;

import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectCostModel implements Serializable {
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
     * 花费对象
     */
    private List<CostModel> costModels;

}
