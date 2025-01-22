package com.jdjm.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * 文件过滤配置，比如文件名（range） 包含（rule) Post（value）
 */
@Data
@Builder
public class FileFilterConfig {
    //过滤范围
    private String range;

    //过滤规则
    private String rule;

    //过滤值
    private String value;
}
