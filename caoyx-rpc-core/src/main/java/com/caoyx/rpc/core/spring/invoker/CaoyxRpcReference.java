package com.caoyx.rpc.core.spring.invoker;

import com.caoyx.rpc.core.netty.client.Client;
import com.caoyx.rpc.core.netty.client.NettyClient;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.impl.ZookeeperRegister;
import com.caoyx.rpc.core.serializer.Serializer;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-19 22:14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CaoyxRpcReference {

    Class<? extends Client> client() default NettyClient.class;

    Class<? extends Serializer> serializer() default JDKSerializerImpl.class;

    int version() default 0;

    Class<? extends CaoyxRpcRegister> register() default ZookeeperRegister.class;

    String[] addressList() default "[]";
}