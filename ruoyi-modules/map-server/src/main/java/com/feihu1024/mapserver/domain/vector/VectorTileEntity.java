package com.feihu1024.mapserver.domain.vector;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@NoArgsConstructor// 创建无参的构造方法
@AllArgsConstructor// 创建满参的构造方法
@Accessors(chain = true)// 使用链式方法
@Data// 重写toString方法等方法

public class VectorTileEntity implements Serializable {
    private Integer id;

    @TableField("tile")
    private byte[] tile;
}
