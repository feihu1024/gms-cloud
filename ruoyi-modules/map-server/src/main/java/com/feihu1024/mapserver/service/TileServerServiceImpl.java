package com.feihu1024.mapserver.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feihu1024.mapserver.domain.TileServerEntity;
import com.feihu1024.mapserver.mapper.TileServerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;

@Service
public class TileServerServiceImpl extends ServiceImpl<TileServerMapper, TileServerEntity> implements TileServerService {

    private static final Logger log = LoggerFactory.getLogger(TileServerService.class);

    private final RedisTemplate<String, byte[]> redisTemplate;

    public TileServerServiceImpl(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<TileServerEntity> getAllServices() {
        return this.list();
    }

    @Override
    public byte[] getTileBytes(String serverName, int z, int x, int y) {
        String cacheKey = "tile-server:" + serverName + ":" + z + ":" + x + ":" + y;

        byte[] cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            if (cached.length == 0) {
                // log.info("缓存命中（空值）: {}", cacheKey);
                return null; // 表示瓦片不存在
            }
            // log.info("缓存命中: {}", cacheKey);
            return cached;
        }

        // log.info("缓存未命中，读取 MBTiles: {}", cacheKey);
        byte[] tileData = readTileBytes(serverName, z, x, y);

        // 4. 写入缓存
        if (tileData != null) {
            // 存在：缓存 7 天
            redisTemplate.opsForValue().set(cacheKey, tileData, Duration.ofSeconds(60));
        } else {
            // 不存在：缓存空值 5 分钟，防止缓存穿透
            redisTemplate.opsForValue().set(cacheKey, new byte[0], Duration.ofSeconds(60));
        }

        return tileData;
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
