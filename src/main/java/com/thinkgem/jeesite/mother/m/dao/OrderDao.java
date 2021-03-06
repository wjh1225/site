package com.thinkgem.jeesite.mother.m.dao;

import com.sun.tools.corba.se.idl.constExpr.Or;
import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.mother.m.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * Created by wangJH on 2017/11/3.
 */
@MyBatisDao
public interface OrderDao extends CrudDao<Order> {
    //批量生成订单
    int addList(List<Order> list);

    List<String> findOrderNumber(Map<String, Object> map);

    List<Order> findOrderListByOrderNumber(List<String> list);

    int updateOrderState(Map<String, Object> map);

    //后台发货
    int delivery(Order order);

    //退费操作
    int updateRefund(Map<String, Object> map);

    //删除
    int updateByOrderNumber(String orderNumber);
}
