package org.anyin.cn.controller;

import org.anyin.cn.service.WebSocketServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Anyin on 2016/1/23.
 */
@Controller
@RequestMapping("/")
public class IndexController implements InitializingBean,DisposableBean{


    @Autowired
    WebSocketServer webSocketServer;

    private Thread thread = new Thread(){
        @Override
        public void run() {
            try {
                webSocketServer.run(8099);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @RequestMapping
    public String index(){
        return "index.html";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
    }
}
