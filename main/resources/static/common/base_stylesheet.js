/**
 * Created by paul on 2018/5/24.
 */
var applicaitoncontext = '/blog';
var boostrappath = applicaitoncontext+'/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css';
var dialogcsspath = applicaitoncontext+'/css/dialog.css';
var bootstrap3dialogcsspath = applicaitoncontext+"/plugins/bootstrap3-dialog/css/bootstrap-dialog.css";
var boostraptreeviewpath = applicaitoncontext+"/plugins/bootstrap-treeview/css/bootstrap-treeview.css";

document.write('<link rel="stylesheet" href= '+boostrappath+'></link>');
document.write('<link rel="stylesheet" href= '+dialogcsspath+'></link>');
document.write('<link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">');
document.write('<link rel="stylesheet" href= '+bootstrap3dialogcsspath+'></link>');

//document.write('<link rel="stylesheet" href= '+boostraptreeviewpath+'></link>');