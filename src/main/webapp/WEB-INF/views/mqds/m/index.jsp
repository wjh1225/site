<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include/taglib.jsp" %>
<html>
<head>
    <%@include file="include/head.jsp" %>
    <link href="${ctxStatic}/m/css/indexAndShop.css" rel="stylesheet">
</head>
<body>
<c:set value="${commodityList}" var="list"/>
<div class="header">
    <input placeholder="输入您要查找的货物名称" class="input-search" id="commodityName">
    <button class="but-search"><span class="sousuo"></span></button>
    <ul class="nav-menu">
        <li class="active" onclick="tapMenu(this,1)">推荐</li>
        <li onclick="tapMenu(this,2)">热门</li>
    </ul>
</div>
<div class="content" id="content">
    <!--必卖商品-->
    <div data-tab-panel-0 class="am-tab-panel am-active">
        <div data-am-widget="slider" class="am-slider am-slider-manual am-slider-c4">
            <div class="swiper-container banner">
                <div class="swiper-wrapper" id="banner">
                    <!--必卖商品banner -->
                </div>
            </div>
        </div>

        <ul data-am-widget="gallery" class="am-gallery am-avg-sm-1 am-avg-md-3 am-avg-lg-4 am-gallery-bordered"
            data-am-gallery="{  }" id="commodityXisMenu">
            <span class="title-six">超值热卖</span>
            <!--6个商品菜单-->
        </ul>
        <div class="am-slider am-slider-carousel boutique">
            <%--<span class="title">推荐精品</span>--%>
            <span class="title-six">推荐精品</span>
            <div class="swiper-container products">
                <div class="swiper-wrapper" id="products">
                    <!--推荐精品 -->
                </div>
            </div>
        </div>
        <ul data-am-widget="gallery" class="am-gallery am-avg-sm-1 am-avg-md-3 am-avg-lg-4 am-gallery-bordered"
            data-am-gallery="{  }" id="commodityLsit">
            <!--商品列表-->
        </ul>
    </div>
</div>
<footer>
    <div data-am-widget="navbar" class="am-navbar am-cf am-navbar-default"
         id="">
        <ul class="am-navbar-nav am-cf am-avg-sm-4">
            <li class="active home">
                <a href="${ctx}/m" class="">
                    <i class="icon-home"></i>
                    <span>首页</span>
                </a>
            </li>
            <li>
                <a href="${ctx}/m/classification" class="">
                    <i class="classification"></i>
                    <span>分类</span>
                </a>
            </li>
            <li onclick="openPage('${ctx}/m/shoppingCat?userId=${sessionScope.mUser.id}')">
                <a href="javascript:void(0);" class="">
                    <i class="shopping-cat"></i>
                    <span>购物车</span>
                </a>
            </li>
            <li onclick="openPage('${ctx}/m/personalCenter')">
                <a href="javascript:void(0);" class="">
                    <i class="personal-center"></i>
                    <span>我的</span>
                </a>
            </li>
        </ul>
    </div>
