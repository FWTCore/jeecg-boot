package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目变更明细
 *
 * @author xcom
 * @date 2024/8/5
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectChangeDetail  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 周期
     */
    private Integer triggerPeriod;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean delFlag;


}
