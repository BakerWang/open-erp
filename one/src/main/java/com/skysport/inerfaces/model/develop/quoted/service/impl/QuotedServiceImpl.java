package com.skysport.inerfaces.model.develop.quoted.service.impl;

import com.skysport.core.cache.DictionaryInfoCachedMap;
import com.skysport.core.constant.CharConstant;
import com.skysport.core.model.common.impl.CommonServiceImpl;
import com.skysport.core.model.workflow.IWorkFlowService;
import com.skysport.core.utils.DateUtils;
import com.skysport.core.utils.ExcelCreateUtils;
import com.skysport.core.utils.UpDownUtils;
import com.skysport.inerfaces.bean.develop.BomInfo;
import com.skysport.inerfaces.bean.develop.QuotedInfo;
import com.skysport.inerfaces.bean.relation.ProjectPojectItemBomSpVo;
import com.skysport.inerfaces.constant.WebConstants;
import com.skysport.inerfaces.mapper.develop.QuotedInfoMapper;
import com.skysport.inerfaces.mapper.info.BomInfoMapper;
import com.skysport.inerfaces.model.develop.bom.IBomService;
import com.skysport.inerfaces.model.develop.quoted.service.IQuotedService;
import com.skysport.inerfaces.model.permission.userinfo.service.IStaffService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 说明:
 * Created by zhangjh on 2015/9/17.
 */
@Service("quotedService")
public class QuotedServiceImpl extends CommonServiceImpl<QuotedInfo> implements IQuotedService, InitializingBean {

    @Autowired
    private QuotedInfoMapper quotedInfoMapper;

    @Autowired
    private BomInfoMapper bomInfoMapper;


    @Resource(name = "bomManageService")
    private IBomService bomManageService;

    @Autowired
    private IStaffService developStaffImpl;

    @Autowired
    private IWorkFlowService quoteInfoTaskImpl;

    @Override
    public void afterPropertiesSet() throws Exception {
        commonMapper = quotedInfoMapper;
    }

    @Override
    public void edit(QuotedInfo info) {
        //计算报价:欧元价+包装费+C% .Remark:150527 2015年度新开发项目 - 价格分析.xlsx
        //%C= (欧元价+包装费) * 0.05 （0.05是佣金）
        //所以报价=(欧元价+包装费) * 1.05
        Float commissionValue = Float.parseFloat(DictionaryInfoCachedMap.SINGLETONE.getDictionaryValue(WebConstants.FINANCE_CONFIG, WebConstants.COMMISSION_RATE, String.valueOf(WebConstants.COMMISSION_RATE_DEFAULTVALUE)));
        BigDecimal commission, quotedPrice;
        if (null != info.getEuroPrice() && null != info.getLpPrice()) {
            DecimalFormat df = new DecimalFormat("0.0000");
            commission = (info.getEuroPrice().add(info.getLpPrice())).multiply(new BigDecimal(commissionValue));
            quotedPrice = info.getEuroPrice().add(info.getLpPrice()).add(commission);
            info.setCommission(new BigDecimal(df.format(commission)));
            info.setQuotedPrice(new BigDecimal(df.format(quotedPrice)));
        }

        quotedInfoMapper.updateInfo(info);
    }

    /**
     * 更新或新增报价信息
     *
     * @param quotedInfo QuotedInfo
     */
    @Override
    public QuotedInfo updateOrAdd(QuotedInfo quotedInfo) {
        //查询BOM是否有对应的报价表
        QuotedInfo quotedInfoInDB = queryInfoByNatrualKey(quotedInfo.getBomId());
        //查询项目和子项目id
        ProjectPojectItemBomSpVo projectPojectItemBomSpVo = bomInfoMapper.queryIds(quotedInfo.getBomId(), quotedInfo.getSpId());
        quotedInfo.setProjectId(projectPojectItemBomSpVo.getProjectId());
        quotedInfo.setProjectItemId(projectPojectItemBomSpVo.getProjectItemId());
        quotedInfo.setProjectName(projectPojectItemBomSpVo.getProjectName());
        quotedInfo.setProjectItemName(projectPojectItemBomSpVo.getProjectItemName());
        quotedInfo.setBomName(projectPojectItemBomSpVo.getBomName());
        quotedInfo.setSpName(projectPojectItemBomSpVo.getSpName());
        if (null == quotedInfoInDB) {
            quotedInfoMapper.add(quotedInfo);
        } else {
            quotedInfoMapper.updateInfo(quotedInfo);
        }
        return quotedInfo;
    }

