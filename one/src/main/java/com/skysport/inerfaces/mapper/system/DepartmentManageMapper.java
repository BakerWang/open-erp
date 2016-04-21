package com.skysport.inerfaces.mapper.system;

import com.skysport.core.mapper.CommonDao;
import com.skysport.inerfaces.bean.system.DepartmentInfo;
import org.springframework.stereotype.Repository;

/**
 * 说明:
 * Created by zhangjh on 2015/12/30.
 */
@Repository("departmentManageMapper")
public interface DepartmentManageMapper extends CommonDao<DepartmentInfo> {

}
