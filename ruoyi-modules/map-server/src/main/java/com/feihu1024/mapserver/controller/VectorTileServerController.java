package com.feihu1024.mapserver.controller;

import com.feihu1024.mapserver.common.R;
import com.feihu1024.mapserver.domain.vector.VectorTileServerEntity;
import com.feihu1024.mapserver.service.VectorTileServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@Tag(name = "矢量瓦片服务", description = "提供地图瓦片、图层等核心功能")
@CrossOrigin
@RestController
@RequestMapping("/vectorTileServer")
public class VectorTileServerController {

    @Autowired
    private VectorTileServerService vectorTileServerService;

    @Operation(summary = "获取所有地图服务", description = "")
    @GetMapping("/getAllVectorServices")
    public R<List<VectorTileServerEntity>> getAllService() {
        List<VectorTileServerEntity> mapServerList = vectorTileServerService.getAllServices();
        return R.ok(mapServerList);
    }

    @Operation(summary = "获取指定服务的瓦片数据", description = "")
    @GetMapping("/vectorTiles/{serverName}/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getTile(@PathVariable String serverName,@PathVariable int z,@PathVariable int x,@PathVariable("y") String y) throws Exception {

        // 安全校验（防止路径遍历）
        if (!serverName.matches("[a-zA-Z0-9_-]+")) {
            return ResponseEntity.badRequest().build();
        }

        Integer tileY = Integer.valueOf(y.replaceAll("[^0-9]", ""));
        byte[] data = null;

        data = vectorTileServerService.getTileBytes(serverName, z, x, tileY);
        // try {
        //     data = vectorTileServerService.getTileBytes(serverName, z, x, tileY);
        // } catch (Exception e) {
        //     return ResponseEntity.internalServerError().build();
        // }

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
