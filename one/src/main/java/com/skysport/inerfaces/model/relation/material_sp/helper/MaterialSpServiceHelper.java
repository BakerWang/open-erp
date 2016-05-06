package com.skysport.inerfaces.model.relation.material_sp.helper;

import com.skysport.inerfaces.bean.develop.MaterialSpInfo;

import java.math.BigDecimal;

/**
 * 说明:
 * Created by zhangjh on 2016-05-06.
 */
public enum MaterialSpServiceHelper {
    SINGLETONE;

    public void caculateCosting(BigDecimal bigDecimal, MaterialSpInfo materialSpInfo) {
        BigDecimal colorPrice = materialSpInfo.getColorPrice();
        if (null != colorPrice) {
            bigDecimal.add(colorPrice);
        }
    }
}
