package com.feihu1024.mapserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feihu1024.mapserver.domain.TileServerCreateBody;
import com.feihu1024.mapserver.domain.TileServerEntity;
import com.feihu1024.mapserver.domain.TileServerUpdateBody;
import com.feihu1024.mapserver.mapper.TileServerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Service
public class TileServerServiceImpl extends ServiceImpl<TileServerMapper, TileServerEntity> implements TileServerService {

    private static final Logger log = LoggerFactory.getLogger(TileServerService.class);

    private static final byte[] EMPTY_TILE = new byte[0];

    @Override
    public List<TileServerEntity> getAllServices() {
        return this.list();
    }

    @Override
    @Cacheable(cacheNames = "cacheTiles",cacheManager = "tileCacheManager",key = "#serverName + ':' + #z + ':' + #x + ':' + #y")
    public byte[] getTileBytesForAfsim(String serverName, int z, int x, int y) {
        // log.info("缓存未命中，读取 MBTiles: {}", cacheKey);
        byte[] tileData = readTileBytesForAfsim(serverName, z, x, y);
        return tileData != null ? tileData : EMPTY_TILE;
    }

    @Override
    @Cacheable(cacheNames = "cacheTiles",cacheManager = "tileCacheManager",key = "#serverName + ':' + #z + ':' + #x + ':' + #y")
    public byte[] getTileBytes(String serverName, int z, int x, int y) {
        // log.info("缓存未命中，读取 MBTiles: {}", cacheKey);
        byte[] tileData = readTileBytes(serverName, z, x, y);
        return tileData != null ? tileData : EMPTY_TILE;
    }

    @Override
    public boolean createServer(TileServerCreateBody tileServerCreateBody){
        TileServerEntity tilEntity = new TileServerEntity();
        tilEntity.setServerName(tileServerCreateBody.getServerName());
        tilEntity.setServerTitle(tileServerCreateBody.getServerTitle());
        tilEntity.setTilePath(tileServerCreateBody.getTilePath());
        tilEntity.setTileFormat(tileServerCreateBody.getTileFormat());
        return this.save(tilEntity);
    }

    @Override
    public boolean updateServer(TileServerUpdateBody tileServerUpdateBody){
        TileServerEntity tilEntity = new TileServerEntity();
        tilEntity.setId(tileServerUpdateBody.getId());
        tilEntity.setServerName(tileServerUpdateBody.getServerName());
        tilEntity.setServerTitle(tileServerUpdateBody.getServerTitle());
        tilEntity.setTilePath(tileServerUpdateBody.getTilePath());
        tilEntity.setTileFormat(tileServerUpdateBody.getTileFormat());
        return this.updateById(tilEntity);
    }

    private byte[] readTileBytesForAfsim(String serverName, int z, int x, int y) {
        // 1. 查询服务配置
        TileServerEntity tileService = this.getOne(
                new LambdaQueryWrapper<TileServerEntity>()
                        .eq(TileServerEntity::getServerName, serverName)
        );
        if (tileService == null) {
            log.warn("服务 [{}] 不存在", serverName);
            return null;
        }

        String tilePath = tileService.getTilePath();
        if (tilePath == null || tilePath.trim().isEmpty()) {
            return null;
        }

        File mbtilesFile = new File(tilePath);
        if (!mbtilesFile.exists()) {
            return null;
        }

        String jdbcUrl = "jdbc:sqlite:" + mbtilesFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {

            // 第一步：从 tiles 表查 tile_id
            String tileId;
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "SELECT tile_id FROM map WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?")) {
                ps1.setInt(1, z);
                ps1.setInt(2, x);
                ps1.setInt(3, y);

                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (!rs1.next()) {
                        // 未找到瓦片记录
                        return null;
                    }
                    tileId = rs1.getString("tile_id");
                    if (tileId == null || tileId.isEmpty()) {
                        // tile_id 为空，说明数据异常
                        log.warn("瓦片记录存在但 tile_id 为空: {}/{}/{}/{}", serverName, z, x, y);
                        return null;
                    }
                }
            }

            // 第二步：用 tile_id 从 images 表查 tile_data
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "SELECT tile_data FROM images WHERE tile_id = ?")) {
                ps2.setString(1, tileId);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        byte[] tileData = rs2.getBytes("tile_data");
                        // log.info("成功读取瓦片: {}/{}/{}/{}", serverName, z, x, y);
                        return tileData;
                    } else {
                        log.warn("images 表中未找到 tile_id = {} 对应的 tile_data", tileId);
                        return null;
                    }
                }
            }

        } catch (Exception e) {
            log.error("读取瓦片失败 - server: {}, z: {}, x: {}, y: {}", serverName, z, x, y, e);
            return null;
        }
    }

    private byte[] readTileBytes(String serverName, int z, int x, int y) {

        // 查询服务，获取瓦片服务对象
        TileServerEntity tileService = this.getOne(new LambdaQueryWrapper<TileServerEntity>().eq(TileServerEntity::getServerName, serverName));
        if (tileService == null) {
            log.warn("服务 [{}] 不存在", serverName);
            return null;
        }

        // 检查瓦片存放路径是否为空
        String tilePath = tileService.getTilePath();
        if (tilePath == null || tilePath.trim().isEmpty()) {
            // log.debug("mbtiles 文件不存在: {}", serverName);
            return null;
        }

        File mbtilesFile = new File(tilePath, z + ".mbtiles");
        if (tilePath == null || tilePath.trim().isEmpty() || !mbtilesFile.exists()) {
            // log.debug("mbtiles 文件不存在: {}", mbtilesFile.getAbsolutePath());
            return null;
        }

        String jdbcUrl = "jdbc:sqlite:" + mbtilesFile.getAbsolutePath();
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT tile_data FROM tiles WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?"
             )) {

            ps.setInt(1, z);
            ps.setInt(2, x);
            ps.setInt(3, y);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] tileData = rs.getBytes("tile_data");
                    // log.info("成功读取瓦片: {}/{}/{}/{}", serverName, z, x, y);
                    return tileData;
                }
            }

        } catch (Exception e) {
            // log.error("读取瓦片失败 - server: {}, z: {}, x: {}, y: {}", serverName, z, x, y, e);
        }
        return null;
    }
}
