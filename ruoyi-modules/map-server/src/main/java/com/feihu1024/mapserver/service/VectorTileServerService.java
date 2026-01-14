package com.feihu1024.mapserver.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.feihu1024.mapserver.domain.vector.VectorTileEntity;
import com.feihu1024.mapserver.domain.vector.VectorTileServerEntity;
import com.feihu1024.mapserver.mapper.VectorTileServerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@DS("base_gis")
@Service
public class VectorTileServerService {

    @Autowired
    private VectorTileServerMapper vectortTileServerMapper;

    private static final Logger log = LoggerFactory.getLogger(VectorTileServerService.class);

    private static final byte[] EMPTY_TILE = new byte[0];

    public List<VectorTileServerEntity> getAllServices() {
        return vectortTileServerMapper.getAllServices();
    }

    @Cacheable(cacheNames = "cacheTiles",cacheManager = "tileCacheManager",key = "#serverName + ':' + #z + ':' + #x + ':' + #y")
    public byte[] getTileBytes(String serverName, int z, int x, int y) throws JSONException {
        // log.info("缓存未命中，读取 MBTiles: {}", cacheKey);
        String columns = vectortTileServerMapper.getColumnsByTableName(serverName);
        double tolerance = calculateTolerance(z);
        double roadLength =  getMinRoadLengthSmooth(z);
        VectorTileEntity tileData = vectortTileServerMapper.getLineVectorTile(serverName,columns,z, x, y, tolerance,roadLength);
        return tileData != null ? tileData.getTile() : EMPTY_TILE;
    }

    /**
     * 根据缩放层级计算简化容差（米）
     */
    private double calculateTolerance(int zoom) {
        if (zoom < 0 ) {
            zoom = 0;
        }
        if(zoom>18){
            zoom = 18;
        }
        // 计算：1.5 * 赤道周长 / (256 * 2^zoom)
        return 1.5 * 40075016.686 / (256 * (1L << zoom));
    }

    /**
     *
     * @param zoom
     * @return 一般情况下的道路长度参考
     */
    public static double getMinRoadLengthSmooth(int zoom) {
        if (zoom < 0 || zoom > 18) {
            throw new IllegalArgumentException("Zoom must be between 0 and 18.");
        }
        // 对数拟合：L(z) = a * exp(-b * z)
        // 经验参数拟合：z=4 → ～100km, z=12 → ～1km
        double a = 800_000.0;
        double b = 0.55;
        double length = a * Math.exp(-b * zoom);
        // 设置下限，避免过小
        return Math.max(50.0, length);
    }
}
