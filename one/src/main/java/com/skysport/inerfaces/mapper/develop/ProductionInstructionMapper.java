package com.skysport.inerfaces.mapper.develop;

import com.skysport.core.mapper.CommonMapper;
import com.skysport.inerfaces.bean.develop.KfProductionInstructionEntity;
import org.springframework.stereotype.Repository;

/**
 * 说明:
 * Created by zhangjh on 2016/4/18.
 */
@Repository
public interface ProductionInstructionMapper extends CommonMapper<KfProductionInstructionEntity> {

    KfProductionInstructionEntity queryProductionInstractionInfo(String bomId);


    KfProductionInstructionEntity queryProjectAndBomInfoByBomId(String bomId);

    void updateProductionInstruction(KfProductionInstructionEntity productionInstruction);

    void addProductionInstruction(KfProductionInstructionEntity productionInstruction);
}
