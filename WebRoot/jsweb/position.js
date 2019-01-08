/**
 * 主要脚本方法
 */
$(function(){
	initDataTable();
});
var ztArr = ["在岗","离岗","执勤中"];
function initDataTable(){
	$("#dg").datagrid({
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		pagination:true,
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
			{field:'xm',title:'姓名',width:'80',align:'left'},
			{field:'zc',title:'职称',width:'130',align:'left'},
			{field:'bm',title:'部门',width:'180',align:'left'},
			{field:'zt',title:'状态',width:'80',align:'center',
			formatter:function(value,row,index){
				return '<span style="font-weight:bold;color:blue;">'+ztArr[parseInt(value,10)]+'</span>';
			}}
		]]
	});
	loadData();
}
//加载数据
function loadData(){
	var data = [];
	data.push({xm:"张三丰",zc:"科员",bm:"信息化",zt:"1",lon:113.24,lat:30.23,imgurl:""});
	data.push({xm:"刘德华",zc:"科员",bm:"信息化",zt:"2",lon:113.24,lat:30.23,imgurl:""});
	data.push({xm:"赵大宝",zc:"科员",bm:"信息化",zt:"2",lon:113.24,lat:30.23,imgurl:""});
	data.push({xm:"胡三军",zc:"科员",bm:"信息化",zt:"2",lon:113.24,lat:30.23,imgurl:""});
	data.push({xm:"叶家队",zc:"科员",bm:"信息化",zt:"1",lon:113.24,lat:30.23,imgurl:""});
	data.push({xm:"陈家英",zc:"科员",bm:"信息化",zt:"1",lon:113.24,lat:30.23,imgurl:""});
	$("#dg").datagrid("loadData",data);
}