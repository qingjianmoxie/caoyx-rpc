package com.caoyx.rpc.core.spring.autoconfiguration;

import com.caoyx.rpc.core.netty.server.NettyServer;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;
import com.caoyx.rpc.core.spring.provider.CaoyxRpcSpringProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-26 14:33
 */
@Slf4j
@Configuration
public class CaoyxRpcProviderSpringConfiguration {

    @Value("${caoyxRpc.port:1119}")
    private int port;

    @ConditionalOnMissingBean(CaoyxRpcSpringProviderFactory.class)
    @Bean
    public CaoyxRpcSpringProviderFactory caoyxRpcSpringProviderFactory() throws IllegalAccessException, InterruptedException, InstantiationException {
        log.info("caoyxRpcSpringProviderFactory init");
        CaoyxRpcSpringProviderFactory factory = new CaoyxRpcSpringProviderFactory();
        factory.setServerInstance(new NettyServer()); //TODO 扩展
        factory.setSerializerInstance(new JDKSerializerImpl()); //TODO 扩展
        factory.setPort(port);  // todo 服务治理
        factory.init();
        return factory;
    }
}