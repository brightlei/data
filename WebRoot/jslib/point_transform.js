/**坐标转换**/
/**
 * WGS-84：是国际标准，GPS坐标（Google Earth使用、或者GPS模块）
 * GCJ-02：中国坐标偏移标准，Google Map、高德、腾讯使用
 * BD-09：百度坐标偏移标准，Baidu Map使用
 */
var PointConvert={
	PI : 3.1415926535897932384626,
	x_PI : 3.14159265358979324 * 3000.0 / 180.0,
	a : 6378245.0,//卫星椭球坐标投影到平面地图坐标系的投影因子。
	ee : 0.00669342162296594323,//椭球的偏心率。
	bd09togcj02:function(bd_lon,bd_lat){//百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换[即 百度 转 谷歌、高德]
	    var x = bd_lon - 0.0065;
	    var y = bd_lat - 0.006;
	    var z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * this.x_PI);
	    var theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * this.x_PI);
	    var gg_lng = z * Math.cos(theta);
	    var gg_lat = z * Math.sin(theta);
	    return {lon:gg_lng,lat:gg_lat};
	},
	gcj02tobd09:function(lon,lat){//火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换[即谷歌、高德 转 百度]
	    var z = Math.sqrt(lon * lon + lat * lat) + 0.00002 * Math.sin(lat * this.x_PI);
	    var theta = Math.atan2(lat, lon) + 0.000003 * Math.cos(lon * this.x_PI);
	    var bd_lng = z * Math.cos(theta) + 0.0065;
	    var bd_lat = z * Math.sin(theta) + 0.006;
	    return {lon:bd_lng,lat:bd_lat};
	},
	wgs84togcj02:function(lon,lat) {//国际标准GPS(WGS84) 转 火星坐标系 (GCJ-02)[即GPS坐标转谷歌、高德]
		if (this.out_of_china(lon, lat)) {
	        return {lon:lon,lat:lat};
	    }
	    else {
	        var dlat = this.transformlat(lon - 105.0, lat - 35.0);
	        var dlng = this.transformlng(lon - 105.0, lat - 35.0);
	        var radlat = lat / 180.0 * this.PI;
	        var magic = Math.sin(radlat);
	        magic = 1 - this.ee * magic * magic;
	        var sqrtmagic = Math.sqrt(magic);
	        dlat = (dlat * 180.0) / ((this.a * (1 - this.ee)) / (magic * sqrtmagic) * this.PI);
	        dlng = (dlng * 180.0) / (this.a / sqrtmagic * Math.cos(radlat) * this.PI);
	        var mglat = lat + dlat;
	        var mglng = lon + dlng;
	        return {lon:mglng,lat:mglat};
	    }
	},
	gcj02towgs84:function(lon,lat){//火星坐标系 (GCJ-02)转国际标准(WGS84)GPS坐标[即谷歌、高德转GPS坐标]
		if (this.out_of_china(lon, lat)) {
	        return {lon:lon,lat:lat};
	    }
	    else {
	        var dlat = this.transformlat(lon - 105.0, lat - 35.0);
	        var dlng = this.transformlng(lon - 105.0, lat - 35.0);
	        var radlat = lat / 180.0 * this.PI;
	        var magic = Math.sin(radlat);
	        magic = 1 - this.ee * magic * magic;
	        var sqrtmagic = Math.sqrt(magic);
	        dlat = (dlat * 180.0) / ((this.a * (1 - this.ee)) / (magic * sqrtmagic) * this.PI);
	        dlng = (dlng * 180.0) / (this.a / sqrtmagic * Math.cos(radlat) * this.PI);
	        var mglat = lat + dlat;
	        var mglng = lon + dlng;
	        var newlon = lon * 2 - mglng;
		    var newlat = lat * 2 - mglat;
	        return {lon:newlon,lat:newlat};
	    }
	},
	bd09towgs84:function(lon,lat){//百度坐标系 (BD-09)转国际标准(WGS84)GPS坐标[即 百度 转 GPS坐标]
		var coord = {
			lon:lon,
			lat:lat
		};
		if (this.out_of_china(lon, lat)) {
	        return coord;
	    }
	    else {
	    	coord = this.bd09togcj02(lon, lat);
	    	var glng = coord.lon;
	        var glat = coord.lat;
	        coord = this.gcj02towgs84(glng, glat);
	        return coord;
	    }
	},
	wgs84tobd09:function(lon,lat){//国际标准GPS(WGS84) 转 百度坐标系 (BD-09)[即GPS坐标 转 百度]
		var coord = {
			lon:lon,
			lat:lat
		};
		if (this.out_of_china(lon, lat)) {
	        return coord;
	    }
	    else {
	    	coord = this.wgs84togcj02(lon, lat);
	    	var glng = coord.lon;
	        var glat = coord.lat;
	        coord = this.gcj02tobd09(glng, glat);
	        return coord;
	    }
	},
	transformlat:function(lon,lat) {
	    var ret = -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * Math.sqrt(Math.abs(lon));
	    ret += (20.0 * Math.sin(6.0 * lon * this.PI) + 20.0 * Math.sin(2.0 * lon * this.PI)) * 2.0 / 3.0;
	    ret += (20.0 * Math.sin(lat * this.PI) + 40.0 * Math.sin(lat / 3.0 * this.PI)) * 2.0 / 3.0;
	    ret += (160.0 * Math.sin(lat / 12.0 * this.PI) + 320 * Math.sin(lat * this.PI / 30.0)) * 2.0 / 3.0;
	    return ret;
	},
	transformlng:function(lon,lat){
		var ret = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * Math.sqrt(Math.abs(lon));
	    ret += (20.0 * Math.sin(6.0 * lon * this.PI) + 20.0 * Math.sin(2.0 * lon * this.PI)) * 2.0 / 3.0;
	    ret += (20.0 * Math.sin(lon * this.PI) + 40.0 * Math.sin(lon / 3.0 * this.PI)) * 2.0 / 3.0;
	    ret += (150.0 * Math.sin(lon / 12.0 * this.PI) + 300.0 * Math.sin(lon / 30.0 * this.PI)) * 2.0 / 3.0;
	    return ret;
	},
	out_of_china:function(lon,lat) {//判断是否在国内，不在国内则不做偏移
	    return (lon < 72.004 || lon > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
	}
};
function test(){
	var lon = 114.413948;
	var lat = 30.470063;
	var coord = PointConvert.bd09togcj02(lon,lat);
	alert("百度坐标["+lon+","+lat+"]转谷歌、高德坐标为["+coord.lon+','+coord.lat+"]")
	coord = PointConvert.gcj02tobd09(lon,lat);
	alert("谷歌、高德坐标["+lon+","+lat+"]转百度坐标为["+coord.lon+','+coord.lat+"]")
	coord = PointConvert.wgs84togcj02(lon,lat);
	alert("国际标准(WGS84)GPS坐标["+lon+","+lat+"]转谷歌、高德坐标为["+coord.lon+','+coord.lat+"]")
	coord = PointConvert.gcj02towgs84(lon,lat);
	alert("谷歌、高德坐标["+lon+","+lat+"]转国际标准(WGS84)GPS坐标为["+coord.lon+','+coord.lat+"]")
	coord = PointConvert.bd09towgs84(lon,lat);
	alert("百度坐标["+lon+","+lat+"]转国际标准(WGS84)GPS坐标为["+coord.lon+','+coord.lat+"]")
	coord = PointConvert.wgs84tobd09(lon,lat);
	alert("国际标准(WGS84)GPS坐标["+lon+","+lat+"]转百度坐标为["+coord.lon+','+coord.lat+"]")
}
//test();