    /**
     * 下载文件
     *
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param natrualkeys String
     * @throws IOException
     */
    @Override
    public void downloadOffer(HttpServletRequest request, HttpServletResponse response, String natrualkeys) throws
            IOException, InvalidFormatException {

        String year = DateUtils.SINGLETONE.getYyyy();
        List<String> itemIds = Arrays.asList(natrualkeys.split(CharConstant.COMMA));
        List<QuotedInfo> quotedInfos = quotedInfoMapper.queryListByProjectItemIds(itemIds, WebConstants.QUOTED_STEP_PRE);

        Set<String> seriesNameSet = new HashSet<String>();
        Set<String> bomNameSet = new HashSet<>();


        //所有bomid
        List<BomInfo> bomInfos = bomManageService.queryBomInfosByProjectItemIds(itemIds);
        getSeriesAnBomName(seriesNameSet, bomNameSet, bomInfos);


        String fileName = getBomQuoteName(seriesNameSet, bomNameSet);

        String ctxPath = new StringBuilder().append(DictionaryInfoCachedMap.SINGLETONE.getDictionaryValue(WebConstants.FILE_PATH, WebConstants.BASE_PATH)).append(WebConstants.FILE_SEPRITER).append(year).append(WebConstants.FILE_SEPRITER)
                .append(DictionaryInfoCachedMap.SINGLETONE.getDictionaryValue(WebConstants.FILE_PATH, WebConstants.DEVELOP_PATH)).toString();

        String resourcePath = WebConstants.RESOURCE_PATH_QUOTE;

        //创建文件
        String downLoadPath = ExcelCreateUtils.getInstance().create(quotedInfos, fileName, ctxPath, resourcePath);

        //下载
        UpDownUtils.download(request, response, fileName, downLoadPath);

    }

    /**
     * @param seriesNameSet
     * @param bomNameSet
     * @return
     */
    public String getBomQuoteName(Set<String> seriesNameSet, Set<String> bomNameSet) {
        StringBuilder bomQuoteName = new StringBuilder();
        bomQuoteName.append(DateUtils.SINGLETONE.getYyyyMmdd());
        bomQuoteName.append(CharConstant.HORIZONTAL_LINE).append(WebConstants.BOM_QUOTE_CN_NAME);
        bomQuoteName.append(StringUtils.join(seriesNameSet.toArray(), ""));
        bomQuoteName.append(StringUtils.join(bomNameSet.toArray(), ""));
        bomQuoteName.append(WebConstants.SUFFIX_EXCEL_XLSX);
        return bomQuoteName.toString();
    }

    /**
     * @param seriesNameSet
     * @param bomNameSet
     * @param bomInfos
     * @return
     */
    public void getSeriesAnBomName(Set<String> seriesNameSet, Set<String> bomNameSet, List<BomInfo> bomInfos) {
        if (!bomInfos.isEmpty()) {
            for (BomInfo bomInfo : bomInfos) {
//                String bomId = bomInfo.getNatrualkey();
                String seriesName = bomInfo.getSeriesName();
                seriesNameSet.add(CharConstant.HORIZONTAL_LINE + seriesName.replace("\\", "").replace("/", ""));//去重复
                String bomName = bomInfo.getName();
                if (bomNameSet.isEmpty()) {
                    bomNameSet.add(bomName);
                } else if (bomNameSet.size() < 3) {
                    bomNameSet.add(WebConstants.AND + bomName);
                }
            }
        }

    }


}