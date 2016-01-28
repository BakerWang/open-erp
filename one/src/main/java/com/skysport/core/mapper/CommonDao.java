package com.skysport.core.mapper;

import com.skysport.core.bean.query.DataTablesInfo;
import com.skysport.core.bean.system.SelectItem2;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhangjh on 2015/6/8.
 */
public interface CommonDao<T> {

    int listInfosCounts();

    int listFilteredInfosCounts(DataTablesInfo dataTablesInfo);

    List<T> searchInfos(DataTablesInfo dataTablesInfo);

    void updateInfo(T t);

    <T> T queryInfo(String natrualKey);

    void add(T t);

    void del(String natrualKey);

    String queryCurrentSeqNo();

    List<SelectItem2> querySelectList(@Param(value = "name") String name);

    void addBatch(List<T> infos);

    void updateBatch(List<T> infos);

    List<SelectItem2> querySelectListByParentId(@Param(value = "parentId")String parentId);
}
