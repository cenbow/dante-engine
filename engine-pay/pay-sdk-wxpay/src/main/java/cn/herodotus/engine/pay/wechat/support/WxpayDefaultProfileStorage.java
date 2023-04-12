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

package cn.herodotus.engine.pay.wechat.support;

import cn.herodotus.engine.pay.wechat.definition.WxpayProfile;
import cn.herodotus.engine.pay.wechat.definition.WxpayProfileStorage;
import cn.herodotus.engine.pay.wechat.properties.WxpayProperties;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>Description: 微信支付配置，配置文件文件存储 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/1/7 14:57
 */
@Component
public class WxpayDefaultProfileStorage extends WxpayProfileStorage {

    private final WxpayProperties wxpayProperties;

    public WxpayDefaultProfileStorage(WxpayProperties wxpayProperties) {
        this.wxpayProperties = wxpayProperties;
    }

    private WxpayProperties getWxpayProperties() {
        return wxpayProperties;
    }

    @Override
    public WxpayProfile getProfile(String identity) {
        Map<String, WxpayProfile> profiles = getWxpayProperties().getProfiles();
        if (MapUtils.isNotEmpty(profiles)) {
            return profiles.get(identity);
        }

        return null;
    }
}
