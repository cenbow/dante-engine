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

package cn.herodotus.engine.message.websocket.definition;

import cn.herodotus.engine.message.core.exception.IllegalChannelException;
import cn.herodotus.engine.message.core.exception.PrincipalNotFoundException;
import cn.herodotus.engine.message.websocket.domain.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

/**
 * <p>Description: WebSocketMessageSender 抽象实现 </p>
 *
 * @author : gengwei.zheng
 * @date : 2023/9/14 17:22
 */
public abstract class AbstractWebSocketMessageSender implements WebSocketMessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    protected AbstractWebSocketMessageSender(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
    }

    @Override
    public <T> void toAll(String channel, T payload) {
        simpMessagingTemplate.convertAndSend(channel, payload);
    }

    /**
     * 发送给指定用户信息。
     *
     * @param webSocketMessage 发送内容参数实体 {@link WebSocketMessage}
     * @param <T>              指定 payload 类型
     * @throws IllegalChannelException    Web Socket 通道设置错误
     * @throws PrincipalNotFoundException 该服务中无法找到与 identity 对应的用户 Principal
     */
    public <T> void toUser(WebSocketMessage<T> webSocketMessage) throws IllegalChannelException, PrincipalNotFoundException {
        simpMessagingTemplate.convertAndSendToUser(webSocketMessage.getTo(), webSocketMessage.getChannel(), webSocketMessage.getPayload());
    }

    @Override
    public SimpUser getUser(String userId) {
        return simpUserRegistry.getUser(userId);
    }

    @Override
    public SimpMessagingTemplate getSimpMessagingTemplate() {
        return simpMessagingTemplate;
    }
}
