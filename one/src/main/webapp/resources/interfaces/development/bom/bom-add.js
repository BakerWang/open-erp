/*!
 * bominfo
 * Copyright 2011-2015 zhangjh.
 * Licensed under MTT (https://github.com/firebata/skysport/blob/master/LICENSE)
 */
(function ($) {
    "use strict";
    var path = $.basepath();
    var saveUrl = path + "/development/bom/edit";
    var listUrl = path + "/development/bom/list";
    var mainColorOld;
    $.extend({
        bomSave: bomSave,
        bomSubmit: bomSubmit,
        bomAutoBackup: bomAutoBackup,
        mainColorEditInBom: mainColorEditInBom
    });

    $(function () {

        initBom();
        //赋值价格
        $("#factoryItemInfo").on("change", "select", cb);

    });

    /**
     * 点击编辑
     * @param _this
     */
    function mainColorEditInBom(_this) {
        // var mainColorDisabled = $('#mainColor').attr("disabled");
        var classVal = $(_this).attr("class");
        var editClassVal = 'glyphicon glyphicon-edit';
        var okClassVal = 'glyphicon glyphicon-ok';
        var $mainColor = $('#mainColor');
        if (classVal == editClassVal) {
            $mainColor.removeAttr("disabled");
            $(_this).removeClass(editClassVal).addClass(okClassVal);
        } else if (classVal == okClassVal) {
            $mainColor.attr("disabled", "disabled");
            $(_this).removeClass(okClassVal).addClass(editClassVal);
        }
    }

    function initQuetoInfo(quoteReference, _thisId, f) {

        if (quoteReference === '1') {//参考工厂报价
            var idNum = _thisId.substring(14);
            $("#factoryOffer").val($("#factoryOffer" + idNum).val());
            $("#factoryMargins").val($("#factoryMargins" + idNum).val());
            $("#euroPrice").val($("#euroPrice" + idNum).val());
        }
        f();
    }

    function cb() {
        if ($(this).attr('name') === 'quoteReference') {
            var quoteReference = $(this).val();
            var thisId = $(this).attr('id');
            initQuetoInfo(quoteReference, thisId, function () {

            });
        }
    }

    function initBom() {
        //初始化描述信息
        //var natrualkey = $("#natrualkey").val();
        $.initBomDesc(function (_data) {

            //初始化报价信息
            $.iniBomQuotedInfo(_data.quotedInfo);

            //初始化面料
            $.initFabric(_data.fabrics);

            //初始化辅料
            $.initAccessories(_data.accessories);

            //初始化包装材料
            $.initPackaging(_data.packagings);

            //初始成衣厂
            $.initFactory(_data.factoryQuoteInfos);

        });

    }


    function buildBomDesc() {

        bominfo.offerAmount = $("#offerAmount").val();
        bominfo.mainColor = $("#mainColor").val();
        bominfo.projectId = $("#projectId").val();
        bominfo.sexId = $("#sexId").val();
        bominfo.mainColorOld = $("#mainColorOld").val();
        bominfo.fabricsEndDate = $("#fabricsEndDate").val();
        bominfo.accessoriesEndDate = $("#accessoriesEndDate").val();
        bominfo.preOfferDate = $("#preOfferDate").val();
        bominfo.clothReceivedDate = $("#clothReceivedDate").val();
        bominfo.natrualkey = $("#natrualkey").val();

    }

    /**
     * 保存数据
     */
    function bomSaveFun(needToLisPage) {

        $.sendJsonAjax(saveUrl, bominfo, function (_bomInfo) {
            if (null == needToLisPage) {
                window.location.href = listUrl;
            } else {
                bootbox.alert("成功暂存数据.");
                if (null != _bomInfo) {
                    var fabrics = _bomInfo["fabrics"];
                    var accessories = _bomInfo["accessories"];
                    var packagings = _bomInfo["packagings"];
                    $.refreshAllFabricId(fabrics);
                    $.refreshAllAccessoriesId(accessories);
                    $.refreshAllPackagingId(packagings);
                }
            }

        })

    }

    /**
     * 报价信息
     */
    function buildBomQuoted() {

        bominfo.quotedInfo = {};
        bominfo.quotedInfo.factoryOffer = $("#factoryOffer").val();
        bominfo.quotedInfo.factoryMargins = $("#factoryMargins").val();
        bominfo.quotedInfo.costing = $("#costing").val();
        bominfo.quotedInfo.lpPrice = $("#lpPrice").val();
        bominfo.quotedInfo.euroPrice = $("#euroPrice").val();
        bominfo.quotedInfo.exchangeCosts = $("#exchangeCosts").val();
        bominfo.quotedInfo.bomId = $("#natrualkey").val();

    }


    /**
     * 构建保存数据，并保存
     */
    function bomSave(needToLisPage) {

        //描述信息
        buildBomDesc();

        //包材信息
        //var fabricItems = buildFabricItems();
        bominfo.fabricItems = $.fabricItems();

        //辅料信息
        var accessoriesItems = $.accessoriesItems();
        bominfo.accessoriesItems = accessoriesItems;

        //包装材料信息
        var packagingItems = $.packagingItems();
        bominfo.packagingItems = packagingItems;

        //成衣厂
        var factoryQuoteInfos = $.factoryQuoteInfos();
        bominfo.factoryQuoteInfos = factoryQuoteInfos;


        buildBomQuoted();

        bomSaveFun(needToLisPage);
    }

    var bominfo = {};

    function bomAutoBackup() {
        bomSave(false);
    }

    function bomSubmit() {

    }
}(jQuery));