-- =====================================================
-- 客户服务日志与日常服务日志工时单位改造 - 数据库迁移脚本
-- 执行日期：2026-07-04
-- 说明：新增 work_hours_hour 字段，迁移历史数据
-- =====================================================

-- 1. 客户服务日志表新增工时(小时)字段
ALTER TABLE biz_customer_service_log
ADD COLUMN work_hours_hour DECIMAL(10,2) DEFAULT NULL COMMENT '工时(小时)' AFTER work_hours;

ALTER TABLE biz_customer_service_log
    MODIFY COLUMN work_hours DECIMAL(10,3) COMMENT '工时(天) - 用于成本核算';

-- 2. 日常服务日志表新增工时(小时)字段
ALTER TABLE biz_work_log
ADD COLUMN work_hours_hour DECIMAL(10,2) DEFAULT NULL COMMENT '工时(小时)' AFTER work_hours;

ALTER TABLE biz_work_log
    MODIFY COLUMN work_hours DECIMAL(10,3) COMMENT '工时(天) - 用于成本核算';

-- 3. 迁移客户服务日志历史数据：天转小时
UPDATE biz_customer_service_log
SET work_hours_hour = work_hours * 8
WHERE work_hours_hour IS NULL AND work_hours IS NOT NULL;

-- 4. 迁移日常服务日志历史数据：天转小时
UPDATE biz_work_log
SET work_hours_hour = work_hours * 8
WHERE work_hours_hour IS NULL AND work_hours IS NOT NULL;

-- =====================================================
-- 验证脚本（可选执行）
-- =====================================================

-- 验证客户服务日志数据迁移结果
-- SELECT id, work_hours, work_hours_hour FROM biz_customer_service_log WHERE work_hours IS NOT NULL LIMIT 10;

-- 验证日常服务日志数据迁移结果
-- SELECT id, work_hours, work_hours_hour FROM biz_work_log WHERE work_hours IS NOT NULL LIMIT 10;

-- 统计迁移记录数
-- SELECT 'biz_customer_service_log' AS table_name, COUNT(*) AS migrated_count FROM biz_customer_service_log WHERE work_hours_hour IS NOT NULL
-- UNION ALL
-- SELECT 'biz_work_log' AS table_name, COUNT(*) AS migrated_count FROM biz_work_log WHERE work_hours_hour IS NOT NULL;
