package com.feihu1024.mapserver.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "地图服务实体类")
@Data
@TableName("map_service_config")
public class TileServerEntity implements Serializable {

    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "服务名称", required = true)
    @TableField("server_name")
    private String serverName;

    @Schema(description = "服务标题")
    @TableField("server_title")
    private String serverTitle;


    @Schema(description = "服务所在路径", required = true)
    @TableField("tile_path")
    private String tilePath;

    @Schema(description = "服务类型（file/mbtiles）",required = true,defaultValue = "mbtiles")
    @TableField("tile_format")
    private String tileFormat;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
