package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectBilling;
import org.jeecg.modules.mzx.model.BizProjectBillingModel;
import org.jeecg.modules.mzx.model.BizProjectBillingVO;

import java.util.List;

/**
 * 项目结算
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface BizProjectBillingMapper extends BaseMapper<BizProjectBilling> {


    /**
     * 获取需要结算的项目数据
     *
     * @return
     */
    List<BizProjectBillingModel> listBizProjectBillingModel();

    /**
     * 批量插入项目结算
     *
     * @param dataList
     */
    void batchInsertBizProjectBilling(@Param("dataList") List<BizProjectBillingModel> dataList);

    /**
     * 分页获取项目提成
     *
     * @param page
     * @param query
     * @return
     */
    IPage<BizProjectBillingVO> pageProjectBilling(Page<BizProjectBilling> page, @Param("query") BizProjectBilling query);
}
