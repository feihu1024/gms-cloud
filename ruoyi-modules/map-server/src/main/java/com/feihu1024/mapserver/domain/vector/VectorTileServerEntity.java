package com.feihu1024.mapserver.domain.vector;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "矢量瓦片地图服务实体类")
@Data
public class VectorTileServerEntity implements Serializable {
    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "服务名称", required = true)
    @TableField("server_name")
    private String serverName;

    @Schema(description = "要素数量")
    @TableField("feature_count")
    private Integer featureCount;

    @Schema(description = "服务说明")
    @TableField("server_comment")
    private String serverComment;
}
