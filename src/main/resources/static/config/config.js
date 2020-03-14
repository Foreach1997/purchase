// 生产静态资源访问
var dev = "http://localhost:8080/"
/**
 * 获取Url
 * @param {Object} name
 */
function getUrlCode(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}