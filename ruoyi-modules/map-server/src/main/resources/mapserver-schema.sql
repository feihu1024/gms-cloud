-- =============================================
-- 瓦片服务配置表创建脚本
-- 表名: tile_service_config
-- 描述: 存储瓦片服务的配置信息
-- =============================================

-- 创建瓦片服务配置表
CREATE TABLE IF NOT EXISTS map_service_config (
    -- 主键ID，自动增长
    id INTEGER PRIMARY KEY,
    -- 服务名称，用于唯一标识服务
    server_name VARCHAR(100) NOT NULL UNIQUE,
    -- 服务标题，用于显示和描述
    server_title VARCHAR(200) NOT NULL,
    -- 瓦片文件存储路径
    tile_path VARCHAR(500) NOT NULL,
    -- 瓦片格式，限制为'mbtiles'或'file'两种类型
    tile_format VARCHAR(20) NOT NULL CHECK (tile_format IN ('mbtiles', 'file')),
    -- 记录创建时间，默认为当前时间戳
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- 记录最后更新时间，默认为当前时间戳
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 创建索引以提高查询性能
-- =============================================

-- 为服务名称创建索引（唯一约束已自动创建索引，但这里显式创建以明确意图）
CREATE INDEX IF NOT EXISTS idx_map_service_server_name ON map_service_config(server_name);

-- =============================================
-- 创建触发器自动更新更新时间字段
-- =============================================

-- 创建更新时间的触发器，当记录更新时自动设置update_time为当前时间
CREATE TRIGGER IF NOT EXISTS update_map_service_timestamp
AFTER UPDATE ON map_service_config
FOR EACH ROW
BEGIN
    UPDATE map_service_config SET update_time = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;