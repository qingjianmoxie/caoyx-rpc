package com.caoyx.rpc.core.spring.provider;

import com.caoyx.rpc.core.provider.CaoyxRpcProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 20:29
 */

public class CaoyxRpcSpringProviderFactory extends CaoyxRpcProviderFactory implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(CaoyxRpcService.class);
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            serviceBeanMap.values().forEach(serviceBean -> {
                CaoyxRpcService caoyxRpcService = serviceBean.getClass().getAnnotation(CaoyxRpcService.class);
                String clazzName = serviceBean.getClass().getInterfaces()[0].getName();
                int version = caoyxRpcService.version();
                addServiceBean(clazzName, version, serviceBean);
            });
        }
    }
}