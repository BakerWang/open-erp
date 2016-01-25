package com.skysport.inerfaces.model.info.material.impl.helper;

import com.skysport.core.bean.system.SelectItem2;
import com.skysport.core.init.SpringContextHolder;
import com.skysport.core.instance.SystemBaseInfo;
import com.skysport.inerfaces.model.info.material.impl.FinishServiceImpl;

import java.util.List;

/**
 * 类说明:
 * Created by zhangjh on 2015/7/7.
 */
public enum FinishServiceHelper {
    SINGLETONE;

    public void refreshSelect() {
        FinishServiceImpl finishService = SpringContextHolder.getBean("finishService");
        List<SelectItem2> finishItems = finishService.querySelectList(null);
        SystemBaseInfo.SINGLETONE.pushBom("finishItems", finishItems);
    }
}
