package com.thinkgem.jeesite.mother.m;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.mother.admin.entity.Commodity;
import com.thinkgem.jeesite.mother.admin.service.CommodityService;
import com.thinkgem.jeesite.mother.m.entity.Muser;
import com.thinkgem.jeesite.mother.m.entity.Order;
import com.thinkgem.jeesite.mother.m.entity.ReceiptAddress;
import com.thinkgem.jeesite.mother.m.entity.ShoppingCat;
import com.thinkgem.jeesite.mother.m.service.AddressService;
import com.thinkgem.jeesite.mother.m.service.MuserService;
import com.thinkgem.jeesite.mother.m.service.OrderService;
import com.thinkgem.jeesite.mother.m.service.ShoppingCatService;
import com.thinkgem.jeesite.mother.m.weixin.*;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;

/**
 * 商城列表
 */
@Controller
@RequestMapping(value = "${frontPath}/m")
public class IndexController {
    //商品
    @Resource
    private CommodityService commodityService;
    //购物车
    @Resource
    private ShoppingCatService shoppingCatService;
    //订单
    @Resource
    private OrderService orderService;
    //手机用户
    @Resource
    private MuserService mUserSerivce;
    @Resource
    private AddressService addressService;

    //手机端商城首页
    @RequestMapping
    public String appIndex(Commodity commodity, Model model, @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                           @RequestParam(required = false, defaultValue = "10") Integer pageSize,HttpServletRequest request) {
        Page<Commodity> page = new Page<Commodity>(pageNo, pageSize);//分页查询
        String openid = (String) request.getSession().getAttribute("openid");//微信用户openId
        if (openid == null) {
            return "redirect:" + ConfigUtil.GET_CODE;//微信取code
        }
        page = commodityService.findPage(page, commodity);
        model.addAttribute("page", page);
        model.addAttribute("commodityList", page.getList());
        return "/mqds/m/index";//首页
    }

    /**
     * 手机端商品详情页面
     *
     * @param commodityId 商品id
     * @return
     */
    @RequestMapping("/commodityDetail")
    public String commodityDetail(String commodityId, Model model) {
        //商品详情页面
        Commodity commodity = commodityService.get(commodityId);
        model.addAttribute("commodity", commodity);
        //商品推荐
        Commodity commodity1 = new Commodity();
        Page<Commodity> page = new Page<Commodity>(1, 6);//分页查询
        page = commodityService.findPage(page, commodity1);
        model.addAttribute("commodityList", page.getList());
        return "mqds/m/commodityDetail";
    }

    /**
     * 手机端商品详情页面AJAX
     *
     * @param commodityId 商品id
     * @return
     */
    @RequestMapping("/commodityById")
    @ResponseBody
    public Map<String, Object> commodityById(String commodityId) {
        Map<String, Object> returnMap = new HashedMap();
        //商品详情页面
        try {
            Commodity commodity = commodityService.get(commodityId);
            returnMap.put("data", commodity);
            returnMap.put("msg", "查询数据成功");
            returnMap.put("code", "0");
        } catch (Exception e) {
            e.printStackTrace();
            returnMap.put("msg", "查询数据失败");
            returnMap.put("code", "-1");
        }
        return returnMap;
    }

