/**
 * Created by zhangjh on 2015/7/14.
 */
(function () {

    "use strict";
    var path = $.basepath();
    var project_selectURL = path + "/system/baseinfo/project_select";
    var project_infoURL = path + "/development/project_item/info/";
    var project_listURL = path + "/development/project_item/list";
    var project_newURL = path + "/development/project_item/new";
    var project_editURL = path + "/development/project_item/edit";
    var uploadFileInfos = [];
    var $fileInput = $("#fileLocation");
    var $fileListLi = $("#filesList");
    var fileUploadURL = path + "/files/upload";


    //第一次初始化下拉列表
    var reloadDetailSelectData = function () {
        $.sendRestFulAjax(project_selectURL, null, 'GET', 'json', initSelectCallBack);
    }

    /**
     * 初始化标签
     */
    var initTags = function () {
        var tag_input = $('#mainColorNames');
        try {
            tag_input.tag(
                {
                    placeholder: tag_input.attr('placeholder'),
                    //enable typeahead by specifying the source array
                    source: ace.vars['US_STATES']//defined in ace.js >> ace.enable_search_ahead
                    /**
                     //or fetch data from database, fetch those that match "query"
                     source: function(query, process) {
						  $.ajax({url: 'remote_source.php?q='+encodeURIComponent(query)})
						  .done(function(result_items){
							process(result_items);
						  });
						}
                     */
                }
            )

            //programmatically add a new
            //var $tag_obj = $('#mainColorNames').data('tag');
            //$tag_obj.add('Programmatically Added');
        }
        catch (e) {
            //display a textarea for old IE, because it doesn't support this plugin or another one I tried!
            tag_input.after('<textarea id="' + tag_input.attr('id') + '" name="' + tag_input.attr('name') + '" rows="3">' + tag_input.val() + '</textarea>').remove();
            //$('#form-field-tags').autosize({append: "\n"});
        }

    }

    var initSelectCallBack = function (_data) {
        initSelect(_data);
        queryInfoAnInitFiled();
    }

    var queryInfoAnInitFiled = function () {
        var natrualkey = $("#natrualkey").val();
        if (natrualkey != '') {
            $.sendRestFulAjax(project_infoURL + natrualkey, null, 'GET', 'json', initFormFields);
        }

    }

    /**
     * 初始化表单
     * @param _data
     */
    var initFormFields = function (_data) {

        //console.info("the info of project:" + _data);

        Object.keys(_data).map(function (key) {

            $('#projectForm input').filter(function () {
                return key == this.name;
            }).val(_data[key]);


            if (key == 'sexIds') {
                if (null != _data[key]) {
                    var arr = _data[key].split(',');
                    $('#sexIds').selectpicker("val", arr);
                }
            }
            else {

                //下拉框
                $("#" + key).val(_data[key]);

                if (key != 'fileLocation' && key != 'collectionNumber' && key != 'natrualkey') {
                    $("#" + key).attr('disabled', 'disabled');
                }
            }
        });

        $.loadFileInput($fileInput, $fileListLi, _data["fileinfosMap"], fileUploadURL);


        //初始化性别属性，再初始化性别属性和颜色
        $.initSexColors(_data["sexColors"]);

        //初始化性别属性，再初始化性别属性和颜色
        $.showHandleBtn(_data["approveStatus"], saveProject, $("#natrualkey").val(), $("#taskId").val());
    }


    var initSelect = function (_data) {

        var data = _data;

        //年份
        var yearCodeItems = data["yearItems"];
        $("#yearCode").empty();
        $("<option></option>").val('').text("请选择...").appendTo($("#yearCode"));
        $.each(yearCodeItems, function (i, item) {
            $("<option></option>")
                .val(item["natrualkey"])
                .text(item["name"])
                .appendTo($("#yearCode"));
        });

        //客户
        var yearCodeItems = data["customerItems"];
        $("#customerId").empty();
        $("<option></option>").val('').text("请选择...").appendTo($("#customerId"));
        $.each(yearCodeItems, function (i, item) {
            $("<option></option>")
                .val(item["natrualkey"])
                .text(item["name"])
                .appendTo($("#customerId"));
        });

        //区域
        var areaItems = data["areaItems"];
        $("#areaId").empty();
        $("<option></option>").val('').text("请选择...").appendTo($("#areaId"));
        $.each(areaItems, function (i, item) {
            $("<option></option>")
                .val(item["natrualkey"])
                .text(item["name"])
                .appendTo($("#areaId"));
        });

        //系列
        var seriesItems = data["seriesItems"];
        $("#seriesId").empty();
        $("<option></option>").val('').text("请选择...").appendTo($("#seriesId"));
        $.each(seriesItems, function (i, item) {
            $("<option></option>")
                .val(item["natrualkey"])
                .text(item["name"])
                .appendTo($("#seriesId"));
        });

        //性别属性
        var sexItems = data["sexItems"];
        $("#sexIds").empty();
        $.each(sexItems, function (i, item) {
            $("<option></option>").val(item["natrualkey"]).text(item["name"]).appendTo($("#sexIds"));
        });
        $('#sexIds').selectpicker({noneSelectedText: '请选择...'});
        $('#sexIds').selectpicker('refresh');

        //一级品类
        var categoryAItems = data["categoryAItems"];
        $("#categoryAid").empty();
        $("<option></option>").val('').text("请选择...").appendTo($("#categoryAid"));
        $.each(categoryAItems, function (i, item) {
            $("<option></option>")
                .val(item["natrualkey"])
                .text(item["name"])
                .appendTo($("#categoryAid"));
        });

        //二级品类
        var categoryBItems = data["categoryBItems"];
        $("#categoryBid").empty();
        $("<option></option>").val('').text("请选择...").appendTo($("#categoryBid"));
        $.each(categoryBItems, function (i, item) {
            $("<option></option>")
                .val(item["natrualkey"])
                .text(item["name"])
                .appendTo($("#categoryBid"));
        });

    }

    /**
     *
     */
    var saveProject = function () {
        //执行表单验证
        $('#projectForm').bootstrapValidator('validate');
    }


    var doSaveAction = function () {

        var natrualkey = $("#natrualkey").val();
        var url;
        if (natrualkey === '' || natrualkey === 'null') {
            url = project_newURL;
            toSave(url, natrualkey);
        } else {
            url = project_editURL;
            bootbox.confirm({
                size: 'small',
                message: "修改子项目的性别与色组信息，可能删除下属BOM,确定修改吗?",
                callback: function (result) {
                    if (result) {
                        toSave(url, natrualkey);
                    }
                }
            })
        }

    }


    function toSave(url, natrualkey) {
        project.categoryAid = '';
        project.categoryBid = '';
        project.collectionNumber = '';
        var projectItemInfo = {};
        projectItemInfo.natrualkey = natrualkey;
        var sexColors = [];

        var sexIds = $("[id^=sexIdChild]").arrayVal();

        for (var index = 0, len = sexIds.length; index < len; index++) {
            var sexColor = {};
            sexColor.sexIdChild = sexIds[index];
            sexColor.sexName = $("#sexName" + sexIds[index]).val();
            sexColor.projectId = natrualkey;
            var $tag_obj = $('#mainColorNames' + sexIds[index]).data('tag');
            var tagValues = $tag_obj.values.join(',');
            sexColor.mainColorNames = tagValues;
            sexColors.push(sexColor);
        }

        projectItemInfo.sexColors = sexColors;
        var sexIdsSelect = $("#sexIds").val();
        if (null != sexIdsSelect) {
            projectItemInfo.sexIds = sexIdsSelect.join(",");
        }
        projectItemInfo.fileInfos = uploadFileInfos;
        $.sendJsonAjax(url, projectItemInfo, function () {
            window.location.href = project_listURL;
        })
    }

    /**
     * 新增/修改校验字段描述
     * @returns {{name: {validators: {notEmpty: {message: string}}}, customerId: {validators: {notEmpty: {message: string}}}}}
     */
    var fieldsDesc =
    {
        yearCode: {
            validators: {
                notEmpty: {
                    message: '年份为必填项'
                }
            }
        },
        customerId: {
            validators: {
                notEmpty: {
                    message: '客户为必填项'
                }
            }
        },
        areaId: {
            validators: {
                notEmpty: {
                    message: '区域为必填项'
                }
            }
        },
        seriesId: {
            validators: {
                notEmpty: {
                    message: '区域为必填项'
                }
            }
        },
        categoryAid: {
            validators: {
                notEmpty: {
                    message: '品类一级名称为必填项'
                }
            }
        },
        categoryBid: {
            validators: {
                notEmpty: {
                    message: '品类二级名称为必填项'
                }
            }
        },
        sexIds: {
            validators: {
                notEmpty: {
                    message: '性别属性为必填项'
                }
            }
        },
        collectionNumber: {
            validators: {
                notEmpty: {
                    message: '款式数量为必填项'
                }
            }
        },
        mainColorNames: {
            validators: {
                notEmpty: {
                    message: '性别属性为必填项'
                }
            }
        }
    }


    //启动表单校验监听
    $('#projectForm').bootstrapValidator({
        //live: 'disabled',
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: fieldsDesc
    }).on('success.form.bv', function (e) { //表单校验成功，ajax提交数据
        $fileInput.fileinput('upload');//批量提交
        var hasExtraData = $.isEmptyObject($fileInput.fileinput("getExtraData"));
        if (hasExtraData) {
            doSaveAction();
        }
    });

    var project = {
        projectId: "",
        projectName: "",
        categoryAid: "",
        categoryBid: "",
        collectionNumber: "",
        sexIds: [],//性别属性
        mainColors: [],//色组
        yearCode: "",
        customerId: "",
        areaId: "",
        seriesId: "",
        sampleDelivery: "",//推销样交期
        isNeedSwatch: "",
        isNeedPreOffer: "",
        needPreOfferDate: "",
        sketchReceivedDate: "",
        fileLocation: []
    }

    function getIsSubmitAction() {
        return isSubmitAction;
    }


    $(function () {
        reloadDetailSelectData();
        initTags();
        $.fileInputAddListenr($fileListLi, $fileInput, uploadFileInfos, doSaveAction, getIsSubmitAction);
    })

    function showHandleBtn(_approveStatus, saveFun, _businessKey, _taskId) {
        var html = ""
        if (_approveStatus == approve_status_new || _approveStatus == approve_status_reject) {
            html = "<div class='col-xs-offset-6 col-xs-9'><button type='button' class='btn btn-info btn-md' onclick='javascript:" + saveFun + "'>保存</button></div>";
            html + "<div class='col-xs-offset-6 col-xs-9'><button type='button' class='btn btn-info btn-md' onclick='javascript:$.submitBuss('" + _businessKey + "','" + _taskId + "'>提交</button></div>";
        }
        else if (_approveStatus == approve_status_undo) {
            html = "<div class='col-xs-offset-6 col-xs-9'><button type='button' class='btn btn-info btn-md' onclick='javascript:$.approveBuss('" + _businessKey + "','" + _taskId + "'>审核</button></div>";
        }
        $("#projectBtnInfo").empty().html(html);
    }

    function approveBuss(_businessKey, _taskId) {
        bootbox.alert("提交_businessKey:" + _businessKey + ",_taskId:" + _taskId);
    }

    function submitBuss(_businessKey, _taskId) {
        bootbox.alert("审核_businessKey:" + _businessKey + ",_taskId:" + _taskId);
    }

    $.saveProjectItem = saveProject;
    $.showHandleBtn = showHandleBtn;
    $.approveBuss = approveBuss;
    $.submitBuss = submitBuss;

}());