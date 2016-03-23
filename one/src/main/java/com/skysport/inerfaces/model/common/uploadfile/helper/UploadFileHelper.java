package com.skysport.inerfaces.model.common.uploadfile.helper;

import com.skysport.core.bean.permission.UserInfo;
import com.skysport.inerfaces.bean.basic.InitialPreviewConfig;
import com.skysport.inerfaces.bean.basic.InitialPreviewExtra;
import com.skysport.inerfaces.bean.common.UploadFileInfo;
import com.skysport.inerfaces.constant.WebConstants;
import com.skysport.inerfaces.model.common.uploadfile.IUploadFileInfoService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明:
 * Created by zhangjh on 2016/2/17.
 */
public enum UploadFileHelper {
    SINGLETONE;


    /**
     * 生成文件上传空间的初试话信息
     *
     * @param resultMap       Map<String, Object>
     * @param uploadFileInfos List<UploadFileInfo>
     */
    public void buildInitialPreviewByFileRecords(Map<String, Object> resultMap, List<UploadFileInfo> uploadFileInfos) {

        List<String> fileUrls = new ArrayList<>();
        List<InitialPreviewConfig> configs = new ArrayList<>();


        if (null != uploadFileInfos && !uploadFileInfos.isEmpty()) {

            for (UploadFileInfo uploadFileInfo : uploadFileInfos) {

                InitialPreviewConfig config = new InitialPreviewConfig();
                String fileUrl = uploadFileInfo.getFileUrl();
                String fileName = uploadFileInfo.getFileName();
                String delUrl = uploadFileInfo.getDelUrl();
                String key = uploadFileInfo.getUid();
                String bussId = uploadFileInfo.getBussId();
                String pathType = uploadFileInfo.getPathType();
                if (pathType.equals(WebConstants.FILE_TXT)) {
                    String txt = "<div class='file-preview-text' title='NOTES.txt'>" +
                            fileUrl +
                            "<span class='wrap-indicator' onclick='$(\"#show-detailed-text\").modal(\"show\")' title='NOTES.txt'>[…]</span>" +
                            "</div>";
                    fileUrls.add(txt);
                } else if (pathType.equals(WebConstants.FILE_IMG)) {
                    fileUrls.add("<img src='" + fileUrl + "' class='file-preview-image' alt='Desert' title='Desert'>");
                } else {
                    String other = "<div class='file-preview-text'>" +
                            "<h2><i class='glyphicon glyphicon-file'></i></h2>" +
                            fileUrl + "</div>";
                    fileUrls.add(other);
                }
                config.setCaption(fileName);
                config.setWidth("120px");
                config.setUrl(delUrl);
                config.setKey(key);
                InitialPreviewExtra extra = new InitialPreviewExtra();
                extra.setId(key);
                extra.setUrl(fileUrl);
                extra.setBussId(bussId);
                config.setExtra(extra);
                configs.add(config);
            }
        }

        resultMap.put("initialPreview", fileUrls);
        resultMap.put("initialPreviewConfig", configs);
    }

    /**
     * 组装,回写文件记录的状态信息
     *
     * @param fileInfos
     * @param bussId
     * @param fileKind
     * @param operator
     */
    public void updateFileStatus(List<UploadFileInfo> fileInfos, String bussId, String fileKind, String operator, String status) {
        for (UploadFileInfo uploadFileInfo : fileInfos) {
            uploadFileInfo.setBussId(bussId);
            uploadFileInfo.setType(fileKind);
            uploadFileInfo.setOperator(operator);
            uploadFileInfo.setStatus(status);
        }
    }


    public void updateFileRecords(List<UploadFileInfo> fileInfos, String bussId, IUploadFileInfoService uploadFileInfoService, String fileKind) {
        if (null != fileInfos && !fileInfos.isEmpty()) {

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            UserInfo user = (UserInfo) session.getAttribute(WebConstants.CURRENT_USER);
            updateFileStatus(fileInfos, bussId, fileKind, user.getAliases(), WebConstants.FILE_IN_FINISH);
            //回写文件记录表的status状态为1
            uploadFileInfoService.updateBatch(fileInfos);
        }
    }

    public Map<String, Object> getFileInfoMap(IUploadFileInfoService uploadFileInfoService, String natrualKey) {
        List<UploadFileInfo> fileInfos = uploadFileInfoService.queryListByBussId(natrualKey, WebConstants.FILE_IN_FINISH);
        Map<String, Object> fileinfosMap = new HashMap<>();
        buildInitialPreviewByFileRecords(fileinfosMap, fileInfos);
        return fileinfosMap;
    }
}
