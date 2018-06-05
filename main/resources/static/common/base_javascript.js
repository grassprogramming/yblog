/**
 * Created by paul on 2018/5/24.
 */
//加载js函数
function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
}
function loadjs(url,islocal) {
    if(islocal){
        url += "?version="+uuid() ;
    }
    document.write('<script src= '+url+'></script>');
}

var applicaitoncontext = '/blog';
var jquerypath = applicaitoncontext+'/webjars/jquery/3.1.1/jquery.min.js';
var boostrappath = applicaitoncontext+'/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js';
var pageutiljspath = applicaitoncontext+'/common/pageutil.js';
var datautiljspath = applicaitoncontext+'/common/datautil.js';
var dialogjspath = applicaitoncontext+'/common/dialog.js';
var boostrap3dialogpath = applicaitoncontext+"/plugins/bootstrap3-dialog/js/bootstrap-dialog.js";
var boostraptreeviewpath = applicaitoncontext+"/plugins/bootstrap-treeview/js/bootstrap-treeview.js";
loadjs(jquerypath,true);
loadjs(pageutiljspath,true);
loadjs(datautiljspath,true);
loadjs(jquerypath,true);
//dialog
loadjs(boostrappath,true);
loadjs(boostrap3dialogpath,true);
loadjs(dialogjspath,true);
//vue
loadjs("https://unpkg.com/vue/dist/vue.js",false);
loadjs("https://unpkg.com/element-ui/lib/index.js",false);
//tree
loadjs(boostraptreeviewpath,true);


