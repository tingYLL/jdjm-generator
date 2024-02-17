package com.jdjm.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {
    private static volatile Meta meta;

    private MetaManager() {
        // 私有构造函数，防止外部实例化
    }

    public static Meta getMetaObject() {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        //从resources目录下读取meta.json文件
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        //转成Meta对象
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);

        //判断meta.json的输入是否正确、是否缺少输入
        MetaValidator.doValidAndFill(newMeta);
        return newMeta;
    }
}

