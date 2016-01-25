package com.skysport.inerfaces.model.info.material.impl;

import com.skysport.inerfaces.bean.info.WorkmanshipOfBondingLaminatingCoatingInfo;
import com.skysport.inerfaces.mapper.info.material.WorkmanshipOfBondingLaminatingCoatingDao;
import com.skysport.core.model.common.impl.CommonServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 类说明:
 * Created by zhangjh on 2015/6/25.
 */
@Component("workmanshipOfBondingLaminatingCoatingService")
public class WorkmanshipOfBondingLaminatingCoatingServiceImpl extends CommonServiceImpl<WorkmanshipOfBondingLaminatingCoatingInfo> implements InitializingBean {
    @Resource(name = "workmanshipOfBondingLaminatingCoatingDao")
    private WorkmanshipOfBondingLaminatingCoatingDao workmanshipOfBondingLaminatingCoatingDao;

    @Override
    public void afterPropertiesSet()  {
        commonDao = workmanshipOfBondingLaminatingCoatingDao;
    }
}
