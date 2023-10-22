/*
 * Copyright (c) 2020-2030 郑庚伟 ZHENGGENGWEI (码匠君) (herodotus@aliyun.com & www.herodotus.cn)
 *
 * Dante Engine licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <http://www.gnu.org/licenses/lgpl.html>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.herodotus.engine.data.autoconfigure;

import cn.herodotus.engine.data.core.properties.DataProperties;
import cn.herodotus.engine.data.mybatis.plus.configuration.MybatisPlusConfiguration;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * <p>Description: Data组件自动注入 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/1/19 19:03
 */
@AutoConfiguration
@EnableConfigurationProperties(DataProperties.class)
@Import({
        MybatisPlusConfiguration.class
})
public class DataAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataAutoConfiguration.class);

    @PostConstruct
    public void postConstruct() {
        log.info("[Herodotus] |- Module [Data Starter] Auto Configure.");
    }
}