    /**
     * 添加到购物车
     *
     * @return
     */
    @RequestMapping("/addShoppingCat")
    @ResponseBody
    public Map<String, Object> addShoppingCat(ShoppingCat shoppingCat) {
        Map<String, Object> returnMap = new HashedMap();
        try {
            shoppingCatService.save(shoppingCat);
            returnMap.put("code", "0");
            returnMap.put("msg", "加入购物车成功");
        } catch (Exception e) {
            returnMap.put("code", "-1");
            returnMap.put("msg", "加入购物车失败");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 手机端购物车
     *
     * @param userId 个人id
     * @return
     */
    @RequestMapping("/shoppingCat")
    public String shoppingCat(Model model, String userId) {
        List<Map<String, Object>> listMap = shoppingCatService.findShoppingCatByUserId(userId);//个人购物车列表
        model.addAttribute("listMap", listMap);

        //商品推荐
        Commodity commodity = new Commodity();
        Page<Commodity> page = new Page<Commodity>(1, 6);//分页查询
        page = commodityService.findPage(page, commodity);
        model.addAttribute("commodityList", page.getList());
        return "mqds/m/shoppingCat";
    }

    /**
     * 手机端订单生成页面
     *
     * @param userId 个人id
     * @return
     */
    @RequestMapping("/orderPage")
    public String orderPage(Model model, String userId) {
        try {
            List<ReceiptAddress> list = addressService.findListfByUserId(userId);//个人收货地址
            model.addAttribute("addresslist", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mqds/m/orderPage";
    }

    /**
     * 手机端订单详情页面
     *
     * @return
     */
    @RequestMapping("/orderDetail")
    public String orderDetail(String orderNumber, Model model) {
        try {
            Map<String, Object> listMap = orderService.findOrderDetailByOrderNumber(orderNumber);
            model.addAttribute("listMap", listMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mqds/m/orderDetail";
    }

    /**
     * 手机端所有订单页面
     *
     * @return
     */
    @RequestMapping("/orderList")
    public String orderList() {
        return "mqds/m/orderList";
    }

    /**
     * 手机端所有订单页面数据请求
     *
     * @return
     */
    @RequestMapping("/orderListDate")
    @ResponseBody
    public Map<String, Object> orderListData(Integer orderState,
                                             @RequestParam(required = false, defaultValue = "0") Integer pageNo,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize, String userId) {
        Map<String, Object> returnMap = new HashedMap();
        Map<String, Object> paramMap = new HashedMap();
        paramMap.put("pageNo", pageNo);
        paramMap.put("pageSize", pageSize);
        paramMap.put("userId", userId);
        paramMap.put("orderState", orderState);// (0已完成,1待付款,2.待发货,3已发货,4已取消,空为全部)
        try {
            List<Map<String, Object>> list = orderService.findOrderList(paramMap);
            returnMap.put("msg", "订单查询成功");
            returnMap.put("code", "0");
            returnMap.put("data", list);
        } catch (Exception e) {
            returnMap.put("msg", "订单查询失败");
            returnMap.put("code", "-1");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 手机端成生订单方法
     *
     * @param commodityId    商品id 多个用","分割
     * @param buyNumber      每个商品购买的数量多个用","分割
     * @param commodityPrice 每个商品单价多个用","分割
     * @param addressId      收货地址id
     * @param userId         个人Id
     * @return
     */
    @RequestMapping("/saveOrder")
    @ResponseBody
    public Map<String, Object> saveOrder(String commodityId, String buyNumber,
                                         String commodityPrice, String addressId, String userId) {
        List<Order> list = new ArrayList<Order>();
        Map<String, Object> returnMap = new HashedMap();
        Map<String, Object> map = new HashedMap();
        Date d = new Date();
        SimpleDateFormat foramt = new SimpleDateFormat("yyyMMddHHmmss");
        String orderNumber = foramt.format(d) + new Date().getTime();//生成订单号
        String[] commodityIdList = commodityId.split(",");//截取每一个商品id
        String[] buyNumberList = buyNumber.split(",");//截取每一个商品id对应购买的数量
        String[] commodityPriceList = commodityPrice.split(",");//截取每一个商品id对应购买的数量
        List<Object> listt = new ArrayList<Object>();
        for (int i = 1; i < commodityIdList.length; i++) {//购物车购买
            Order order = new Order();
            order.setUserId(userId);
            order.setId(IdGen.uuid());//设置id
            order.setCommodityId(commodityIdList[i]);//设置商品id
            listt.add(commodityIdList[i]);//用于清空购物车
            order.setCommodityNumber(Integer.parseInt(buyNumberList[i]));//设置商品购买的数量
            order.setCommodityPrice(commodityPriceList[i]);//设置商品购买的单价
            order.setOrderState("1");//待付款
            order.setAddressId(addressId);
            order.setOrderNumber(orderNumber);
            order.setCreateDate(d);
            list.add(order);//用于生成订单
        }
        try {
            if (list.size() < 1) {//商品详情立即购买
                Order order = new Order();
                order.setUserId(userId);
                order.setId(IdGen.uuid());//设置id
                order.setCommodityId(commodityId);//设置商品id
                order.setCommodityNumber(Integer.parseInt(buyNumber));//设置商品购买的数量
                order.setCommodityPrice(commodityPrice);//设置商品购买的单价
                order.setOrderState("1");//待付款
                order.setAddressId(addressId);
                order.setOrderNumber(orderNumber);
                order.setCreateDate(d);
                list.add(order);//用于生成订单
            } else {
                map.put("commodityList", listt);
                map.put("userId", userId);
                map.put("update", d);
                shoppingCatService.delShoppingCatByCommodityId(map);//标记删除购物车
            }
            orderService.addList(list);//生成订单
            returnMap.put("msg", "生成订单成功");
            returnMap.put("code", "0");
            returnMap.put("orderNumber", orderNumber);//订单号
        } catch (Exception e) {
            returnMap.put("msg", "生成订单失败");
            returnMap.put("code", "-1");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 支付页面
     *
     * @param orderNumber 订单号
     * @param orderBody   订单描述或商品名称
     * @param payMoney    支付金额
     * @return
     */
    @RequestMapping("/payPage")
    public String payPage(Model m, String orderBody, String payMoney, String orderNumber, HttpServletRequest request) {
        String openid = (String) request.getSession().getAttribute("openid");//微信用户openId
        m.addAttribute("openid", openid);
        m.addAttribute("orderNo", orderNumber);
        String timeStamp = PayCommonUtil.create_timestamp();
        String nonceStr = PayCommonUtil.create_nonce_str();
        m.addAttribute("appid", ConfigUtil.APPID);
        m.addAttribute("timestamp", timeStamp);
        m.addAttribute("nonceStr", nonceStr);
        m.addAttribute("openid", openid);
     //微信支付的金额单位为分。所以这里要*100
        int price = (int) (Float.parseFloat(payMoney) * 100);
        String prepayId = WxPayUtil.unifiedorder(orderBody, orderNumber, openid, price);
        SortedMap<Object, Object> signParams = new TreeMap<Object, Object>();
        signParams.put("appId", ConfigUtil.APPID);
        signParams.put("nonceStr", nonceStr);
        signParams.put("package", "prepay_id=" + prepayId);
        signParams.put("timeStamp", timeStamp);
        signParams.put("signType", "MD5");
        // 生成支付签名，要采用URLENCODER的原始值进行SHA1算法！
        String sign = PayCommonUtil.createSign(signParams);
        m.addAttribute("paySign", sign);
        m.addAttribute("packageValue", "prepay_id=" + prepayId);
        return "mqds/m/payPage";
    }

    /**
     * 支付回调收到后,告诉微信不要再发起支付了
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/payCallBack")
    public void payCallBack(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        String retInfo = PayCommonUtil.setXML("SUCCESS", "OK");
        response.getWriter().write(retInfo);
    }

    /**
     * 支付成功后改订单回已支付
     * @param orderNumber
     * @return
     */
    @RequestMapping("updateOrderState")
    @ResponseBody
    public Map<String,Object> updateOrderState(String orderNumber,String state){
        Map<String,Object>  returnMap = new HashedMap();
        try {
            Map<String,Object> paramMap = new HashedMap();
            paramMap.put("orderNumber",orderNumber);
            paramMap.put("orderState",state);
            paramMap.put("updateDate",new Date());
            orderService.updateOrderState(paramMap);//0已完成,1待付款,2.待发货,3已发货,4已取消)
            returnMap.put("code","0");
            returnMap.put("msg","修改订单成功");
        } catch (Exception e) {
            returnMap.put("code","-1");
            returnMap.put("msg","修改订单失败");
            e.printStackTrace();
        }
        return returnMap;
    }
    /**
     * 手机端个人中心
     *
     * @return
     */
    @RequestMapping("/personalCenter")
    public String personalCenter() {
        return "mqds/m/personalCenter";
    }

    /**
     * 手机端个人收货地址列表
     *
     * @param userId 个人id
     * @return
     */
    @RequestMapping("/addressList")
    public String addressList(String userId, Model model) {
        try {
            List<ReceiptAddress> list = addressService.findListfByUserId(userId);
            model.addAttribute("list", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mqds/m/addressList";
    }

    /**
     * 手机端修改个人默认收货地址
     *
     * @param userId 个人id
     * @return
     */
    @RequestMapping("/checkedDefault")
    @ResponseBody
    public Map<String, Object> checkedDefault(String userId, String adddressId) {
        Map<String, Object> returnMap = new HashedMap();
        Map<String, Object> prarmMap = new HashedMap();
        try {
            prarmMap.put("userId", userId);//用户Id
            prarmMap.put("id", adddressId);//地址Id
            prarmMap.put("updateDate", new Date());
            addressService.updateDefault(prarmMap);
            returnMap.put("msg", "修改默认地址成功");
            returnMap.put("code", "0");
        } catch (Exception e) {
            returnMap.put("msg", "修改默认地址失败");
            returnMap.put("code", "-1");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 手机端删除收货地址
     *
     * @return
     */
    @RequestMapping("/delAddress")
    @ResponseBody
    public Map<String, Object> delAddress(String adddressId) {
        Map<String, Object> returnMap = new HashedMap();
        try {
            addressService.delDelAddress(adddressId);
            returnMap.put("msg", "删除地址成功");
            returnMap.put("code", "0");
        } catch (Exception e) {
            returnMap.put("msg", "删除地址失败");
            returnMap.put("code", "-1");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 手机端修改收货地址详情
     *
     * @return
     */
    @RequestMapping("/addressDetail")
    @ResponseBody
    public Map<String, Object> addressDetail(String adddressId) {
        Map<String, Object> returnMap = new HashedMap();
        try {
            ReceiptAddress receiptAddress = addressService.findListfById(adddressId);
            returnMap.put("msg", "查询成功");
            returnMap.put("code", "0");
            returnMap.put("data", receiptAddress);
        } catch (Exception e) {
            returnMap.put("msg", "查询失败");
            returnMap.put("code", "-1");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 手机端添加收货地址
     *
     * @param userId 个人id
     * @return
     */
    @RequestMapping("/saveAddress")
    @ResponseBody
    public Map<String, Object> saveAddress(ReceiptAddress receiptAddress, String userId) {
        Map<String, Object> returnMap = new HashedMap();
        receiptAddress.setUserId(userId);
        receiptAddress.setIsDefault("1");//不是默认地址
        try {
            addressService.save(receiptAddress);
            returnMap.put("msg", "添加地址成功");
            returnMap.put("code", "0");
        } catch (Exception e) {
            returnMap.put("msg", "添加地址失败");
            returnMap.put("code", "-1");
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 手机端银行卡列表
     *
     * @return
     */
    @RequestMapping("/bindBankCard")
    public String bindBankCard() {
        return "mqds/m/bindBankCard";
    }

    /**
     * 手机端系统设置
     *
     * @return
     */
    @RequestMapping("/systemSettings")
    public String systemSettings() {
        return "mqds/m/systemSettings";
    }


    //登录页面
    @RequestMapping("/loginPage")
    public String loginPage() {
        return "mqds/m/login";
    }

    /**
     * 手机端登录
     *
     * @param code 验证码
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public Map<String, Object> login(Muser muser, String code, HttpServletRequest request) {
        Map<String, Object> returnMap = new HashedMap();
        Map<String, Object> paramMap = new HashedMap();
        try {
            if (muser.getOpenId() != null) {
                paramMap.put("openId", muser.getOpenId());
                //微信登录
            } else if (1 == 1) {//验证码校验
                paramMap.put("phone", muser.getPhone());
                Muser m = checkUser(paramMap);
                if (m == null) {//如果该用户没有注册过
                    muser.setIsVip("1");
                    mUserSerivce.save(muser);//新增用户
                    m = checkUser(paramMap);//新增完再去校验一次
                }
                request.getSession().setAttribute("mUser", m);
            }
            returnMap.put("code", "0");
            returnMap.put("msg", "登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            returnMap.put("code", "-1");
            returnMap.put("msg", "登录失败");
        }
        return returnMap;
    }

    /**
     * 手机端注销
     *
     * @return
     */
    @RequestMapping("/loginOut")
    @ResponseBody
    public Map<String, Object> loginOut(HttpServletRequest request) {
        Map<String, Object> returnMap = new HashedMap();
        request.getSession().removeAttribute("mUser");
        returnMap.put("code", "0");
        returnMap.put("msg", "注销成功");
        return returnMap;
    }

    //验证用户是否已经存在
    public Muser checkUser(Map<String, Object> map) {
        try {
            return mUserSerivce.findUser(map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Muser();
        }
    }
}
