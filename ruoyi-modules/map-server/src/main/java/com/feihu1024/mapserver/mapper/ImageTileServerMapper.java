package com.feihu1024.mapserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feihu1024.mapserver.domain.TileServerEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageTileServerMapper extends BaseMapper<TileServerEntity> {
}
