package org.anyin.cn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;


/**
 * Created by Anyin on 2016/1/23.
 */
@SpringBootApplication
public class Application  implements EmbeddedServletContainerCustomizer{

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

    /**
     * 设置端口
     * @param container
     */
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(8090);
    }

}
