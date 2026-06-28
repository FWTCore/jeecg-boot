-- =====================================================
-- 加班管理功能数据库脚本
-- 创建时间：2026-06-27
-- =====================================================

-- 1. 创建加班记录表
CREATE TABLE IF NOT EXISTS `biz_overtime_record` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `staff_id` varchar(36) NOT NULL COMMENT '员工ID',
  `staff_name` varchar(100) DEFAULT NULL COMMENT '员工姓名（冗余存储）',
  `overtime_date` date NOT NULL COMMENT '加班日期',
  `overtime_hours` decimal(4,1) NOT NULL COMMENT '加班时长（小时，最多一位小数）',
  `overtime_reason` varchar(500) DEFAULT NULL COMMENT '加班原因',
  `project_id` varchar(36) DEFAULT NULL COMMENT '关联项目ID',
  `project_name` varchar(200) DEFAULT NULL COMMENT '项目名称（冗余存储）',
  `project_schedule_usage_item_id` varchar(32) DEFAULT NULL COMMENT '项目进度进度明细id',
  `schedule_name` varchar(110) DEFAULT NULL COMMENT '条目名称',
  `service_type` tinyint DEFAULT NULL COMMENT '服务方式，字典',
  `service_content` varchar(1000) DEFAULT NULL COMMENT '服务内容',
  `confirm_status` int(11) DEFAULT 0 COMMENT '确认状态（0:待确认, 1:已确认）',
  `confirmer_id` varchar(36) DEFAULT NULL COMMENT '确认人ID',
  `confirmer_name` varchar(100) DEFAULT NULL COMMENT '确认人姓名',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除状态（0:正常, 1:已删除）',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_staff_date` (`staff_id`, `overtime_date`),
  KEY `idx_confirm_status` (`confirm_status`)
) ENGINE=InnoDB COMMENT='加班记录表';

-- 2. 创建确认状态字典
INSERT INTO sys_dict (id, dict_name, dict_code, description, del_flag, create_by, create_time, type)
SELECT REPLACE(UUID(), '-', ''), '加班确认状态', 'overtime_confirm_status', '加班确认状态', 0, 'admin', NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict WHERE dict_code = 'overtime_confirm_status');

-- 3. 创建确认状态字典项
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time)
SELECT REPLACE(UUID(), '-', ''), (SELECT id FROM sys_dict WHERE dict_code = 'overtime_confirm_status'), '待确认', '0', '', 1, 1, 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_item di
    JOIN sys_dict d ON di.dict_id = d.id
    WHERE d.dict_code = 'overtime_confirm_status' AND di.item_value = '0'
);

INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time)
SELECT REPLACE(UUID(), '-', ''), (SELECT id FROM sys_dict WHERE dict_code = 'overtime_confirm_status'), '已确认', '1', '', 2, 1, 'admin', NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_dict_item di
    JOIN sys_dict d ON di.dict_id = d.id
    WHERE d.dict_code = 'overtime_confirm_status' AND di.item_value = '1'
);
