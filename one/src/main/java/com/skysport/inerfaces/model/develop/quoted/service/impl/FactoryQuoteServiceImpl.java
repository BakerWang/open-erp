package com.skysport.inerfaces.model.develop.quoted.service.impl;

import com.skysport.core.model.seqno.service.IncrementNumber;
import com.skysport.inerfaces.bean.develop.BomInfo;
import com.skysport.inerfaces.bean.develop.FactoryQuoteInfo;
import com.skysport.inerfaces.constant.WebConstants;
import com.skysport.inerfaces.mapper.develop.FactoryQuotedInfoMapper;
import com.skysport.core.model.common.impl.CommonServiceImpl;
import com.skysport.inerfaces.model.develop.quoted.service.IFactoryQuoteService;
import com.skysport.inerfaces.utils.BuildSeqNoHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 说明:
 * Created by zhangjh on 2015/10/8.
 */
@Service("factoryQuoteService")
public class FactoryQuoteServiceImpl extends CommonServiceImpl<FactoryQuoteInfo> implements IFactoryQuoteService, InitializingBean {


    @Resource(name = "factoryQuotedInfoMapper")
    private FactoryQuotedInfoMapper factoryQuotedInfoMapper;

    @Resource(name = "incrementNumber")
    private IncrementNumber incrementNumber;


    @Override
    public void afterPropertiesSet() throws Exception {
        commonDao = factoryQuotedInfoMapper;
    }


    @Override
    public List<FactoryQuoteInfo> queryFactoryQuoteInfoList(String bomId) {
        return factoryQuotedInfoMapper.queryFactoryQuoteInfoList(bomId);
    }

    @Override
    public void updateBatch(List<FactoryQuoteInfo> factoryQuoteInfos, BomInfo bomInfo) {
        //找出被删除的工厂报价id，并删除
        String bomId = StringUtils.isBlank(bomInfo.getNatrualkey()) ? bomInfo.getBomId() : bomInfo.getNatrualkey();

        deleteFactoryQuoteInfoByIds(factoryQuoteInfos, bomId);

        if (null != factoryQuoteInfos) {
            //工厂报价id存在，修改；工厂报价id不存在则新增
            for (FactoryQuoteInfo factoryQuoteInfo : factoryQuoteInfos) {
                factoryQuoteInfo.setBomId(bomId);
                String quoteInfoId = factoryQuoteInfo.getFactoryQuoteId();
                //有id，更新
                if (StringUtils.isNotBlank(quoteInfoId)) {
                    factoryQuotedInfoMapper.updateInfo(factoryQuoteInfo);
                }
                //无id，新增
                else {
                    String kind_name = buildKindName(bomInfo, factoryQuoteInfo);

                    String seqNo = BuildSeqNoHelper.SINGLETONE.getFullSeqNo(kind_name, incrementNumber, WebConstants.FACTORY_QUOTE_SEQ_NO_LENGTH);
                    quoteInfoId = kind_name + seqNo;
                    factoryQuoteInfo.setFactoryQuoteId(quoteInfoId);
                    factoryQuotedInfoMapper.add(factoryQuoteInfo);
                }
            }
        }
    }


    /**
     * @param bomInfo
     * @param factoryQuoteInfo
     * @return
     */
    private String buildKindName(BomInfo bomInfo, FactoryQuoteInfo factoryQuoteInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bomInfo.getBomId());
        stringBuilder.append(WebConstants.CLOTH_MATERIAL_TYPE_ID);
        stringBuilder.append(factoryQuoteInfo.getNameNum());
        return stringBuilder.toString();
    }

    /**
     * @param factoryQuoteInfos
     * @param bomId
     */
    private void deleteFactoryQuoteInfoByIds(List<FactoryQuoteInfo> factoryQuoteInfos, String bomId) {
        List<String> allPackagingIds = factoryQuotedInfoMapper.selectAlId(bomId);
        List<String> needToSavIds = buildNeedSaveId(factoryQuoteInfos);

        allPackagingIds.removeAll(needToSavIds);

        if (null != allPackagingIds && !allPackagingIds.isEmpty()) {
            factoryQuotedInfoMapper.deleteByIds(allPackagingIds);
        }

    }

    private List<String> buildNeedSaveId(List<FactoryQuoteInfo> factoryQuoteInfos) {
        List<String> needToSavIds = new ArrayList<>();
        if (null != factoryQuoteInfos) {
            //工厂报价id存在，修改；工厂报价id不存在则新增
            for (FactoryQuoteInfo factoryQuoteInfo : factoryQuoteInfos) {
                needToSavIds.add(factoryQuoteInfo.getFactoryQuoteId());
            }
        }
        return needToSavIds;
    }
}
