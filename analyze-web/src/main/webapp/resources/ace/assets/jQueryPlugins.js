var keyCode = {
		ALT: 18,
		BACKSPACE: 8,
		CAPS_LOCK: 20,
		COMMA: 188,
		COMMAND: 91,
		COMMAND_LEFT: 91, // COMMAND
		COMMAND_RIGHT: 93,
		CONTROL: 17,
		DELETE: 46,
		DOWN: 40,
		END: 35,
		ENTER: 13,
		ESCAPE: 27,
		HOME: 36,
		INSERT: 45,
		LEFT: 37,
		MENU: 93, // COMMAND_RIGHT
		NUMPAD_ADD: 107,
		NUMPAD_DECIMAL: 110,
		NUMPAD_DIVIDE: 111,
		NUMPAD_ENTER: 108,
		NUMPAD_MULTIPLY: 106,
		NUMPAD_SUBTRACT: 109,
		PAGE_DOWN: 34,
		PAGE_UP: 33,
		PERIOD: 190,
		RIGHT: 39,
		SHIFT: 16,
		SPACE: 32,
		TAB: 9,
		UP: 38,
		WINDOWS: 91 // COMMAND
};
/**
 * 异步提交时候button的提示信息
 * 做成：刘慎宝
 * 用法：
 * 显示等待消息：$(this).showRuningMsg();
 * 取消等待消息：$(this).removeRuningMsg();
 */
;(function($){
	$.fn.showRuningMsg = function(){
		return this.each(function(){
			var thiz = $(this);
			var width = parseInt(thiz.width());
			var height = thiz.height();
			var top = height/4;
			width = width > 60 ? width : 60;
			var refSpan = $("<span class='redText loading'>提交中</span>").css({
				'width':width+"px",
				'padding-top':top+"px",
				'height':height+"px"
			});
			if(!thiz.is("span")){
				refSpan.css({
					'display':"-moz-inline-box",
					'display':"inline-block"
				});
			}
			refSpan.insertAfter(thiz);
			thiz.data("refSpan",refSpan);
			thiz.hide();
		});
	};
	$.fn.removeRuningMsg = function(){
		return this.each(function(){
			$(this).show().data("refSpan").remove();
		});
	};
})(jQuery);
/**
 * 自定义jqueryForm校验插件
 * 做成：刘慎宝
 * 注意事项：每个要校验的dom对象必须设置name属性,且不隐藏
 * 使用样例
 * $("#formId").valilater();
 * 支持自定义设置
 * $("#formId").valilater({
 * 		alertMsg:true,//支持弹出alert提示方式
 *      checkHiddenDom:false,//是否校验隐藏的dom插件
 * 		checkPassPro:"checkPass",//自定义属性名
 * 		refTextPro:"refText",//自定义属性名
 * 		refTypePro:"refType",//自定义属性名
 * 		highlightPro:"warning",//自定义高亮提示CSS类名
 * 		warningTextPro:"warningText",//自定义高亮提示文本CSS类名
 * 		debug:true,//输出debug校验信息(页面底部)
 *      eventTrigger:false//是否失去焦点立即触发
 * });
 * formValiter.check();
 * 执行校验所有form表单[text,radio,select,checkbox](包含非空校验：用在提交form表单时校验)
 * formValiter.checkPassNull();
 * 执行校验所有form表单[text,radio,select,checkbox](不包含非空校验：用在提交查询时校验)
 * formValiter.reInitV();
 * 重新初始化(一般用在隐藏dom对象状态变化[隐藏->显示]后重新初始化)
 * formValiter.resert();
 * 取消提示消息
 * form表单内元素属性列表
 * 如例：
 * 添加checkPass="y"则逃过校验
 * <input type="text" name="checkPassDemo" value="" checkPass="y"/>
 * 添加refText="公司名称"则提示名称以refText为准，如：公司名称不能为空
 * <input type="text" name="refTextDemo" value="" refText="公司名称"/>
 * 验证非空:refType="notNull"
 * <input type="text" name="notNullInputDemo" value="" refType="notNull|int+"/>
 * <select id="workerStatus" name="notNullSelectDemo" refType="notNull" nullVal="0">//select可通过nullVal设置不可选下拉项
 * 添加refType则校验类型
 * <input type="text" name="refTypeDemo" value="w" refType="int+"/>
 * refType种类：notNull、notNull(id1,id2..)、int+、int-、int、maxLength(位数)、length(位数)、length[int/string](位数)、double+(整数位,小数位)、double-(整数位,小数位)、double(整数位,小数位)、自定义类型
 * 自定义类型举例：[{"checkfun":"check1","warnmsg":"长度小于3"},{"regex":"^[0-9]+$","warnmsg":"非数字"}]
 */
