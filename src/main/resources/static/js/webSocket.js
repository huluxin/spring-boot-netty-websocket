$(function () {
    //用户ID
    var uid = 0;

    layer.config({
        extend: 'extend/layer.ext.js'
    });

    //发送信息
    $('.chat__input').keypress(function (event) {
        if (event.keyCode == 13) {
            var msg = $('.chat__input').val();
            //自己的消息模板
            var tpl = $('#chat_msgRow_mine_tpl').html();
            var data = {message:msg};
            laytpl(tpl).render(data,function(html){
                $('.chat__messages').append(html);
                var o = {};
                o.to = $('#to').attr('data');
                o.message = msg;
                o.from = uid;
                o.type = 9;
                send(o);
                $('.chat__input').val('')
            });
        }
    });

    //webSocket
    var socket;
    if (!window.WebSocket) {
        window.WebSocket = window.MozWebSocket;
    }
    //打开webSocket链接
    if (window.WebSocket) {
        socket = new WebSocket("ws://localhost:8099/websocket");
        socket.onmessage = function (event) {
            var o = JSON.parse(event.data);
            if(o.type == 1){
                var tpl = $('#friend_list_tpl').html();
                var data = {name: o.message,uid: o.to};
                laytpl(tpl).render(data, function (html) {
                    $('.sidebar-content').append(html);
                });
                uid = o.from;
            }else if(o.type == 2){
                var id = o.message;
                $("#" + id).remove();
            }else{
                var tpl = $('#chat_msgRow_notMine_tpl').html();
                var data = {message: o.message}
                laytpl(tpl).render(data,function(html){
                    $('.chat__messages').append(html);
                });
            }
        };

        //打开socket连接
        socket.onopen = function (event) {
            //layer.msg('打开WebSocket服务正常，浏览器支持WebSocket!');

            //输入昵称
            layer.ready(function(){
                layer.prompt({
                    title:'请输入您的昵称'
                },function(value,index,elem){
                    var o = {};
                    o.type = 1; //1是注册
                    o.message = value;
                    send(o)
                    layer.close(index);
                });
            });
        }
        //关闭socket连接
        socket.onclose = function (event) {
            var o = {};
            o.type = 2;
            o.message = uid;
            send(o);
        }
    } else {
        layer.msg('抱歉，您的浏览器不支持WebSocket协议');
    }

    /**
     * 发送消息
     * @param message
     */
    function send(message) {
        var json = JSON.stringify(message);
        if (!window.WebSocket) {
            return;
        }
        if (socket.readyState == WebSocket.OPEN) {
            socket.send(json);
        } else {
            layer.msg("WebSocket链接没有建立成功");
        }
    }
});
