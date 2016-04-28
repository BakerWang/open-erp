package com.skysport.inerfaces.model.info.material.impl;

import com.skysport.inerfaces.bean.develop.MaterialInfo;
import com.skysport.inerfaces.mapper.info.MaterialMapper;
import com.skysport.inerfaces.model.info.material.IMaterialManageService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类说明:
 * Created by zhangjh on 2015/7/13.
 */
@Service("materialManageService")
public class MaterialManageServiceImpl implements IMaterialManageService {

    @Resource(name = "materialMapper")
    private MaterialMapper materialMapper;

    @Override
    public MaterialInfo saveFabricFun(MaterialInfo info) {
        return materialMapper.saveFabricFun(info);
    }

    @Override
    public void addBatch(List<MaterialInfo> fabricItems) {
        materialMapper.addBatch(fabricItems);
    }

    @Override
    public List<MaterialInfo> queryFabricByBomId(String bomId) {
        return null;
    }


}
