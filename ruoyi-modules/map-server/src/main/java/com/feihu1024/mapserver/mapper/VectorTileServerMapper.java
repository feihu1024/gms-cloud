package com.feihu1024.mapserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feihu1024.mapserver.domain.vector.VectorTileEntity;
import com.feihu1024.mapserver.domain.vector.VectorTileServerEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VectorTileServerMapper extends BaseMapper<VectorTileServerEntity> {

    // 查询指定表的字段（忽略geom列）
    @Select("select array_to_string(array_agg(column_name),',') as column_string from information_schema.columns where table_name = #{tableName} and column_name !='geom'")
    String getColumnsByTableName(String tableName);

    @Select("SELECT\n" +
            "    c.oid AS id,\n" +
            "    c.relname AS server_name,\n" +
            "    c.reltuples AS feature_count,\n" +
            "    obj_description(c.oid) AS server_comment\n" +
            "FROM\n" +
            "    pg_class c\n" +
            "    JOIN pg_namespace n ON n.oid = c.relnamespace\n" +
            "WHERE\n" +
            "    c.relkind = 'r'\n" +
            "    AND n.nspname NOT IN ('pg_catalog', 'information_schema')\n" +
            "    AND n.nspname = 'public';")
    List<VectorTileServerEntity> getAllServices();

    /**
     * 查询线图层 MVT 瓦片（使用标准 PostGIS 函数）
     *
     * @param tableName         物理表名（必须由 Service 层白名单校验！）
     * @param fields            要返回的字段列表（如 "name, class"，已校验）
     * @param z                 缩放层级
     * @param x                 瓦片 X
     * @param y                 瓦片 Y
     * @param toleranceInMeters 简化容差（单位：米）
     * @param lineLength
     * @return MVT 字节数组（未压缩）
     */
    @Select({
            "SELECT ST_AsMVT(tile_agg, #{tableName}, 4096, 'geom') AS tile",
            "FROM (",
            "  SELECT",
            "    ${fields},",
            "    ST_AsMVTGeom(",
            "      ST_Transform(geom, 3857),",
            "      ST_TileEnvelope(#{z}, #{x}, #{y}), 4096, 64, true) AS geom",
            "  FROM ${tableName}",
            "  WHERE geom && ST_Transform(ST_TileEnvelope(#{z}, #{x}, #{y}), 4326) AND NOT ST_IsEmpty(geom) AND ST_Length(ST_Transform(geom, 3857)) >=#{lineLength} LIMIT 50000",
            " ) AS tile_agg WHERE geom IS NOT NULL;"
    })
    VectorTileEntity getLineVectorTile(
            @Param("tableName") String tableName,
            @Param("fields") String fields,
            @Param("z") int z,
            @Param("x") int x,
            @Param("y") int y,
            @Param("toleranceInMeters") double toleranceInMeters,
            @Param("lineLength") double lineLength
    );
}
