-- =====================================================
-- 加班记录新增企业微信打卡一致性字段
-- 创建时间：2026-07-04
-- 说明：新增 wework_clock_match_flag 字段，记录加班申请是否与企业微信打卡时间一致
-- =====================================================

-- 1. 新增字段（允许 NULL，以便历史数据处理）
ALTER TABLE biz_overtime_record
ADD COLUMN wework_clock_match_flag TINYINT(1) COMMENT '是否与企业微信打卡时间一致，0=否，1=是';

-- 2. 历史数据设置默认值（设置为 0=否）
UPDATE biz_overtime_record
SET wework_clock_match_flag = 0
WHERE wework_clock_match_flag IS NULL;

-- 3. 修改为 NOT NULL（新数据必填）
ALTER TABLE biz_overtime_record
MODIFY COLUMN wework_clock_match_flag TINYINT(1) NOT NULL COMMENT '是否与企业微信打卡时间一致，0=否，1=是';

-- =====================================================
-- 注意事项：
-- 1. 历史数据默认设置为 0（否），可由管理员后续核实更新
-- 2. 新增/编辑时该字段为必填项
-- 3. 前端使用字典 dicCode="yn" 进行展示
-- =====================================================
