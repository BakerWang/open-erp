package com.skysport.inerfaces.mapper.info;

import com.skysport.inerfaces.bean.info.FactoryInfo;
import com.skysport.core.mapper.CommonDao;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Created by zhangjh on 2015/6/9.
 */
@Repository("factoryManageMapper")
public interface FactoryManageMapper extends CommonDao<FactoryInfo> {
}
