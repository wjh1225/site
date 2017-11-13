<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include/taglib.jsp" %>
<html>
<!--地址列表-->
<head>
    <%@include file="include/head.jsp" %>
    <link href="${ctxStatic}/m/address/css/LArea.css" rel="stylesheet">
    <script src="${ctxStatic}/m/address/js/LArea.js"></script>
    <script src="${ctxStatic}/m/address/js/LAreaData1.js"></script>
</head>
<style>
    .add-address-div {
        position: fixed;
        bottom: 0;
        width: 100%;
        padding: 10px 0;
        background: #f9f9f9;
    }

    .add-address-div .am-btn-danger {
        width: 80%;
        margin: auto;
        display: block;
    }

    .am-list h5 {
        margin: 0;
        color: #333;
    }

    .am-list {
        font-size: 12px;
    }

    .am-divider-default {
        border-top: 1px solid #fdf6f6;
        margin: 0;
        margin: 5px 0;
    }

    .am-ucheck-icons {
        top: 3px;
    }

    .am-list-static.am-list-border > li {
        padding-bottom: 0;
    }

    .operation {
        position: absolute;
        right: 10px;
        bottom: 0;
    }

    .operation a {
        margin-left: 10px;
    }

    .bottom-model > .model-title li {
        line-height: 50px;
        border-bottom: 1px solid #eee;
    }

    .bottom-model > .model-title li input {
        height: 50px;
        width: 77%;
        border: none;
    }

    #loading .am-modal-dialog {
        position: absolute;
        width: 60%;
        top: 5%;
        right: 20%;
    }

    #loading {
        z-index: 1108;
    }

    form {
        margin: 0;
    }
</style>
<body>
<div class="content">
    <ul class="am-list am-list-static am-list-border">
        <c:forEach var="itme" items="${list}">
            <li>
                <h5>${itme.consignee} ${itme.consigneePhone}</h5>
                <span>${itme.province}${itme.city}${itme.county}${itme.address}</span>
                <hr data-am-widget="divider" style="" class="am-divider am-divider-default"/>
                <label class="am-radio">
                    <input type="radio" name="isDefault" value="${itme.id}" data-am-ucheck
                           <c:if test="${itme.isDefault == '0'}">checked</c:if>>
                    默认地址
                </label>
                <div class="operation">
                    <a class="update-address" id="${itme.id}">编辑</a>
                    <a class="del-address" id="${itme.id}">删除</a>
                </div>
            </li>
        </c:forEach>
    </ul>

    <div class="add-address-div">
        <button type="button" class="am-btn am-btn-danger">+新建地址</button>
    </div>
</div>
<!--底部弹出新增地址-->
<div class="am-modal-actions" id="open-bottom-model">
    <form id="address">
        <div class="bottom-model">
            <ul class="am-list model-title">
                <input name="id" hidden><!--id 修改时用到-->
                <span class="close-model">x</span>
                <li class="am-g am-list-item-dated">
                    <span>收货人:</span><input name="consignee">
                </li>
                <li class="am-g am-list-item-dated">
                    <span>联系方式:</span>
                    <input name="consigneePhone" maxlength="11">
                </li>
                <li class="am-g am-list-item-dated">
                    <span>所在区域:</span>
                    <input id="demo1" name="location" readonly>
                </li>
                <li class="am-g am-list-item-dated">
                    <span>详细地址:</span><input name="receipAddress">
                </li>
            </ul>
            <button type="button" class="am-btn am-btn-danger" onclick="saveAddress()">确定</button>
            <button type="reset" id="reset" hidden></button>
        </div>
    </form>
</div>
<!-- 删除地址-->
<div class="am-modal am-modal-confirm" tabindex="-1" id="confirm">
    <div class="am-modal-dialog">
        <div class="am-modal-hd">温馨提示</div>
        <div class="am-modal-bd">
            你，确定要删除这条记录吗？
        </div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-cancel>取消</span>
            <span class="am-modal-btn" data-am-modal-confirm>确定</span>
        </div>
    </div>
</div>
<script>
    $(".update-address").on("click", function () {
        $("#reset").click();
        $.post("${ctx}/m/addressDetail?adddressId=" + $(this).attr("id"), function (ret) {
            if (ret.code == "0") {
                $("input[name='id']").val(ret.data.id);
                $("input[name='consignee']").val(ret.data.consignee);
                $("input[name='consigneePhone']").val(ret.data.consigneePhone);
                $("input[name='location']").val(ret.data.province+","+ret.data.city+","+ret.data.county);
                $("input[name='receipAddress']").val(ret.data.address);
                $("#open-bottom-model").modal('open');
            }
        })
    })
    $(".am-btn-danger").on("click", function () {
        $("#open-bottom-model").modal('open');
    })
    $(".close-model").on("click", function () {
        $("#open-bottom-model").modal('close');
    })
    $(".del-address").on("click", function () {
        $('#confirm').modal({
            relatedTarget: this,
            onConfirm: function (options) {
                $.post("${ctx}/m/delAddress?adddressId=" + $(this.relatedTarget).attr("id"), function (data) {
                    if (data.code == "0") {
                        window.location.href = window.location.href;//刷新
                    }
                })
            },
            onCancel: function () {

            }
        });
    })
    //三级联动
    var area1 = new LArea();
    area1.init({
        'trigger': '#demo1', //触发选择控件的文本框，同时选择完毕后name属性输出到该位置
        'valueTo': '#value1', //选择完毕后id属性输出到该位置
        'keys': {
            id: 'id',
            name: 'name'
        }, //绑定数据源相关字段 id对应valueTo的value属性输出 name对应trigger的value属性输出
        'type': 1, //数据源类型
        'data': LAreaData //数据源
    });
    area1.value = [6, 11, 7];//控制初始位置，注意：该方法并不会影响到input的value
    //保存收货地址
    function saveAddress() {
        var consignee = $("input[name='consignee']").val();
        var consigneePhone = $("input[name='consigneePhone']").val();
        var location = $("input[name='location']").val();
        var receipAddress = $("input[name='receipAddress']").val();

        if (consignee == "") {
            loadingShow("收货人不能为空");
            return;
        }
        if (consigneePhone == "") {
            loadingShow("联系方式不能为空");
            return;
        }
        if(!(/^1[3|4|5|8][0-9]\d{4,8}$/.test(consigneePhone))){
            loadingShow("请输入正确的手机号");
            return ;
        }
        if (location == "") {
            loadingShow("所在区域不能为空");
            return;
        }
        var locationList = location.split(",");//分割地址
        if (receipAddress == "") {
            loadingShow("详细地址不能为空");
            return;
        }
        var DATA = {
            id: $("input[name='id']").val(),
            consignee: consignee,
            consigneePhone: consigneePhone,
            address: receipAddress,
            province: locationList[0] == undefined ? "" : locationList[0],//省
            city: locationList[1] == undefined ? "" : locationList[1],//市
            county:locationList[2] == undefined ? "" : locationList[2],//区/县
            userId:'${param.userId}'//个人Id
        }
        $.post("${ctx}/m/saveAddress", DATA, function (data) {
            if (data.code == "0") {
                $("#open-bottom-model").modal('close');
                window.location.href = window.location.href;//刷新
            }
        })
    }
    //更改默认地址
    $(function () {
        $(":radio").click(function () {
            $.post("${ctx}/m/checkedDefault?adddressId=" + $(this).val(), function (data) {
                if (data.code == "0") {
                    window.location.href = window.location.href;//刷新
                }
            })
        });
    });
</script>
</body>
</html>