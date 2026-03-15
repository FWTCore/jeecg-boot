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
 * 日志提醒
 *
 * @author xcom
 * @date 2024/12/21
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizLogReminder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 员工Id
     */
    private String staffId;
    /**
     * 员工
     */
    private String staff;
    /**
     * 本人禁用
     */
    private Short myselfDisable;
    /**
     * 管理员禁用
     */
    private Short managerDisable;
    /**
     * 提醒周期
     */
    private Integer reminderPeriod;
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean delFlag;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;


}
