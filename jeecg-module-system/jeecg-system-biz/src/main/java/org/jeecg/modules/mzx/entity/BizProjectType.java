package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目类型
 *
 * @author xcom
 * @date 2024/1/31
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 类型名称
     */
    private String projectTypeName;
    /**
     * 实施提成
     */
    private BigDecimal commissionRatio;
    /**
     * 生命线
     */
    private BigDecimal lifeLine;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer delFlag;
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