;(function($){
	var configdefault = {
		thiz:false,
		checkDoms:[],
		checkPass:true,
		checkHiddenDom:false,
		location:"bottom",//left,bottom,top
		checkPassPro:"checkPass",
		refTextPro:"refText",
		refTypePro:"refType",
		highlightPro:"warning",
		warningTextPro:"warningText",
		alertMsg:false,
		debug:false,
		checkNull:true,
		eventTrigger:false,
		msgRemoveTime:1500
	};
	var notNullRex = /^notNull$/;
	var notNullRefRex = /^notNull\((.+)\)$/;
	var int1Rex = /^int\+$/;
	var int2Rex = /^int\-$/;
	var intRex = /^int$/;
	var double1Rex = /^double\+\(([0-9]+),([0-9]+)\)$/;
	var double2Rex = /^double\-\(([0-9]+),([0-9]+)\)$/;
	var doubleRex = /^double\(([0-9]+),([0-9]+)\)$/;
	var length1Rex = /^length\(([0-9]+)\)$/;
	var lengthRex = /^length\[([a-z]+)\]\(([0-9]+)\)$/;
	var maxLengthRex = /^maxlength\(([0-9]+)\)$/i;
	var msgHtml = '<div class="vFormMsg"><div class="arrow"></div><div class="popover-content">${msg}</div></div>';
	var allType = 'notNull、notNull(id1,id2..)、int+、int-、int、maxLength(位数)、length(位数)、length[int/string](位数)、double+(整数位,小数位)、double-(整数位,小数位)、double(整数位,小数位)、自定义类型';
	
	var showMsg = function(name,errorMag,jTarget,thisdefault){
		var msg;
		if(thisdefault.alertMsg == true){
			jTarget.focus();
			alert(name+errorMag);
			return;
		}
		//若已经有提示则不提示
		if(jTarget.parent().find("."+thisdefault.warningTextPro).size() > 0){
			return;
		}
		var position = jTarget.position(), 
			location = configdefault.location, //left
			left = 0,
			top = 0;
		if(position.left == 0 && position.top == 0){
			//无法定位位置
			alert(name+errorMag);
			return;
		}
		if(location == "bottom"){
			left = position.left;
			top = position.top + jTarget.height() + 10;
		} else if(location == "right"){
			left = position.left + jTarget.width() + 10,
			top = position.top;
		} else {
			//top
			left = position.left,
			top = position.top - 10;
		}
		msg = $(msgHtml.replace("${msg}", errorMag))
			.addClass("bottom")
			.addClass(thisdefault.warningTextPro)
			.css({top: top + "px", left: left + "px"}).on("mouseout", function(){
				$(this).remove();
			}).on("mouseover", function(){
				var closeMsgFunId = $(this).data("closeMsgFunId");
				if(closeMsgFunId){
					clearTimeout(closeMsgFunId);
					$(this).data("closeMsgFunId", null);
				}
			}).data("closeMsgFunId", setTimeout(function(){
				msg.remove();
			}, thisdefault.msgRemoveTime));
		jTarget.addClass(thisdefault.highlightPro).parent().append(msg);
		return;
	};
	var removeMsg = function(thisdefault){
		thisdefault.thiz.find("div."+thisdefault.warningTextPro+"").remove();
		thisdefault.thiz.find("."+thisdefault.highlightPro).removeClass(thisdefault.highlightPro);
	};
	var showNullMsg = function(name,jTarget,thisdefault){
		showMsg(name,"不能为空",jTarget,thisdefault);
	};
	/**
	 * 整数验证(用户单独使用)
	 * @param num：要检验的字符串数字
	 * @param exponent：小数位数
	 * @param maxNum：最大值
	 * @returns
	 */
	var checkInt = function(num,minNum,maxNum){
		var array=new Array('y',
				'非法数字',
				'数字必须为整数',
				'必须小于等于'+maxNum,
				'必须大于等于'+minNum);
		var valid = true;
		var errorTipText = "";
		numStr = num + "";
		if(isNaN(numStr) || ""==numStr){
			errorTipText = array[1];
			valid = false;
		}else if(!/^\-{0,1}[0-9]+$/.test(numStr)){
	    	errorTipText = array[2];
	    	valid = false;
		}else if(parseInt(numStr)<parseInt(minNum)){
	    	errorTipText = array[4];
	    	valid = false;
		}else if(parseInt(numStr)>parseInt(maxNum)){
	    	errorTipText = array[3];
	    	valid = false;
	    }
		if(valid == true){
	    	errorTipText = array[0];
	    }
		return errorTipText;
	};
	/**
	 * 金额验证(用户单独使用)
	 * @param numStr：要检验的字符串数字
	 * @param exponent：小数位数
	 * @param maxNum：最大值
	 * @param onlyPositive：是否仅仅正数
	 * @returns
	 */
	var checkPrice = function(price,exponent,maxNum,onlyPositive){
		var array=new Array('y',
				'非法数字',
				'保留'+exponent+'位小数',
				'必须小于'+maxNum+'',
				'不能为负数或者零');
		var valid = true;
		var errorTipText = "";
		numStr = price + "";
		if(isNaN(numStr) || ""==numStr){
			errorTipText = array[1];
			valid = false;
		}else if(parseFloat(numStr)<=0){
			if(onlyPositive == true){
				errorTipText = array[4];
				valid = false;
			}
		}else if(numStr.indexOf('.') != -1){
			var splitArray=numStr.split('.');
			if(splitArray[1].length>exponent){
				errorTipText = array[2];
				valid = false;
			}
		}
		if(parseFloat(numStr) > parseFloat(maxNum)){
	    	errorTipText = array[3];
	    	valid = false;
	    }
		if(valid == true){
	    	errorTipText = array[0];
	    }
		return errorTipText;
	};
	/**
	 * 长度验证
	 * @param value：要检验的字符串
	 * @param type：类型只能int或者string
	 * @param length：长度
	 * @returns
	 */
	var checkLength = function(value,type,length){
		var array=new Array('y',
				'非法数字',
				'长度必须等于'+length+'位',
				'长度必须等于'+length+'位[半角](1全角=2半角)');
		var valid = true;
		var errorTipText = "";
		numStr = value + "";
		length = parseInt(length);
		if(type == 'int'){
			var rex = new RegExp("^[0-9]+$");
			if(rex.test(value) == false){
				errorTipText = array[1];
				valid = false;
			}else if(value.length != length){
				errorTipText = array[2];
				valid = false;
			}
		}else if(type == 'string'){
			 var cArr = value.match(/[^\x00-\xff]/ig);   
			 var strLength = value.length + (cArr == null ? 0 : cArr.length);   
			 if(strLength != length){
				 errorTipText = array[3];
				 valid = false;
			 }
		}else{
			throw new Error("refType='length[int/string](3)'：type must [int] or [string]");
		}
		if(valid == true){
	    	errorTipText = array[0];
	    }
		return errorTipText;
	};
	/**
	 * 最大长度验证
	 * @param value：要检验的字符串
	 * @param length：长度
	 * @returns
	 */
	var checkMaxLength = function(value,length){
		var array=new Array('y','长度必须小于'+length+'位[半角](1全角=2半角)');
		var valid = true;
		var errorTipText = "";
		value = value + "";
		length = parseInt(length);
		var cArr = value.match(/[^\x00-\xff]/ig);   
		var strLength = value.length + (cArr == null ? 0 : cArr.length);   
		if(strLength > length){
			errorTipText = array[1];
			valid = false;
		}
		if(valid == true){
	    	errorTipText = array[0];
	    }
		return errorTipText;
	};
	var isCustCheck = function(type){
		try{
			var custCheckObjs = $.parseJSON(type.replace(/'/g,"\""));
			if(true == $.isArray(custCheckObjs)){
				for(var i = 0; i < custCheckObjs.length; i++){
					if(typeof custCheckObjs[i]["warnmsg"] != "string"){
						return false;
					}
					//回调函数或者正则表达式
					var regex = custCheckObjs[i]["regex"];
					var checkfun = null;
					try{
						checkfun = eval(custCheckObjs[i]["checkfun"]);
					}catch(e){
						alert("checkfun["+custCheckObjs[i]["checkfun"]+"] must be defined");
						return false;
					}
					var checkfun = eval(custCheckObjs[i]["checkfun"]);
					if(typeof regex == "string"){
						var re = new RegExp(regex,"g");
					}else if(false == $.isFunction(checkfun)){
						return false;
					}
					if(typeof checkfun == "string" && false == $.isFunction(checkfun)){
						alert("regex must be function/regex");
						return false;
					}
				}
			}else{
				return false;
			}
			return true;
		}catch(e){
			return false;
		}
	};
	var custCheck = function(type,jTarget){
		var custCheckObjs = $.parseJSON(type.replace(/'/g,"\""));
		for(var i = 0; i < custCheckObjs.length; i++){
			var checkResult = true;
			//回调函数或者正则表达式
			var regex = custCheckObjs[i]["regex"];
			if(typeof regex == "string"){//正则表达式
				checkResult = (new RegExp(regex,"g")).test(jTarget.val());
			}
			var checkfun = eval(custCheckObjs[i]["checkfun"]);
			if(checkResult == true && $.isFunction(checkfun)){//回调函数调用
				checkResult = checkfun(jTarget);
			}
			if(false == checkResult){
				return custCheckObjs[i]["warnmsg"];
			}
		}
		return 'y';
	};
	var isNull = function(jTarget, thisdefault){
		var isBlack = false, nullVal;
		if(jTarget.is("input[type='radio'],input[type='checkbox']")){
			if(thisdefault.thiz.find("[name='"+jTarget.attr("name")+"']").filter(":checked").size() == 0){
				isBlack = true;
			}
		}else if(jTarget.is("select")){
			nullVal = jTarget.attr("nullVal");
			if(jTarget.val() == (nullVal ? nullVal : "")){
				isBlack = true;
			}
		}else{//type=file,type=text
			if(jTarget.val() == ""){
				isBlack = true;
			}
		}
		return isBlack;
	};
	/**
	 * 非空【notNull】
	 * @param value：要检验的字符串
	 * @param length：长度
	 * @returns
	 */
	var notNull = function(jTarget, value, thisdefault){
		var array=new Array('y','不能为空');
		if(isNull(jTarget, thisdefault)){
			return array[1];
		} else {
			return array[0];
		}
	};
	/**
	 * 非空【notNullRef】:其中一个不为空即可
	 * @returns
	 */
	var notNullRef = function(jTarget, ids, thisdefault){
		var i,valid = false,array = new Array('y','不能为空');
		for(i = 0; i < ids.length; i++){
			if(isNull($("#"+ids[i]), thisdefault) == false){
				valid = true;
				continue;
			}
		}
		if(valid){
			return array[0];
		} else {
			return array[1];
		}
	};
	var checkType = function(name,jTarget,thisdefault){
		var utype = jTarget.attr(thisdefault.refTypePro);
		if(!utype){
			return true;
		}
		var types = utype.split("|");
		var type = "";
		var value = jTarget.val();
		var msg = 'y';
		var tmp = [];
		var i = 0;
		var debugMsg;
		for(i = 0; i < types.length; i++){
			type = types[i];
			if((tmp = notNullRex.exec(type)) != null){//非空【notNull】
				msg = notNull(jTarget, value, thisdefault);
			}else if((tmp = notNullRefRex.exec(type)) != null){//非空【notNull(id1,id2)】
				msg = notNullRef(jTarget, tmp[1].split(","), thisdefault);
			} else if(value){
				if((tmp = int1Rex.exec(type)) != null){//正整数【int+】
					msg = checkInt(value,1,2147483647);
				}else if((tmp = int2Rex.exec(type)) != null){//负整数【int-】
					msg = checkInt(value,-2147483647,-1);
				}else if((tmp = intRex.exec(type)) != null){//整数【int】
					msg = checkInt(value,-2147483647,2147483647);
				}else if((tmp = double1Rex.exec(type)) != null){ //double类型【double+(1,3)】
					msg = checkPrice(value,tmp[2],Math.pow(10,parseInt(tmp[1])),true);
				}else if((tmp = double2Rex.exec(type)) != null){ //double类型【double-(1,3)】
					msg = checkPrice(value,tmp[2],0,false);
				}else if((tmp = doubleRex.exec(type)) != null){ //double类型【double(1,3)】
					msg = checkPrice(value,tmp[2],Math.pow(10,parseInt(tmp[1])),false);
				}else if((tmp = length1Rex.exec(type)) != null){//定长类型【length(3)】
					msg = checkLength(value,'int',tmp[1]);
				}else if((tmp = lengthRex.exec(type)) != null){//定长类型【length[int/string](3)】
					msg = checkLength(value,tmp[1],tmp[2]);
				}else if((tmp = maxLengthRex.exec(type)) != null){//定长类型【maxLength[int/string](3)】
					msg = checkMaxLength(value,tmp[1]);
				}else if(isCustCheck(utype) == true){//自定义类型【[{checkfun:function,warnmsg:''},{checkfun:/regex/,warnmsg:''}]】
					msg = custCheck(utype,jTarget);
				}else{
					throw new Error("dom对象name["+jTarget.attr("name")+"]的"+thisdefault.refTypePro+"属性["+type+"]配置错误，请从以下样式选择"+allType);
				}
			}
			if(thisdefault.debug == true){
				showHtml(jTarget, "[{_jobjectText}]["+thisdefault.refTypePro+"="+type+"]验证结果:"+msg);
			}
			if(msg != 'y'){
				thisdefault.checkPass = false;
				showMsg(name,msg,jTarget,thisdefault);
				return false;
			}
		}
		return thisdefault.checkPass;
	};
	var check = function(thisdefault){
		thisdefault.checkPass = true;
		var checkDoms = thisdefault.checkDoms;
		var refText = null;
		var type = null;
		var jobject = null;
		var pass = true;
		var continueCheck = thisdefault.alertMsg === false;//是否继续校验【提示方式采用alert方式，则采取一个个校验的方式】
		for(var i = 0; i < checkDoms.length; i++){
			pass = true;
			jobject = checkDoms[i];
			refText = jobject.attr(thisdefault.refTextPro);
			refText = refText == undefined ? "" : refText;
			if(thisdefault.checkNull && isNull(jobject, thisdefault)){
				pass = false;
				thisdefault.checkPass = false;
				showNullMsg(name,jobject,thisdefault);
				if(thisdefault.debug == true){
					showHtml(jobject, "[{_jobjectText}][非空]验证结果:"+pass);
				}
			}
			if(pass){
				pass = checkType(refText,jobject,thisdefault);
			}
			if(pass === false && continueCheck === false){
				break;
			}
		}
		return thisdefault.checkPass;
	};
	var exe = function(thisdefault){
		removeMsg(thisdefault);
		return check(thisdefault);
	};
	var bindEvent = function(event){
		exe(event.data["thisdefault"]);
	};
	var html2 = function(text){
		return text.replace(/\n|	/g, "").replace(/\</g, "&lt;").replace(/\>/g, "&gt;");
	};
	var showHtml = function(jobject, text){
		var cHtmlArray = new Array(), debugMsg;
		if(jQuery.isArray(jobject)){
			for(var i = 0; i < jobject.length; i++){
				var tmpDiv = $("<div/>");
				cHtmlArray.push(tmpDiv.append(jobject[i].clone()).html());
				tmpDiv.remove();
			}
		}else{
			var tmpDiv = $("<div/>");
			cHtmlArray.push(tmpDiv.append(jobject.clone()).html());
			tmpDiv.remove();
		}
		debugMsg = text.replace("{_jobjectText}", cHtmlArray.join("").replace(/\n|	/g, ""));
		if(console){
			console.log(debugMsg);
		} else {
			$(document.body).append(html2(debugMsg)).append("<br/>");
		}
	};
	var init = function(thisdefault){
		var debugMsg, inputs = thisdefault.thiz.find("input[type='text'],input[type='radio'],input[type='checkbox'],input[type='file'],select,textarea");
		thisdefault.checkDoms = [];
		for(var i = 0; i < inputs.length; i++){
			var jobject = $(inputs[i]);
			var name = jobject.attr("name");
			var pass = jobject.attr(thisdefault.checkPassPro) == "y" ? true : false;
			var hide = false;
			if(thisdefault.checkHiddenDom === false){
				hide == jobject.is(":hidden");
			}
			if(name && pass === false && hide === false){
				thisdefault.checkDoms.push(jobject);
				if(thisdefault.debug == true){
					showHtml(jobject, "校验对象[{_jobjectText}]已添加");
				}
			}else{
				if(thisdefault.debug == true){
					showHtml(jobject, "校验对象[{_jobjectText}]没有添加，原因:checkPass='true':["+pass+"]/hide:["+hide+"]/name:["+name+"]");
				}
			}
		}
		if(thisdefault.eventTrigger == true){
			inputs.unbind("focusout",bindEvent);
			inputs.bind("focusout",{"thisdefault":thisdefault},bindEvent);
		}
	};
	$.fn.valilater = function(config){
		var thisdefault = {};
		$.extend(true,thisdefault,configdefault,config);
		thisdefault.thiz = $(this);
		//添加可执行方法
		this.resert = function(){ /**取消提示消息*/
			removeMsg(thisdefault);
			return thisdefault.thiz;
		};
		this.reInitV = function(){ /**重新初始化*/
			this.resert();
			init(thisdefault);
			return thisdefault.thiz;
		};
		this.checkPassNull = function(){ /**执行校验(跳过空值)*/
			this.reInitV();
			thisdefault.checkNull = false;
			return exe(thisdefault);
		},
		this.check = function(){ /**执行校验*/
			this.reInitV();
			thisdefault.checkNull = true;
			return exe(thisdefault);
		};
		return this.each(function(){
			//初始化
			init(thisdefault);
		});
	};
})(jQuery);
/**
 * 自定义jquery下载插件
 * 做成：刘慎宝
 * 如例：
 * //导出Excel
 *$("#btnOutExcel").pagedownload({
 *	onePageNum:$pageInfo.pageSize*10,
 *	totalNum:$pageInfo.totalItem,
 *	dateUrl:"paySheetsToExcel.action",
 *	contentId:"formId"//form的id属性
 *});
 */
;(function($){
	var configdefault = {
			thiz:false,
			showDialog:false,
			downloadiframe:false,
			title:"数据导出",
			autoOpen:false,
			width:300,
			height:400,
			contentId:false,
			onePageNum:2000,
			maxNum:500000,
			totalNum:0,
			getToatlNumUrl:"",
			dateUrl:"",
			paraDate:{},
			pagePara:"page",
			pageSizePara:"pageSize"
	};
	var resetparaObject = function(returnObject){
		var resetpara = {};
		$.extend(resetpara,returnObject);
		for(var index in resetpara){
			if(resetpara[index] != resetpara){
				resetpara[index] = "";
			}
		}
	    return resetpara;
	};
	var initDate = function(thisdefault){
		var ul = thisdefault.showDialog.children("ul");
		ul.html("");
		var tempArray = new Array();
		var endNum = 0;
		for(var i = 1; i <= thisdefault.totalNum; i = i + thisdefault.onePageNum){
			thisdefault.paraDate[thisdefault.pagePara] = (i+thisdefault.onePageNum-1)/thisdefault.onePageNum;
			thisdefault.paraDate[thisdefault.pageSizePara] = thisdefault.onePageNum;
			if((i + thisdefault.onePageNum) < thisdefault.totalNum){
				endNum = (i + thisdefault.onePageNum-1);
			}else{
				endNum = thisdefault.totalNum;
			}
			tempArray.push("<li refhref='"+thisdefault.dateUrl+"?"+$.param(thisdefault.paraDate)+"'>"+"下载>" + (i) + "-" + endNum + "</li>");
			if(i > thisdefault.maxNum){
				//若大于则退出
				break;
			}
		}
		ul.append(tempArray.join(""));
	};
	var init = function(thisdefault){
		var thiz = thisdefault.thiz;
		var thisCallback = thisdefault.thisCallback;
		if( thisdefault.showDialog === false ){
			var showDialog = thisdefault.showDialog = $('<div class="jd_showDialog"><ul class="pagedownload"></ul></div>');
//			var iframe = thisdefault.downloadiframe = document.createElement("iframe");
//			$(iframe).appendTo(document.body);
			thisdefault.showDialog.appendTo(document.body);
			showDialog.dialog({
				title:thisdefault.title,
				autoOpen: thisdefault.autoOpen,
				width: thisdefault.width,
				height:thisdefault.height,
				modal: true,
				buttons: {
					'导出': function() {
						var sl = thisdefault.showDialog.find("li[class='selected']");
						if(sl.size() > 0){
							window.location.href=thisdefault.showDialog.find("li[class='selected']").attr("refhref");
						}else{
							alert("请先选择打印范围记录数");
						}
					},'关闭': function() {
						$(this).dialog("close");
					}
				},
				close: function() {
				}
			});
			var ul = showDialog.children("ul");
			ul.delegate("li", "mouseover", function(event){
				$(this).addClass("activit");
			});
			ul.delegate("li", "mouseout", function(event){
				$(this).removeClass("activit");
			});
			ul.delegate("li", "click", function(event){
//				thisdefault.downloadiframe.src = $(this).attr("refhref");
//				window.location.href=$(this).attr("refhref");
				thisdefault.showDialog.find("li").removeClass("selected");
				$(this).addClass("selected");
			});
//			$(thisdefault.downloadiframe.window).bind("load",function(){
//				alert("加载完毕");
//			});
		}
		var inputs = null;
		if(false === thisdefault.contentId){
			inputs = $(document).find("input,select");
		}else{
			inputs = $("#"+thisdefault.contentId).find("input,select");
		}
		var jobject = null;
		for(var index = 0; index < inputs.length; index++){
			jobject = $(inputs[index]);
			var name = jobject.attr("name");
			var value = jobject.val();
			if(name && value){
				if(jobject.is("input[type='radio']:checked")){
					thisdefault.paraDate[name] = value;
				}else if(jobject.is("input[type='checkbox']:checked")){
					if(thisdefault.paraDate[name]){
						thisdefault.paraDate[name] = thisdefault.paraDate[name] + "," + value;
					}else{
						thisdefault.paraDate[name] = value;
					}
				}else if(jobject.is("input[type='text'],input[type='file'],input[type='hidden'],select")){
					thisdefault.paraDate[name] = value;
				}
			}
		}
		if(thisdefault.autoOpen === true){
			//同步显示的时候
			initDate(thisdefault);
		}else{
			thiz.bind('click',function(e){
				initDate(thisdefault);
				thisdefault.showDialog.dialog("open");
				return false;
			});
		}
	};
	$.fn.pagedownload = function(config){
		var thisdefault = $.extend({},configdefault,config);
		var thiz = thisdefault.thiz = $(this);
		this.setTotalNum = function(totalNum){ /**执行校验*/
			thisdefault.totalNum = totalNum;
			init(thisdefault);
			return thiz;
		};
		return this.each(function(){
			init(thisdefault);
		});
	};
})(jQuery);
/**
 * 锁定标题表头不随table变化
 * 使用方式请参照posrelatedlist.vm
 */
(function($) {	
	var getCss = function(tr){
		var position = tr.position();
		var offset = tr.offset();
		return {"height":tr.height(),
			"width":tr.width(),
			"left":position.left,
			"top":position.top,
			"oLeft":offset.left,
			"oTop":offset.top};
	};
	var getHeaderHtml = function(tr){
		var tmpArray = new Array();
		var tds = tr.find("td,th");
		for(var i = 0; i < tds.length; i++){
			var td = $(tds[i]);
			var position = td.position();
			tmpArray.push("<div style='left:"+position.left+"px;width:"+(td.outerWidth()-2)+"px'>");
			tmpArray.push(td.html());
			tmpArray.push("</div>");
		}
		return tmpArray.join("");
	};
	$.fn.floatNav = function(c) {
		var d = $.extend({
			start : null,
			end : null,
			fixedClass : "nav-fixed",
			targetEle : null,
			range : 0,
			onStart : function() {
			},
			onEnd : function() {
			}
		}, c);
		var show = false;
		var thiz = $(this);
		var tr = thiz.find("tr").first();
		var g = $('<div class="nav-fixed-title"/>'), css = getCss(tr);
		g.html(getHeaderHtml(tr)).css({"height":css.height,"width":(thiz.outerWidth()+3),"top":0,"left":css.left,"position":"absolute"}).hide();
		$(document.body).append(g);
		$(window).bind("scroll",function() {
			var i = $(document).scrollTop(),
				j = $(document).scrollLeft(), 
				l = d.start || thiz.offset().top,
				k = d.targetEle ? $(d.targetEle).offset().top : 100000;
			if (i > l && i < (d.end || k) - d.range) {
				g.css({"top":i,"left":getCss(thiz).left});
				if(!show){
					g.show();
					show = true;
				}
				if (d.onStart) {
					d.onStart();
				}
			} else {
				if(show){
					g.hide();
					show = false;
				}
				if (d.onEnd) {
					d.onEnd();
				}
			}
		});
		return this;
	};
})(jQuery);
/**
 * 自定义jquery下拉列表筛选插件
 * 做成：刘慎宝
 * 如例：
 * //导出Excel
 *$("#btnOutExcel").selectFilter();
 */
;(function($){
	var configdefault = {
			thiz:false,
			showDialog:false,
			title:"下拉列表筛选",
			autoOpen:false,
			width:300,
			height:400,
			paraDate:{},
			filterOldText:"",
			btnHtml:"<input type='button' value='筛选' />",
			filterText:{}
	};
	var filter = function(thisdefault){
		var text = thisdefault.filterText.val().trim();
		var lis = thisdefault.showDialog.find("ul li");
		if(text == ''){
			lis.show();
		}else if(text !== thisdefault.filterOldText){
			for(var i = 0; i < lis.length; i++){
				var li = $(lis[i]);
				if(li.text().indexOf(text) != -1 || li.attr("refValue").indexOf(text) != -1){
					li.show();
				}else{
					li.hide();
				}
			}
			thisdefault.filterOldText = text;
			thisdefault.showDialog.find("ul > li").removeClass("activit").filter(":visible").eq(0).addClass("activit");
		}
	};
	var initDate = function(thisdefault){
		var thiz = thisdefault.thiz;
		var options = thiz.find("option");
		var jobject = null;
		var ul = thisdefault.showDialog.find("ul[class='selectList']");
		ul.html("");
		for(var index = 0; index < options.length; index++){
			jobject = $(options[index]);
			var text = jobject.attr("text") || jobject.text();
			var value = jobject.attr("value") || text;
			if(text && value){
				ul.append($("<li/>").text(text).attr("refValue", value).attr("title", text + "[" + value + "]"));
			}
		}
		thisdefault.filterOldText = "";
		filter(thisdefault);
	};
	var select = function(thisdefault){
		var thiz = thisdefault.thiz;
		var sl = thisdefault.showDialog.find("li[class='activit']");
		if(sl.size() > 0){
			thiz.val(sl.attr("refValue"));
			thiz.trigger("change");
		}
		thisdefault.showDialog.dialog("close");
	};
	var init = function(thisdefault){
		var thiz = thisdefault.thiz;
		if( thisdefault.showDialog === false ){
			var showDialog = thisdefault.showDialog = $('<div class="jd_showDialog"><div class="selectListTitle"><input type="text" /></div><div class="selectListContent"><ul class="selectList"></ul></div></div>');
			thisdefault.showDialog.appendTo(document.body);
			showDialog.dialog({
				title:thisdefault.title,
				autoOpen: thisdefault.autoOpen,
				width: thisdefault.width,
				height:thisdefault.height + 20,
				modal: false,
				resizable: false,
				close: function() {}
			});
			showDialog.find("div[class='selectListContent']").css({"height":(thisdefault.height-60)+"px","width":(thisdefault.width-30)+"px"});
			var ul = showDialog.find("ul[class='selectList']");
			ul.delegate("li", "mouseover", function(event){
				$(this).addClass("activit");
			});
			ul.delegate("li", "mouseout", function(event){
				$(this).removeClass("activit");
			});
			ul.delegate("li", "click", function(event){
				select(thisdefault);
			});
			thisdefault.filterText = thisdefault.showDialog.find("input[type='text']").bind("keyup", function(event){
				var lilist = thisdefault.showDialog.find("ul > li:visible");
				var activitIndex = -1;
				var size = lilist.size();
				var exit = false;
				for(var i = 0;i<lilist.size();i++){
					if($(lilist[i]).is(".activit")){
						activitIndex = i;
						break;
					}
				}
				var frontIndex = 0;
				var nextIndex = 0;
				if(activitIndex == -1){//没有高亮记录
					activitIndex = 0;
					frontIndex = size-1;
					nextIndex = 0;
				}else{
					frontIndex = activitIndex == 0 ? size-1 : activitIndex-1;
					nextIndex = activitIndex >= (size-1) ? 0 : activitIndex+1;
				}
				switch(event.keyCode ) {
					case keyCode.UP:
						$(lilist[activitIndex]).trigger("mouseout");
						$(lilist[frontIndex]).trigger("mouseover");
						event.preventDefault();
						break;
					case keyCode.DOWN:
						$(lilist[activitIndex]).trigger("mouseout");
						$(lilist[nextIndex]).trigger("mouseover");
						event.preventDefault();
						break;
					case keyCode.ENTER:
					case keyCode.NUMPAD_ENTER:
						select(thisdefault);
						break;
					case keyCode.TAB:
						break;
					case keyCode.ESCAPE:
					default:
						filter(thisdefault);
						break;
				}
			});
		};
		var refbtn = $(thisdefault.btnHtml).bind('click',function(e){
			initDate(thisdefault);
			thisdefault.showDialog.dialog("open");
			return false;
		});
		thiz.after(refbtn);
	};
	$.fn.selectFilter = function(config){
		return this.each(function(){
			var thisdefault = $.extend(true,{},configdefault,config);
			var thiz = thisdefault.thiz = $(this);
			init(thisdefault);
			return thiz;
		});
	};
})(jQuery);
/**
 * table表格tr行可上下拖动
 * $(this).dragTableAble();
 */
;(function($){
	var dragManager = {
		thiz : false,
		exceptRowNum : 0,
		exceptRow : false,
		dragObj : false,
		dragingClass : "draging",
		clientY : 0,
		draging : function(e) {//mousemove时拖动行
			var dragObj = dragManager.dragObj;
			if (dragObj) {
				if (window.getSelection) {//w3c
					window.getSelection().removeAllRanges();
				} else if (document.selection) {
					document.selection.empty();//IE
				}
				var tr = $(e.currentTarget);
				if (!tr.is("tr") || tr.is(dragManager.exceptRow)) {
					return;
				}
				var y = e.clientY;
				var down = y > dragManager.clientY;//是否向下移动
				dragManager.clientY = y;
				if (!dragObj.is(tr)) {
					if(down){
						tr.after(dragObj);
					}else{
						tr.before(dragObj);
					}
				}
			}
		},
		dragStart : function(e) {
			var target = $(e.currentTarget);
			dragManager.exceptRow = dragManager.thiz.find("tr").eq(dragManager.exceptRowNum);
			if (target.is("tr") && !target.is(dragManager.exceptRow)) {
				dragManager.dragObj = target;
				//显示为可移动的状态
				target.addClass(dragManager.dragingClass);
				dragManager.clientY = e.clientY;
				$(document).bind("mouseup", dragManager.dragEnd);
			}
		},
		dragEnd : function(e) {
			if (dragManager.dragObj) {
				dragManager.dragObj.removeClass(dragManager.dragingClass);
				dragManager.dragObj = false;
				$(document).unbind("mouseup", dragManager.dragEnd);
			}
		},
		init : function(thizPara){
			dragManager.thiz = thizPara;
			dragManager.thiz.delegate("tr","mousedown",dragManager.dragStart).delegate("tr","mousemove",dragManager.draging);
		}
	};
	$.fn.dragTableAble = function(config){
		return this.each(function(){
			var thisdefault = $.extend(true,{},dragManager,config);
			thisdefault.init($(this));
			return thisdefault.thiz;
		});
	};
})(jQuery);
/**
 * textarea操作类
 * 做成：刘慎宝
 * 用法：$(this).operator().insert(text);
 */
;(function($){
	var IE = typeof document.selection === 'object';
	//var IE = typeof textarea.selectionStart == 'undefined';
	var check = function(thiz){
		return $(thiz).is("textarea");
	};
	var replace = function(thisz,fromText,toText){
		if(IE){
			thisz.focus();
			var sel = document.selection.createRange();
			sel.text = sel.text.replace(fromText,toText);
		}else{
			var value = thisz.value;
			var len = value.length;
			var start = thisz.selectionStart;
			var end = thisz.selectionEnd;
			var sel = thisz.value.substring(start, end);
			var rep = sel.replace(fromText,toText);
			thisz.value = value.substring(0,start) + rep + value.substring(end,len);
		}
	};
	var selectedReplace = function(thisz,toText){
		if(IE){
			thisz.focus();
			var sel = document.selection.createRange();
			sel.text = toText;
		}else{
			var value = thisz.value;
			var len = value.length;
			var start = thisz.selectionStart;
			var end = thisz.selectionEnd;
			thisz.value = value.substring(0,start) + toText + value.substring(end,len);
		}
	};
	var light = function(thisz,start,end){
		if(IE){
			var range = thisz.createTextRange(); 
			// 先把相对起点移动到0处 
			range.moveStart( "character", 0);
			range.moveEnd( "character", 0); 
			range.collapse( true); // 移动插入光标到start处 
			range.moveEnd( "character", end); 
			range.moveStart( "character", start); 
			range.select(); 
		}else{
			thisz.setSelectionRange(start, end);
		}
		thisz.focus();
	};
	var insert = function(thisz,text){
		if(IE){
			var range = thisz.createTextRange();
			range.text = range.text + text;
		}else{
			var value = thisz.value;
			var len = value.length;
			var start = thisz.selectionStart;
			var end = thisz.selectionEnd;
			var sel = value.substring(start, end);
			var rep = sel + text;
			thisz.value = value.substring(0,start) + rep + value.substring(end,len); 
		}
	};
	$.fn.operator = function(){
		var thisz = this;
		if(!check(thisz)){
			return this;
		}
		this.replace = function(fromText,toText){
			return replace(thisz[0],fromText,toText);
		};
		this.light = function(start,end){
			return light(thisz[0],start,end);
		};
		this.insert = function(text){
			return insert(thisz[0],text);
		};
		this.selectedReplace = function(toText){
			return selectedReplace(thisz[0],toText);
		};
		return this;
	};
})(jQuery);