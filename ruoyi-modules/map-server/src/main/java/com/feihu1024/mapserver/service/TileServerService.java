package com.feihu1024.mapserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feihu1024.mapserver.domain.TileServerCreateBody;
import com.feihu1024.mapserver.domain.TileServerEntity;
import com.feihu1024.mapserver.domain.TileServerUpdateBody;

import java.util.List;

public interface TileServerService extends IService<TileServerEntity> {
    List<TileServerEntity> getAllServices();
    byte[] getTileBytes(String serverName, int z, int x, int y);
    byte[] getTileBytesForAfsim(String serverName, int z, int x, int y);
    boolean updateServer(TileServerUpdateBody tileServerUpdateBody);
    boolean createServer(TileServerCreateBody tileServerCreateBody);
}
