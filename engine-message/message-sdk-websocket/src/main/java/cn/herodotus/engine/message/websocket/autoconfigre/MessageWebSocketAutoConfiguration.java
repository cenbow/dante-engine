/*
 * Copyright (c) 2020-2030 ZHENGGENGWEI(码匠君)<herodotus@aliyun.com>
 *
 * Dante Engine licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Dante Engine 采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改 Dante Cloud 源码头部的版权声明。
 * 3.请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 <https://gitee.com/herodotus/dante-engine>
 * 5.在修改包名，模块名称，项目代码等时，请注明软件出处 <https://gitee.com/herodotus/dante-engine>
 * 6.若您的项目无法满足以上几点，可申请商业授权
 */

package cn.herodotus.engine.message.websocket.autoconfigre;

import cn.herodotus.engine.assistant.core.definition.BearerTokenResolver;
import cn.herodotus.engine.message.websocket.annotation.ConditionalOnMultipleWebSocketInstance;
import cn.herodotus.engine.message.websocket.annotation.ConditionalOnSingleWebSocketInstance;
import cn.herodotus.engine.message.websocket.configuration.WebSocketMessageBrokerConfiguration;
import cn.herodotus.engine.message.websocket.definition.WebSocketMessageSender;
import cn.herodotus.engine.message.websocket.domain.WebSocketMessage;
import cn.herodotus.engine.message.websocket.interceptor.WebSocketAuthenticationHandshakeInterceptor;
import cn.herodotus.engine.message.websocket.processor.MultipleInstanceMessageSender;
import cn.herodotus.engine.message.websocket.processor.MultipleInstanceMessageSyncConsumer;
import cn.herodotus.engine.message.websocket.processor.SingleInstanceMessageSender;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.util.function.Consumer;

/**
 * <p>Description: WebSocket 处理器相关配置 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/12/29 15:52
 */
@AutoConfiguration
public class MessageWebSocketAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MessageWebSocketAutoConfiguration.class);

    @PostConstruct
    public void postConstruct() {
        log.debug("[Herodotus] |- Module [Message WebSocket] Auto Configure.");
    }

    @Bean
    public WebSocketAuthenticationHandshakeInterceptor webSocketPrincipalHandshakeHandler(BearerTokenResolver bearerTokenResolver) {
        WebSocketAuthenticationHandshakeInterceptor webSocketAuthenticationHandshakeInterceptor = new WebSocketAuthenticationHandshakeInterceptor(bearerTokenResolver);
        log.trace("[Herodotus] |- Bean [WebSocket Authentication Handshake Interceptor] Auto Configure.");
        return webSocketAuthenticationHandshakeInterceptor;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSingleWebSocketInstance
    static class SingleInstanceConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public WebSocketMessageSender webSocketMessageSender(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
            SingleInstanceMessageSender singleInstanceMessageSender = new SingleInstanceMessageSender(simpMessagingTemplate, simpUserRegistry);
            log.debug("[Herodotus] |- Strategy [Single Instance Web Socket Message Sender] Auto Configure.");
            return singleInstanceMessageSender;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMultipleWebSocketInstance
    static class MultipleInstanceConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public WebSocketMessageSender webSocketMessageSender(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry, StreamBridge streamBridge) {
            MultipleInstanceMessageSender multipleInstanceMessageSender = new MultipleInstanceMessageSender(simpMessagingTemplate, simpUserRegistry, streamBridge);
            log.debug("[Herodotus] |- Strategy [Single Instance Web Socket Message Sender] Auto Configure.");
            return multipleInstanceMessageSender;
        }

        @Bean
        public <T> Consumer<WebSocketMessage<T>> webSocketConsumer(WebSocketMessageSender webSocketMessageSender) {
            MultipleInstanceMessageSyncConsumer<T> multipleInstanceMessageSyncConsumer = new MultipleInstanceMessageSyncConsumer<>(webSocketMessageSender);
            log.trace("[Herodotus] |- Bean [Multiple Instance Message Receiver] Auto Configure.");
            return multipleInstanceMessageSyncConsumer;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Import({
            WebSocketMessageBrokerConfiguration.class,
    })
    @ComponentScan(basePackages = {
            "cn.herodotus.engine.message.websocket.controller",
            "cn.herodotus.engine.message.websocket.listener",
    })
    static class WebSocketConfiguration {

    }
}
