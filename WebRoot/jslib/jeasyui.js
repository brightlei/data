__CreateJSPath = function (js) {
    var scripts = document.getElementsByTagName("script");
    var path = "";
    for (var i = 0, l = scripts.length; i < l; i++) {
        var src = scripts[i].src;
        if (src.indexOf(js) != -1) {
            var ss = src.split(js);
            path = ss[0];
            break;
        }
    }
    var href = location.href;
    href = href.split("#")[0];
    href = href.split("?")[0];
    var ss = href.split("/");
    ss.length = ss.length - 1;
    href = ss.join("/");
    if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1 && path.indexOf("\/") != 0) {
        path = href + "/" + path;
    }
    return path;
}

var bootPATH = __CreateJSPath("jeasyui.js");
var basePath = bootPATH.substring(0,bootPATH.indexOf("/data")+6);
//debugger
mini_debugger = true;   
//定义全局缓存对象
var cache = {};

//easyui
document.write('<link href="' + bootPATH + 'themes/bootstrap/easyui.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/color.css?t=1" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/icon.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'themes/icons/icon-all.css" rel="stylesheet" type="text/css" />');
document.write('<link href="' + bootPATH + 'zondy.css" rel="stylesheet" type="text/css" />');
document.write('<script src="' + bootPATH + 'jquery.min.js" type="text/javascript"></script>');
document.write('<script src="' + bootPATH + 'jquery.easyui.min.js" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'easyui-lang-zh_CN.js" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'utils.js?t=11" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'jquery.json-2.2.min.js?t=11" type="text/javascript" ></script>');
document.write('<script src="' + bootPATH + 'log.js?t=11" type="text/javascript" ></script>');