/**
 * Created by paul on 2018/7/2.
 */
function AddShade() {
    var shade = "<div class='loadingshade' style='z-index: 1040;background:#ffffff;opacity: 0.5'>";
    shade += "<svg version='1.1' id='L4' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' x='0px' y='0px' style='z-index: 1041'";
    shade+=" viewBox='0 0 100 100' enable-background='new 0 0 0 0' xml:space='preserve'>";
    shade+=" <circle fill='#409EFF' stroke='none' cx='6' cy='50' r='6'>";
    shade+=" <animate";
    shade+=" attributeName='opacity'";
    shade+=" dur='1s'";
    shade+=" values='0;1;0'";
    shade+=" repeatCount='indefinite'";
    shade+=" begin='0.1'>  ";
    shade+=" </circle>";

    shade+=" <circle fill='#409EFF' stroke='none' cx='26' cy='50' r='6'>";
    shade+=" <animate";
    shade+=" attributeName='opacity'";
    shade+=" dur='1s'";
    shade+=" values='0;1;0'";
    shade+=" repeatCount='indefinite'";
    shade+=" begin='0.2'/>  ";
    shade+=" </circle>";

    shade+=" <circle fill='#409EFF' stroke='none' cx='46' cy='50' r='6'>";
    shade+=" <animate";
    shade+=" attributeName='opacity'";
    shade+=" dur='1s'";
    shade+=" values='0;1;0'";
    shade+=" repeatCount='indefinite'";
    shade+=" begin='0.3'/>  ";
    shade+=" </circle>";

    shade+= " </svg>";
    shade +=" </div>";
    $(window.top.document.body).append(shade);
}

function RemoveShade() {
    $(".loadingshade").remove();
}