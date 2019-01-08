--创建数据字典分类表
create table t_dic(
    id varchar(32) primary key,--主键
	code varchar(50) NOT NULL,--字典编码
	name varchar(100) NOT NULL,--字典名称
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	state int default 1 --状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_dic',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'字典编码',N'user',N'dbo',N'table',N't_dic',N'column',N'code'
exec sp_addextendedproperty N'MS_Description',N'字典名称',N'user',N'dbo',N'table',N't_dic',N'column',N'name'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_dic',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_dic',N'column',N'state'

--创建数据字典明细表
create table t_dicdata(
	id varchar(32) primary key,--主键
	diccode varchar(50) NOT null,--字典编码
    code varchar(50) not null,--字典明细编码
	name varchar(100) not null,--字典明细名称
    rank int default 100,--字典排序
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_dicdata',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'字典编码',N'user',N'dbo',N'table',N't_dicdata',N'column',N'diccode'
exec sp_addextendedproperty N'MS_Description',N'字典明细编码',N'user',N'dbo',N'table',N't_dicdata',N'column',N'code'
exec sp_addextendedproperty N'MS_Description',N'字典明细名称',N'user',N'dbo',N'table',N't_dicdata',N'column',N'name'
exec sp_addextendedproperty N'MS_Description',N'字典排序',N'user',N'dbo',N'table',N't_dicdata',N'column',N'rank'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_dicdata',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_dicdata',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_dicdata',N'column',N'state'

--创建部门信息表
create table t_department (
  id varchar(32) primary key,--主键
  code varchar(20) not null,--部门编号
  name varchar(50) not null,--部门名称
  pcode varchar(20) not null,--父部门编号
  createtime varchar(19) default convert(varchar,getdate(),120),--创建时间
  edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
  state int default 1 --记录状态:0-已删除，1-正常
);
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_department',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'部门编号',N'user',N'dbo',N'table',N't_department',N'column',N'code'
exec sp_addextendedproperty N'MS_Description',N'部门名称',N'user',N'dbo',N'table',N't_department',N'column',N'name'
exec sp_addextendedproperty N'MS_Description',N'父部门编号',N'user',N'dbo',N'table',N't_department',N'column',N'pcode'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_department',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_department',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_department',N'column',N'state'

--创建人员信息表--
create table t_person (
  id varchar(32) primary key,--主键
  name varchar(10) not null,--姓名
  deptcode varchar(20) not null,--部门编号
  diccode varchar not null,--职务字典编号
  telephone varchar(20),--联系电话
  description varhcar(200),--描述信息
  headimg varchar(500),--人员头像图片地址
  createtime varchar(19) default convert(varchar,getdate(),120),--创建时间
  edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
  state int default 1 --记录状态:0-已删除，1-正常
);

--创建用户角色信息表--
create table t_role (
  id int identity primary key,--主键
  code varchar(100) not null,--角色编码
  name varchar(20) not null,--角色名称
  pageright varchar(1000),--页面访问及操作权限编码
  dataright varchar(1000),--数据访问权限编码
  description varchar(2000),--描述信息
  createtime varchar(19) default convert(varchar,getdate(),120),--创建时间
  edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
  state int default 1 --记录状态:0-已删除，1-正常
);
insert into t_role(code,name) values('superadmin','超级管理员');


--创建用户角色页面权限信息表--
create table t_roleright (
  id int identity primary key,--主键
  code varchar(200) not null,--编码
  name varchar(100) not null,--角色名称
  pcode varchar(200) not null,--父编码
  pageurl varchar(2000),--页面地址
  operate varchar(1000),--功能操作方法
  description varchar(2000),--描述信息
  createtime varchar(19) default convert(varchar,getdate(),120),--创建时间
  edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
  state int default 1 --记录状态:0-已删除，1-正常
);
insert into t_rolegight(code,name,pcode) values('system','系统','0');

--创建系统用户数据表--
create table t_user(
	id varchar(32) primary key,--主键
	username varchar(20) not null,--用户名
	userpwd varchar(20) not null,--用户密码
	personid varchar(32) not null,--人员编号
	deptid varchar(50) not null,--部门编号
	longitude decimal(8, 4),--位置信息：经度
	latitude decimal(8, 4),--位置信息：纬度
	address varchar(500),--地理位置
	headimg varchar(500),--用户头像图片地址
	createtime varchar(19) default convert(varchar,getdate(),120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);

--创建图层分类数据表
create table t_layer(
	id varchar(32) primary key,--主键
	code varchar(50) not null,--编码
    name varchar(100) not null,--图层名称
	pcode varchar(50),--父节点编号
    rank int default 100,--排序
	type varchar(50),--类型
	iconurl varchar(500),--图标图片地址
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建图层数据表
create table t_layerdata(
	id varchar(32) primary key,--主键
	layercode varchar(50) not null,--图层编码
    name varchar(100) not null,--图层数据名称
	description varchar(1000),--描述信息
	longitude decimal(8, 4),--位置信息：经度
	latitude decimal(8, 4),--位置信息：纬度
	address varchar(500),--地理位置
    imgurl varchar(500),--平面图片地址
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建固定信息数据表
create table t_fixdata(
	id varchar(32) primary key,--主键
	layercode varchar(50) not null,--图层编码
    name varchar(100) not null,--图层数据名称
	description varchar(2000),--描述信息
	longitude decimal(8, 4),--位置信息：经度
	latitude decimal(8, 4),--位置信息：纬度
	address varchar(500),--地理位置
    imgurl varchar(500),--平面图片地址
    workshop varchar(200),--所属车间
	workarea varchar(500),--所属工区
	mainperson varchar(20),--负责人
	telephone varchar(100),--联系电话
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
--创建人工信息表
create table t_rengongxx(
	id varchar(32) primary key,--主键
	layercode varchar(50) not null,--图层编码
    name varchar(100) not null,--图层数据名称
	description varchar(2000),--描述信息
	longitude decimal(8, 4),--位置信息：经度
	latitude decimal(8, 4),--位置信息：纬度
	address varchar(500),--地理位置
    imgurl varchar(500),--平面图片地址
    workshop varchar(200),--所属车间
	workarea varchar(500),--所属工区
	mainperson varchar(20),--负责人
	telephone varchar(100),--联系电话
	starttime varchar(19) default convert(varchar, getdate(), 120),--开始时间
	endtime varchar(19) default convert(varchar,getdate(),120),--结束时间
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'图层编码',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'layercode'
exec sp_addextendedproperty N'MS_Description',N'图层数据名称',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'name'
exec sp_addextendedproperty N'MS_Description',N'描述信息',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'description'
exec sp_addextendedproperty N'MS_Description',N'经度',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'longitude'
exec sp_addextendedproperty N'MS_Description',N'纬度',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'latitude'
exec sp_addextendedproperty N'MS_Description',N'地理位置',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'address'
exec sp_addextendedproperty N'MS_Description',N'平面图片地址',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'imgurl'
exec sp_addextendedproperty N'MS_Description',N'所属车间',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'workshop'
exec sp_addextendedproperty N'MS_Description',N'所属工区',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'workarea'
exec sp_addextendedproperty N'MS_Description',N'负责人',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'mainperson'
exec sp_addextendedproperty N'MS_Description',N'联系电话',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'telephone'
exec sp_addextendedproperty N'MS_Description',N'开始时间',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'starttime'
exec sp_addextendedproperty N'MS_Description',N'结束时间',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'endtime'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_rengongxx',N'column',N'state'

--创建短期信息数据表
create table t_shortmsg(
	id varchar(32) primary key,--主键
	layercode varchar(50) not null,--图层编码
    make_unit varchar(200),--编制单位
	plan_state varchar(100),--计划状态
	job_number varchar(100),--作业号
	job_date varchar(20),--作业日期
	job_leader varchar(30),--作业负责人
	class_time varchar(50),--班次
	job_type varchar(100),--作业类别
	job_level varchar(100),--作业等级
	line_name varchar(200),--线名
	row_type varchar(100),--行别
	job_location varchar(400),--作业位置
	point_inout varchar(200),--点内点外
	job_project varchar(100),--作业项目
	job_address varchar(1000),--作业地点
	job_timespace varchar(200),--作业时段
	effect_range varchar(500),--影响范围
	upper_track varchar(500),--上道位置
	lower_track varchar(500),--下道位置
	resist_station varchar(500),--登记站
	job_area varchar(500),--作业区段
	staff_count int,--职工数
	contract_worker varchar(50),--劳务工
	daiban_person varchar(50),--带班人
	machine_tool varchar(1000),--机具
	cooperate_unit varchar(1000),--配合单位
	zz_fhr varchar(100),--驻站防护人
	xc_fhr varchar(100),--现场防护人
	yf_fhr varchar(100),--远方防护人
	risk_level varchar(100),--风险等级
	gqlsrs varchar(20),--工区留守人数
	gqzbr varchar(20),--工区值班人
	gqsbr varchar(20),--工区上传人
	gqsbsj varchar(20),--工区上报时间
	cjdkr varchar(20),--车间盯控人
	cjshr varchar(20),--车间审核人
	cjshsbsj varchar(20),--车间审核上报时间
	ddkr varchar(20),--段盯控人
	dshr varchar(20),--段审核人
	dspsj varchar(20),--段审批时间
	longitude decimal(8, 4),--位置信息：经度
	latitude decimal(8, 4),--位置信息：纬度
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'图层编码',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'layercode'
exec sp_addextendedproperty N'MS_Description',N'编制单位',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'make_unit'
exec sp_addextendedproperty N'MS_Description',N'计划状态',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'plan_state'
exec sp_addextendedproperty N'MS_Description',N'作业号',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_number'
exec sp_addextendedproperty N'MS_Description',N'作业日期',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_date'
exec sp_addextendedproperty N'MS_Description',N'作业负责人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_leader'
exec sp_addextendedproperty N'MS_Description',N'班次',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'class_time'
exec sp_addextendedproperty N'MS_Description',N'作业类别',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_type'
exec sp_addextendedproperty N'MS_Description',N'作业等级',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_level'
exec sp_addextendedproperty N'MS_Description',N'线名',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'line_name'
exec sp_addextendedproperty N'MS_Description',N'行别',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'row_type'
exec sp_addextendedproperty N'MS_Description',N'作业位置',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_location'
exec sp_addextendedproperty N'MS_Description',N'点内点外',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'point_inout'
exec sp_addextendedproperty N'MS_Description',N'作业项目',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_project'
exec sp_addextendedproperty N'MS_Description',N'作业地点',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_address'
exec sp_addextendedproperty N'MS_Description',N'作业时段',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_timespace'
exec sp_addextendedproperty N'MS_Description',N'影响范围',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'effect_range'
exec sp_addextendedproperty N'MS_Description',N'上道位置',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'upper_track'
exec sp_addextendedproperty N'MS_Description',N'下道位置',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'lower_track'
exec sp_addextendedproperty N'MS_Description',N'登记站',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'resist_station'
exec sp_addextendedproperty N'MS_Description',N'作业区段',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'job_area'
exec sp_addextendedproperty N'MS_Description',N'职工数',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'staff_count'
exec sp_addextendedproperty N'MS_Description',N'劳务工',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'contract_worker'
exec sp_addextendedproperty N'MS_Description',N'带班人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'daiban_person'
exec sp_addextendedproperty N'MS_Description',N'机具',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'machine_tool'
exec sp_addextendedproperty N'MS_Description',N'配合单位',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'cooperate_unit'
exec sp_addextendedproperty N'MS_Description',N'驻站防护人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'zz_fhr'
exec sp_addextendedproperty N'MS_Description',N'现场防护人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'xc_fhr'
exec sp_addextendedproperty N'MS_Description',N'远方防护人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'yf_fhr'
exec sp_addextendedproperty N'MS_Description',N'风险等级',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'risk_level'
exec sp_addextendedproperty N'MS_Description',N'工区留守人数',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'gqlsrs'
exec sp_addextendedproperty N'MS_Description',N'工区值班人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'gqzbr'
exec sp_addextendedproperty N'MS_Description',N'工区上传人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'gqsbr'
exec sp_addextendedproperty N'MS_Description',N'工区上报时间',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'gqsbsj'
exec sp_addextendedproperty N'MS_Description',N'车间盯控人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'cjdkr'
exec sp_addextendedproperty N'MS_Description',N'车间审核人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'cjshr'
exec sp_addextendedproperty N'MS_Description',N'车间审核上报时间',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'cjshsbsj'
exec sp_addextendedproperty N'MS_Description',N'段盯控人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'ddkr'
exec sp_addextendedproperty N'MS_Description',N'段审核人',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'dshr'
exec sp_addextendedproperty N'MS_Description',N'段审批时间',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'dspsj'
exec sp_addextendedproperty N'MS_Description',N'位置信息：经度',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'longitude'
exec sp_addextendedproperty N'MS_Description',N'位置信息：纬度',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'latitude'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_shortmsg',N'column',N'state'


--创建图层数据图片信息表
create table t_dataimg(
	id varchar(32) primary key,--主键
	dataid varchar(32) not null,--图层数据ID
	name varchar(100),--图片名称
    imgpath varchar(500) not null,--图片路径
	imgwidth int,--图片宽度
	imgheight int,--图片高度
	smallimg varchar(500),--图片缩略图
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建图标数据表
create table t_icon(
	id int identity primary key,--主键
	iconurl varchar(500) not null --图标图片路径
	width int,--图标宽度
	height int --图标高度
)

--创建图片标注点数据表
create table t_imgmark(
	id varchar(32) primary key,--主键
	markno varchar(32) not null,--数据编号
	name varchar(100),--标注名称
	description varchar(500),--描述信息
	username varchar(20) not null,--用户名
	xrate decimal(8, 4),--标注位置X坐标百分比
	yrate decimal(8, 4),--标注位置Y坐标百分比
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建用户登录日志数据表
create table t_user_login(
	id varchar(32) primary key,--主键
	sid varchar(36) not null,--登录会话
	ip varchar(20) not null,--登录IP地址
	username varchar(20) not null,--登录用户
	loginmethod varchar(200),--登录方式,web,mobile
	logintime varchar(19) default convert(varchar, getdate(), 120),--登录时间
	leavetime varchar(19),--离开时间
	state int default 1 --记录状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_user_login',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'会话ID',N'user',N'dbo',N'table',N't_user_login',N'column',N'sid'
exec sp_addextendedproperty N'MS_Description',N'登录IP地址',N'user',N'dbo',N'table',N't_user_login',N'column',N'ip'
exec sp_addextendedproperty N'MS_Description',N'登录用户名',N'user',N'dbo',N'table',N't_user_login',N'column',N'username'
exec sp_addextendedproperty N'MS_Description',N'登录方式,web,mobile',N'user',N'dbo',N'table',N't_user_login',N'column',N'loginmethod'
exec sp_addextendedproperty N'MS_Description',N'登录时间',N'user',N'dbo',N'table',N't_user_login',N'column',N'logintime'
exec sp_addextendedproperty N'MS_Description',N'离开时间',N'user',N'dbo',N'table',N't_user_login',N'column',N'leavetime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_user_login',N'column',N'state'

--创建用户操作日志数据表
create table t_user_oplog(
	id varchar(32) primary key,--主键
	ip varchar(20) not null,--IP地址
	name varchar(100),--业务名称
	username varchar(20) not null,--操作人
	optype varchar(50),--操作类型
	opcontent varchar(2000),--操作内容
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建系统运行日志数据表
create table t_log4j(
	id int identity primary key,--主键
	logLevel varchar(20) not null,--日志级别
	logTime varchar(20) not null,--日志时间
	className varchar(200) not null,--类名
	message varchar(4000),--日志信息
)
---2017年8月6日新增数据表---
create table t_linepoint(
	id varchar(32) primary key,--主键
	lonstr varchar(50),--经度字符串-度分秒
	latstr varchar(50),--纬度字符串-度分秒
	longitude decimal(8, 4),--经度-数值
	latitude decimal(8, 4),--纬度-数值
	mileage decimal(12, 4),--里程
	name varchar(200),--线名
	linenum varchar(100),--线号
	direction varchar(100),--方向
	longchain varchar(200),--长链
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_linepoint',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'度分秒经度',N'user',N'dbo',N'table',N't_linepoint',N'column',N'lonstr'
exec sp_addextendedproperty N'MS_Description',N'度分秒纬度',N'user',N'dbo',N'table',N't_linepoint',N'column',N'latstr'
exec sp_addextendedproperty N'MS_Description',N'数值经度',N'user',N'dbo',N'table',N't_linepoint',N'column',N'longitude'
exec sp_addextendedproperty N'MS_Description',N'数值纬度',N'user',N'dbo',N'table',N't_linepoint',N'column',N'latitude'
exec sp_addextendedproperty N'MS_Description',N'里程',N'user',N'dbo',N'table',N't_linepoint',N'column',N'mileage'
exec sp_addextendedproperty N'MS_Description',N'线名',N'user',N'dbo',N'table',N't_linepoint',N'column',N'name'
exec sp_addextendedproperty N'MS_Description',N'线号',N'user',N'dbo',N'table',N't_linepoint',N'column',N'linenum'
exec sp_addextendedproperty N'MS_Description',N'方向',N'user',N'dbo',N'table',N't_linepoint',N'column',N'direction'
exec sp_addextendedproperty N'MS_Description',N'长链',N'user',N'dbo',N'table',N't_linepoint',N'column',N'longchain'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_linepoint',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_linepoint',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_linepoint',N'column',N'state'

---2017年9月6日新增数据表---
--创建铁路线信息表
create table t_railway(
	id varchar(32) primary key,--主键
	name varchar(200) not null,--线名
	linenum varchar(100) not null,--线号
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_railway',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'线名',N'user',N'dbo',N'table',N't_railway',N'column',N'name'
exec sp_addextendedproperty N'MS_Description',N'线号',N'user',N'dbo',N'table',N't_railway',N'column',N'linenum'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_railway',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_railway',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_railway',N'column',N'state'
---2017年9月6日新增数据表---
--创建铁路线坐标点信息表
create table t_railway_point(
	id varchar(32) primary key not null,--主键
	linenum varchar(100) not null,--线号
	longitude decimal(8, 4),--经度-数值
	latitude decimal(8, 4),--纬度-数值
	createtime varchar(19) default convert(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default convert(varchar,getdate(),120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
Go
exec sp_addextendedproperty N'MS_Description',N'主键',N'user',N'dbo',N'table',N't_railway_point',N'column',N'id'
exec sp_addextendedproperty N'MS_Description',N'线号',N'user',N'dbo',N'table',N't_railway_point',N'column',N'linenum'
exec sp_addextendedproperty N'MS_Description',N'数值经度',N'user',N'dbo',N'table',N't_railway_point',N'column',N'longitude'
exec sp_addextendedproperty N'MS_Description',N'数值纬度',N'user',N'dbo',N'table',N't_railway_point',N'column',N'latitude'
exec sp_addextendedproperty N'MS_Description',N'创建时间',N'user',N'dbo',N'table',N't_railway_point',N'column',N'createtime'
exec sp_addextendedproperty N'MS_Description',N'修改时间',N'user',N'dbo',N'table',N't_railway_point',N'column',N'edittime'
exec sp_addextendedproperty N'MS_Description',N'状态:0-已删除，1-正常',N'user',N'dbo',N'table',N't_railway_point',N'column',N'state'

--创建车间日计划表视图
create view view_shortmsg as select t1.*,t1.job_date+' '+substring(t1.job_timespace,0,6)+':00' as job_starttime,t1.job_date+' '+substring(t1.job_timespace,7,5)+':00' as job_endtime,t2.iconurl,t2.showcolumns from t_shortmsg t1 left join t_layer t2 on t2.tablename='t_shortmsg'
