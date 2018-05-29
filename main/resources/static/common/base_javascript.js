/**
 * Created by paul on 2018/5/24.
 */
<!-- import Vue before Element -->
var applicaitoncontext = '/blog';
var jquerypath = applicaitoncontext+'/webjars/jquery/3.1.1/jquery.min.js';
var pageutiljspath = applicaitoncontext+'/common/pageutil.js';
var datautiljspath = applicaitoncontext+'/common/datautil.js';
document.write('<script src= '+jquerypath+'></script>');
document.write('<script src= '+pageutiljspath+'></script>');
document.write('<script src= '+datautiljspath+'></script>');
document.write('<script src="https://unpkg.com/vue/dist/vue.js"></script>');
document.write('<script src="https://unpkg.com/element-ui/lib/index.js"></script>');