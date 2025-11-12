package com.feihu1024.mapserver.controller;

import com.feihu1024.mapserver.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Tag(name = "瓦片服务", description = "提供地图瓦片、图层等核心功能")

@RestController
@RequestMapping("/tile")
public class TileController {

    @Operation(summary = "创建一个瓦片地图服务", description = "根据指定配置创建一个瓦片服务")
    @GetMapping("createServer")
    public R<Map<String,Object>> createServer(String filepath)
    {
        Map<String,Object> data = new HashMap<String,Object>(){};
        data.put("filepath",filepath);
        return R.ok(data);
    }
}
