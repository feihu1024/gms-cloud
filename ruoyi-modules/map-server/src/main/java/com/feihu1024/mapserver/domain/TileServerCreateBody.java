package com.feihu1024.mapserver.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "地图服务实体类")
@Data
public class TileServerCreateBody implements Serializable {

    @Schema(description = "服务名称", required = true)
    private String serverName;

    @Schema(description = "服务标题")
    private String serverTitle;


    @Schema(description = "服务所在路径", required = true)
    private String tilePath;

    @Schema(description = "服务类型（file/mbtiles）",required = true,defaultValue = "mbtiles")
    private String tileFormat;
}