</footer>
<script>
    var phone = '${sessionScope.mUser.phone}';
    var login = '${sessionScope.mUser.login}';
    function openPage(url) {
        if (login == "no" || login == "") {//如果手动注销过
            window.location.href = "${ctx}/m/loginPage";
            return;
        } else {//已经绑定
            window.location.href = url;
        }
    }
    $(document).ready(function () {
        loadingShow();//首次提示
        init();

    })
    var isScroll = true;
    var isLoading = true;//确保滑动每次加载一次
    $(window).scroll(function () {//向下滑动
        if ($(window).scrollTop() >= (document.getElementById("content").scrollHeight - window.screen.height)) {
            if (isScroll && isLoading) {
                init();
            }

        }
    });
    var paramData = {
        pageNo: 0,
        pageSize: 5,
        type: 1,//1推荐2热门
        commodityName: ""//模糊查询商品名称
    }
    var userId = '${sessionScope.mUser.id}';//用户Id
    //页面初始数据
    function init() {
        isLoading = false;
        $.post("${ctx}/m/indexData", paramData, function (ret) {
            if (ret.code == "0") {
                paramData.pageNo++;
                if (paramData.pageNo == 1 && paramData.type == 1 && paramData.commodityName == "") {//只加载一次
                    var banner = ret.banner;//商品banenr
                    var strBanner = "";//顶部图片banner
                    for (var i = 0; i < banner.length; i++) {
                        var img = "";
                        if (banner[i].commodityImager != null) {
                            img = banner[i].commodityImager.split("|");
                        }
                        strBanner += '<div class="swiper-slide"> <a href="${ctx}/m/commodityDetail?commodityId=' + banner[i].id + '"> <img  class="lazy" src="' + img[1] + '"/></a></div>';
                    }
                    $("#banner").append(strBanner);//顶部banner图
                    var mySwiper = new Swiper('.banner', {
                        effect: 'fade',
                        loop: true,
                        autoplay: 3000,//可选选项，自动滑动
                        autoplayDisableOnInteraction: false
                    })
                    var products = ret.products;//推荐精品
                    var productsStr = "";//推荐精品数据拼接
                    for (var i = 0; i < products.length; i++) {
                        var img = "";
                        var imgSrc = "";
                        if (products[i].commodityImager != null) {
                            img = products[i].commodityImager.split("|");
                            for (var k = 0; k < img.length; k++) {
                                if (img[k] != null && img[k] != "") {
                                    imgSrc = img[k];
                                    break;
                                }
                            }
                        }
                        productsStr += '<div class="swiper-slide"><a href="${ctx}/m/commodityDetail?commodityId=' + products[i].id + '"> <img  class="lazy" src="' + imgSrc + '"/>';
                        productsStr += '<span class="img-name">' + products[i].commodityName + '</span></a></div>';
                    }
                    $('#products').append(productsStr);
                    var mySwiper = new Swiper('.products', {
                        slidesPerView: 'auto',//'auto'
                        onTransitionEnd: function (swiper) {
                            if (swiper.progress == 1) {
                                swiper.activeIndex = swiper.slides.length - 1
                            }
                        }
                    })
                    if (productsStr == "") {//如果没有精品图片
                        $('#products').parent().hide();
                    }
                    var data = ret.listIndexMenuSix;//首页6个菜单商品
                    var commodityXisMenuStr = "";//首页6个菜单商品列表
                    for (var i = 0; i < data.length; i++) {//首页6个菜单商品商品列表
                        var img = "";
                        if (data[i].commodityImager != null) {
                            img = data[i].commodityImager.split("|");
                        }

                        commodityXisMenuStr += '<li><div class="am-gallery-item">';
                        commodityXisMenuStr += ' <a href="${ctx}/m/commodityDetail?commodityId=' + data[i].id + '" class=""><img class="lazy" src="' + img[1] + '"/>';
                        /*   commodityXisMenuStr += '<div class="am-gallery-desc"><hr/></ul><ul class="nav-menu"><li>￥</li>';*/
                        var price = data[i].commodityPice;
                        if ((/(^[1-9]\d*$)/.test(price))) {
                            price += ".00"
                        }
                        /*  commodityXisMenuStr += '<li class="active">' + price + '</li><li>';
                         commodityXisMenuStr += '<a href="javascript:buyNow(\'' + data[i].id + '\')">';
                         commodityXisMenuStr += '<spen class="buy">#购买</spen></a></li></ul></div></div></li>';*/
                        commodityXisMenuStr += '<h3>' + data[i].commodityName + '</h3><span>￥' + price + '</span></a>';
                    }

                    $("#commodityXisMenu").append(commodityXisMenuStr);//首页6个菜单商品商品列表
                }
                var data = ret.listCommodity;//商品数据
                var commodityListStr = "";//商品列表
                for (var i = 0; i < data.length; i++) {//商品列表
                    var img = "";
                    if (data[i].commodityImager != null) {
                        img = data[i].commodityImager.split("|");
                    }
                    var style = "";
                    if (data.length == 1) { //如果只有一个商品
                        style = "border-bottom: 1px solid #eee;margin-bottom: 8px;border-top: 10px solid #eee;";
                    }
                    commodityListStr += '<li style="' + style + '"><div class="am-gallery-item">';
                    commodityListStr += ' <a href="${ctx}/m/commodityDetail?commodityId=' + data[i].id + '" class=""><img class="lazy" src="' + img[1] + '"/>';
                    //commodityListStr += '<div class="am-gallery-desc"><hr/></ul><ul class="nav-menu"><li>￥</li>';
                    var price = data[i].commodityPice;
                    if ((/(^[1-9]\d*$)/.test(price))) {
                        price += ".00"
                    }
                    /*     commodityListStr += '<li class="active">' + price + '</li><li>';
                     commodityListStr += '<a href="javascript:buyNow(\'' + data[i].id + '\')">';
                     commodityListStr += '<spen class="buy">#购买</spen></a></li></ul></div></div></li>';*/
                    commodityListStr += '<h3>' + data[i].commodityName + '</h3><span>￥' + price + '</span></a>';
                }
                if (commodityListStr != "") {
                    $("#commodityLsit").append(commodityListStr);//商品列表
                    isLoading = true;//如果还有数据可以再给滑动加载
                }
                if (data.length < paramData.pageSize) {
                    isScroll = false;//不可以滑动了
                }
                /*  if (data.length < paramData.pageSize && paramData.pageNo > 1) {
                 loadingShow("数据加载完啦", "2000");
                 return;
                 }*/
                loadingClose();//关闭加载提示
                //$("img.lazy").lazyload();//图片懒加载  src换成data-original
            }
        })
    }
    //头部推荐和热门两切换
    function tapMenu(ev, type) {
        $("#commodityName").val("");
        loadingShow();//加载提示
        paramData.pageNo = 0;
        paramData.type = type;
        isScroll = true;
        isLoading = true;
        paramData.commodityName = "";
        $(ev).siblings().removeClass("active");
        $(ev).addClass("active");
        if (type == 2) {//热门
            $(".am-slider").hide();
            $("#commodityXisMenu").hide();
        } else {//推荐
            $(".am-slider").show();
            $("#commodityXisMenu").show();
        }
        $("#commodityLsit").html("");//清空
        init();
    }
    //模糊搜索商品名称 确认或完成建
    $(document).keydown(function (event) {
        if (event.keyCode == 13) {
            findCommdityByName()
        }
    });
    //模糊搜索商品名称
    $(".but-search").on("click", function () {
        findCommdityByName();

    })
    ///模糊搜索商品名称
    function findCommdityByName() {
        var commodityName = $("#commodityName").val();
        if (commodityName == "") {
            $(".am-slider").show();
            $("#commodityXisMenu").show();
            return;
        }
        paramData.commodityName = commodityName;
        paramData.pageNo = 0;
        paramData.type = 1;//所有商品
        isScroll = true;
        isLoading = true;
        $(".am-slider").hide();
        $("#commodityLsit").html("");//清空
        $("#commodityXisMenu").hide();
        init();
    }
    //立即购买
    function buyNow(commodityId) {
        if (userId != "") {//立即购买页面
            window.location.href = '${ctx}/m/orderPage?nowBuy=yes&commodityId=' + commodityId + '&userId=' + userId;
        } else {//没有登录，去登录
            window.location.href = "${ctx}/m/loginPage";
        }

    }
</script>
</body>
</html>
