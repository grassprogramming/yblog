/**
 * Created by paul on 2018/5/28.
 */
function GetRootPath() {
    var loc = window.location,
        host = loc.hostname,
        protocol = loc.protocol,
        port = loc.port ? (':' + loc.port) : '';
    var path = location.pathname;

    if (path.indexOf('/') === 0) {
        path = path.substring(1);
    }

    var mypath = '/' + path.split('/')[0];
    path = (mypath != undefined ? mypath : ('/' + loc.pathname.split('/')[1])) + '/';

    var rootPath = protocol + '//' + host + port + path;
    return rootPath;
}