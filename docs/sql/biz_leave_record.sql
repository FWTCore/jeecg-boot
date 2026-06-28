-- 调休使用记录表
-- 执行此SQL创建 biz_leave_record 表

CREATE TABLE IF NOT EXISTS `biz_leave_record` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `staff_id` varchar(36) NOT NULL COMMENT '员工ID',
  `staff_name` varchar(100) DEFAULT NULL COMMENT '员工姓名（冗余存储）',
  `leave_date` date NOT NULL COMMENT '调休日期',
  `leave_hours` decimal(4,1) NOT NULL COMMENT '调休时长（小时）',
  `leave_reason` varchar(500) DEFAULT NULL COMMENT '调休原因',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除状态',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_staff_date` (`staff_id`, `leave_date`)
) ENGINE=InnoDB COMMENT='调休使用记录表';


--添加菜单
INSERT INTO `xcom-system`.sys_permission (id, parent_id, name, url, component, is_route, component_name, redirect,
                                          menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive,
                                          hidden, hide_tab, description, create_by, create_time, update_by, update_time,
                                          del_flag, rule_flag, status, internal_or_external)
VALUES ('2071200698812583938', '1754793500379574273', '员工调休管理', '/mzx/employee/level', 'mzx/employeeLevel/index',
        1, 'mzx-employee-salary', null, 1, null, '0', 25, 0, 'ant-design:customer-service-twotone', 1, 1, 0, 0, null,
        '888', '2026-06-28 19:55:02', '888', '2026-06-28 19:55:34', 0, 0, null, 0);

