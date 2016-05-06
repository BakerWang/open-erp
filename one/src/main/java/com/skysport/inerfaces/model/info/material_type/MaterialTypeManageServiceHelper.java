package com.skysport.inerfaces.model.info.material_type;

import com.skysport.core.bean.system.SelectItem2;
import com.skysport.core.bean.SpringContextHolder;
import com.skysport.core.cache.SystemBaseInfoCachedMap;
import com.skysport.inerfaces.constant.WebConstants;
import com.skysport.inerfaces.model.info.material_type.impl.MaterialTypeManageServiceImpl;

import java.util.List;

/**
 * 类说明:
 * Created by zhangjh on 2015/7/7.
 */
public enum MaterialTypeManageServiceHelper {
    SINGLETONE;

    public void refreshSelect() {
        //材料类别
        MaterialTypeManageServiceImpl materialTypeManageService = SpringContextHolder.getBean("materialTypeManageService");
        List<SelectItem2> materialTypeItems = materialTypeManageService.querySelectList(null);
        SystemBaseInfoCachedMap.SINGLETONE.pushBom(WebConstants.MATERIALTYPEITEMS, materialTypeItems);
    }










}
