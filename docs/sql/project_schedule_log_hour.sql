-- =====================================================
-- 项目服务日志工时单位改造数据库脚本
-- 创建时间：2026-06-29
-- 说明：新增 work_hours_hour 字段（小时），修改 work_hours 精度为3位小数
-- =====================================================

-- 1. 修改 work_hours 字段精度为3位小数（确保小时转天不丢失精度）
ALTER TABLE biz_project_schedule_log
MODIFY COLUMN work_hours DECIMAL(10,3) COMMENT '工时(天) - 用于成本核算';

-- 2. 新增 work_hours_hour 字段（单位：小时）
ALTER TABLE biz_project_schedule_log
ADD COLUMN work_hours_hour DECIMAL(6,1) COMMENT '工时(小时) - 用于录入和展示';

-- 3. 历史数据刷数：根据 workHours（天）反向计算 workHoursHour（小时）
-- 说明：历史数据原来以天为单位录入，乘以8转换为小时
UPDATE biz_project_schedule_log
SET work_hours_hour = work_hours * 8
WHERE work_hours IS NOT NULL AND work_hours_hour IS NULL;

-- =====================================================
-- 注意事项：
-- 1. 精度扩展不改变原有值，例如原来存 1.0，修改后仍是 1.000
-- 2. 历史数据刷数后，前端展示为小时仅供参考
-- 3. 新录入数据优先使用 work_hours_hour 字段
-- =====================================================