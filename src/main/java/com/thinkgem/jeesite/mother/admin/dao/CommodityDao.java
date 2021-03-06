package com.thinkgem.jeesite.mother.admin.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.mother.admin.entity.Commodity;

import java.util.List;
import java.util.Map;

/**
 * Created by wangJH on 2017/10/31.
 * 商品Dao
 */
@MyBatisDao
public interface CommodityDao extends CrudDao<Commodity> {
    List<Map<String,Object>> findAdvertising(Map<String, Object> map);

    List<Map<String,Object>> findPageCommodity(Map<String, Object> map);
    List<Commodity> specialtyCommodity(Map<String, Object> map);
    List<Map<String,Object>> recommended(Map<String, Object> map);

}
