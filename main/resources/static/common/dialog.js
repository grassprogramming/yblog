/**
 * Created by paul on 2018/5/31.
 */
function OpenDialog(id,title,url,height,width,callback) {
    height += 'px';
    var pagepath = GetRootPath()+url;
    var dialogInstance = new BootstrapDialog({
        onhidden:callback
    });
    dialogInstance.setId(id);
    dialogInstance.setTitle(title);
    dialogInstance.setMessage($('<div><iframe  src="'+pagepath+'" style="width:100%;height:'+height+' "></iframe></div>'));
    //打开窗口页面增加接受消息监听，在关闭事件后传递消息通知父级页面关闭dialog
    window.addEventListener('message', receiveMessage, false);
    function receiveMessage(tag) {
        var tag = tag.toString();
        if(tag='close'){
            dialogInstance.close();
        }
    }
    //如果要改变样式，先调用realize方法
    dialogInstance.realize();
    dialogInstance.getModalBody().css('height',height);
    dialogInstance.getModalBody().css('padding','0');
    dialogInstance.open();
    BootstrapDialog.close
}

function CloseDialog() {
    //$("[role='dialog']",window.parent.top.document).modal('hide');
    window.parent.postMessage('close', '*');
}

