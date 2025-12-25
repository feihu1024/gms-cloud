package com.feihu1024.mapserver.controller;

import com.feihu1024.mapserver.common.R;
import com.feihu1024.mapserver.domain.TileServerCreateBody;
import com.feihu1024.mapserver.domain.TileServerEntity;
import com.feihu1024.mapserver.domain.TileServerUpdateBody;
import com.feihu1024.mapserver.service.TileServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "瓦片服务", description = "提供地图瓦片、图层等核心功能")

@RestController
@RequestMapping("/tileserver")
public class TileServerController {

    @Autowired
    private TileServerService tieServerService;

    @Operation(summary = "创建一个地图服务", description = "根据指定配置创建一个瓦片服务")
    @PostMapping("createServer")
    public R<Boolean> createServer(@RequestBody TileServerCreateBody tileServerCreateBody)
    {
        Map<String,Object> data = new HashMap<String,Object>(){};
        boolean success = tieServerService.createServer(tileServerCreateBody);
        return R.ok(success);
    }

    @Operation(summary = "更新地图服务的属性", description = "")
    @PostMapping("updateServer")
    public R<Boolean> updateTile(@RequestBody TileServerUpdateBody tileServerUpdateBody) {
        boolean success = tieServerService.updateServer(tileServerUpdateBody);
        return R.ok(success);
    }

    @Operation(summary = "根据id删除指定的服务", description = "")
    @DeleteMapping("deleteServerById")
    public R<Boolean> deleteServerById(Long id) {
        boolean success = tieServerService.removeById(id);
        return R.ok(success);
    }

    @Operation(summary = "获取所有地图服务", description = "")
    @GetMapping("/getAllServices")
    public R<List<TileServerEntity>> getAllService() {
        List<TileServerEntity> mapServerList = tieServerService.getAllServices();
        return R.ok(mapServerList);
    }

    @Operation(summary = "获取指定服务的瓦片数据", description = "")
    @GetMapping("/tiles/{serverName}/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getTile(@PathVariable String serverName,@PathVariable int z,@PathVariable int x,@PathVariable int y) {

        // 安全校验（防止路径遍历）
        if (!serverName.matches("[a-zA-Z0-9_-]+")) {
            return ResponseEntity.badRequest().build();
        }

        byte[] data = tieServerService.getTileBytes(serverName, z, x, y);

        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = detectImageType(data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(data.length);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @Operation(summary = "获取指定服务的瓦片数据for_afsim", description = "")
    @GetMapping("/tiles_for_afsim/{serverName}/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getTileForAfsim(@PathVariable String serverName,@PathVariable int z,@PathVariable int x,@PathVariable int y) {

        // 安全校验（防止路径遍历）
        if (!serverName.matches("[a-zA-Z0-9_-]+")) {
            return ResponseEntity.badRequest().build();
        }

        byte[] data = tieServerService.getTileBytesForAfsim(serverName, z, x, y);

        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = detectImageType(data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(data.length);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    private MediaType detectImageType(byte[] data) {
        if (data.length < 4) return MediaType.IMAGE_PNG;

        byte[] header = Arrays.copyOfRange(data, 0, 4);
        // PNG
        if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47) {
            return MediaType.IMAGE_PNG;
        }
        // JPG
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
            return MediaType.IMAGE_JPEG;
        }
        // 默认 PNG
        return MediaType.IMAGE_PNG;
    }
}
