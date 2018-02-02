$(function(){
	$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
		_title: function(title) {
			var $title = this.options.title || '&nbsp;'
			if( ("title_html" in this.options) && this.options.title_html == true )
				title.html($title);
			else title.text($title);
		}
	}));
	Dropzone.autoDiscover = false;
	//给String对象添加trim方法
	String.prototype.trim = function(){ 
		return this.replace(/(^\s*)|(\s*$)/g, ""); 
	};
	String.prototype.ltrim = function(){ 
		return this.replace(/(^\s*)/g, ""); 
	};
	String.prototype.rtrim = function(){ 
		return this.replace(/(\s*$)/g, ""); 
	};
	String.prototype.date = function(){ 
		return this.replace(/T/g, " "); 
	};
	/**  
	 * 时间对象的格式化;  
	 */
	Date.prototype.format = function(format) {   
	    /*  
	     * eg:format="yyyy-MM-dd hh:mm:ss";  
	     */  
	    var o = {   
	        "M+" : this.getMonth() + 1, // month   
	        "d+" : this.getDate(), // day   
	        "h+" : this.getHours(), // hour   
	        "m+" : this.getMinutes(), // minute   
	        "s+" : this.getSeconds(), // second   
	        "q+" : Math.floor((this.getMonth() + 3) / 3), // quarter   
	        "S" : this.getMilliseconds()   
	        // millisecond   
	    };
	    if (/(y+)/.test(format)) {   
	        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4   
	                        - RegExp.$1.length));   
	    };   
	    for (var k in o) {   
	        if (new RegExp("(" + k + ")").test(format)) {   
	            format = format.replace(RegExp.$1, RegExp.$1.length == 1   
	                            ? o[k]   
	                            : ("00" + o[k]).substr(("" + o[k]).length));   
	        }   
	    }   
	    return format;
	};
	//自动添加在每个页面，自动去掉text和texeeare的文本的前后空格
	$(document).delegate("input[type='text'],textarea", "keyup", function(event){
		var value = $(this).val();
		if(/(^\s+)|(\s+$)/g.test(value)){
			$(this).val(value.trim());
		}
	});
	window.gridDateFormate = function (cellvalue, options, rowObject){
		if(isNaN(cellvalue)==false){
			return new Date(cellvalue).format("yyyy-MM-dd hh:mm:ss");
		} else {
			return cellvalue;
		}
	}
	window.gridUnDateFormate = function (cellvalue, options, cell){
		setTimeout(function(){
			$('input[type=text]', cell).click(function(){WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})});
		}, 0);
	}
	window.alert = function (text, time){
		if(!time){
			time = 1000;
		}
		$.gritter.add({
			title: '提示',
			text: text,
			time: time,
			class_name: 'gritter-info gritter-center'
		});
	};
	window.checkGridPara = function (formId){
		var i,refType,val,lab,ins = $("#"+formId).find("input");
		for(i = 0; i < ins.length; i++){
			refType = ins.eq(i).attr("refType");
			val = ins.eq(i).val();
			if(refType && refType == "num"){
				if(isNaN(val)){
					lab = ins.eq(i).prev("label");
					if(lab){
						alert(lab.text()+"请填写数字")
					} else {
						alert("请填写数字");
					}
					return false;
				}
			}
		}
	};
	$.extend($.jgrid.defaults, {
		sortorder:"desc",
		sortname:"id",
		jsonReader:{repeatitems: false, id: "id"},
		viewrecords : true,
		rowNum:20,
		rowList:[20,50,100],
		pager : "#grid-pager",
		altRows: true,
		multiselect: true,
	    multiboxonly: true,
		loadComplete : function() {
			var table = this;
			var replacement = {
				'ui-icon-seek-first' : 'icon-double-angle-left bigger-140',
				'ui-icon-seek-prev' : 'icon-angle-left bigger-140',
				'ui-icon-seek-next' : 'icon-angle-right bigger-140',
				'ui-icon-seek-end' : 'icon-double-angle-right bigger-140'
			};
			$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
				var icon = $(this);
				var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
				if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
			})
		},
		loadError : function(xhr,status,error){
			alert("异常");
		},
		caption: "查询结果",
		autowidth: true
	});
});