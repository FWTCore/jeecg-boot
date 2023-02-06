package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 项目进度模板
 * </p>
 *
 * @Author
 * @since
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectScheduleItemTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 项目进度模板id
     */
    private String projectScheduleTemplateId;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 条目名称
     */
    @Excel(name = "条目名称", width = 50)
    private String itemName;

    /**
     * 默认文本，内容为数组方式
     */
    @Excel(name = "选项值", width = 255)
    private String defaultText;

    /**
     * 说明
     */
    @Excel(name = "说明", width = 200)
    private String directions;

    /**
     * 排序
     */
    @Excel(name = "排序", width = 15,type=4)
    private Integer sortOrder;

    /**
     * 状态（1启用 0不启用）
     */
    @Dict(dicCode = "dict_item_status")
    private Integer status;


    /**
     * 删除状态
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
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
}
