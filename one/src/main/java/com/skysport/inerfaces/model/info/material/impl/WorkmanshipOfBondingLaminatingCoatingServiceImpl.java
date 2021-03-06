package com.skysport.inerfaces.model.info.material.impl;

import com.skysport.core.model.common.impl.CommonServiceImpl;
import com.skysport.inerfaces.bean.info.WorkmanshipOfBondingLaminatingCoatingInfo;
import com.skysport.inerfaces.mapper.info.material.WorkmanshipOfBondingLaminatingCoatingMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 类说明:
 * Created by zhangjh on 2015/6/25.
 */
@Service("workmanshipOfBondingLaminatingCoatingService")
public class WorkmanshipOfBondingLaminatingCoatingServiceImpl extends CommonServiceImpl<WorkmanshipOfBondingLaminatingCoatingInfo> implements InitializingBean {
    @Resource(name = "workmanshipOfBondingLaminatingCoatingMapper")
    private WorkmanshipOfBondingLaminatingCoatingMapper workmanshipOfBondingLaminatingCoatingMapper;

    @Override
    public void afterPropertiesSet()  {
        commonMapper = workmanshipOfBondingLaminatingCoatingMapper;
    }
}
