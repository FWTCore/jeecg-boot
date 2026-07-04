package org.jeecg.modules.mzx.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量确认加班记录结果
 */
@Data
public class BatchConfirmResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功确认的数量
     */
    private int successCount;

    /**
     * 跳过（已确认）的数量
     */
    private int skippedCount;

    /**
     * 失败的数量
     */
    private int failedCount;

    /**
     * 成功确认的ID列表
     */
    private List<String> successIds;

    /**
     * 跳过的ID列表（已确认状态）
     */
    private List<String> skippedIds;
}
