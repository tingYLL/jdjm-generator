package com.jdjm.maker.generator;

import com.jdjm.maker.meta.Meta;
import com.jdjm.maker.meta.MetaManager;

public class MainGenerator {
    public static void main(String[] args) {
        Meta metaObject = MetaManager.getMetaObject();
        System.out.println(metaObject);
    }
}
