/*
 * Copyright (c) 2020-2030 郑庚伟 ZHENGGENGWEI (码匠君) (herodotus@aliyun.com & www.herodotus.cn)
 *
 * Dante Engine licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <http://www.gnu.org/licenses/lgpl-3.0.html>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.herodotus.engine.oauth2.resource.autoconfigure;

import cn.herodotus.engine.message.kafka.autoconfigure.MessageKafkaAutoConfiguration;
import cn.herodotus.engine.oauth2.resource.autoconfigure.stream.StreamMessageSendingAdapter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.stream.function.FunctionConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

/**
 * <p>Description: Stream 消息发送适配器配置 </p>
 *
 * @author : gengwei.zheng
 * @date : 2023/10/27 23:25
 */
@AutoConfiguration(after = FunctionConfiguration.class)
@ConditionalOnBean(StreamBridge.class)
public class StreamAdapterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MessageKafkaAutoConfiguration.class);

    @PostConstruct
    public void postConstruct() {
        log.info("[Herodotus] |- Module [Stream Adapter] Auto Configure.");
    }

    @Bean
    public StreamMessageSendingAdapter streamMessageSendingAdapter(StreamBridge streamBridge) {
        StreamMessageSendingAdapter adapter = new StreamMessageSendingAdapter(streamBridge);
        log.trace("[Herodotus] |- Bean [Stream Message Sending Adapter] Auto Configure.");
        return adapter;
    }
}
