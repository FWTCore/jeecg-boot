package org.jeecg.modules.mzx.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CustomerSyncModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

}
