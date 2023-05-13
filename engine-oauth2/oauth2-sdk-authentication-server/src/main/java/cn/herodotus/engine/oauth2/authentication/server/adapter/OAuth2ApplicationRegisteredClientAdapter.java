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

package cn.herodotus.engine.oauth2.authentication.server.adapter;

import cn.herodotus.engine.assistant.core.enums.Target;
import cn.herodotus.engine.oauth2.authentication.server.entity.OAuth2Application;
import cn.herodotus.engine.oauth2.authentication.server.entity.OAuth2Scope;
import cn.herodotus.engine.oauth2.core.properties.SecurityProperties;
import cn.herodotus.engine.oauth2.data.jpa.definition.AbstractRegisteredClientAdapter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Description: OAuth2Application  </p>
 *
 * @author : gengwei.zheng
 * @date : 2023/5/13 10:34
 */
public class OAuth2ApplicationRegisteredClientAdapter extends AbstractRegisteredClientAdapter<OAuth2Application> {

    private final SecurityProperties securityProperties;

    public OAuth2ApplicationRegisteredClientAdapter(SecurityProperties securityProperties) {
        super();
        this.securityProperties = securityProperties;
    }

    @Override
    public Set<String> getScopes(OAuth2Application details) {
        Set<OAuth2Scope> clientScopes = details.getScopes();
        return clientScopes.stream().map(OAuth2Scope::getScopeCode).collect(Collectors.toSet());
    }

    @Override
    public ClientSettings getClientSettings(OAuth2Application details) {
        ClientSettings.Builder clientSettingsBuilder = ClientSettings.builder();
        clientSettingsBuilder.requireAuthorizationConsent(details.getRequireAuthorizationConsent());
        clientSettingsBuilder.requireProofKey(details.getRequireProofKey());
        if (StringUtils.hasText(details.getJwkSetUrl())) {
            clientSettingsBuilder.jwkSetUrl(details.getJwkSetUrl());
        }
        if (ObjectUtils.isNotEmpty(details.getAuthenticationSigningAlgorithm())) {
            JwsAlgorithm jwsAlgorithm = SignatureAlgorithm.from(details.getAuthenticationSigningAlgorithm().name());
            if (ObjectUtils.isNotEmpty(jwsAlgorithm)) {
                clientSettingsBuilder.tokenEndpointAuthenticationSigningAlgorithm(jwsAlgorithm);
            }
        }
        return clientSettingsBuilder.build();
    }

    @Override
    public TokenSettings getTokenSettings(OAuth2Application details) {
        TokenSettings.Builder tokenSettingsBuilder = TokenSettings.builder();
        tokenSettingsBuilder.authorizationCodeTimeToLive(details.getAuthorizationCodeValidity());
        tokenSettingsBuilder.deviceCodeTimeToLive(details.getDeviceCodeValidity());
        tokenSettingsBuilder.accessTokenTimeToLive(details.getAccessTokenValidity());
        // refreshToken 的有效期
        tokenSettingsBuilder.refreshTokenTimeToLive(details.getRefreshTokenValidity());
        // 是否可重用刷新令牌
        tokenSettingsBuilder.reuseRefreshTokens(details.getReuseRefreshTokens());
        tokenSettingsBuilder.accessTokenFormat(getTokenFormat());
        if (ObjectUtils.isNotEmpty(details.getIdTokenSignatureAlgorithm())) {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.from(details.getIdTokenSignatureAlgorithm().name());
            if (ObjectUtils.isNotEmpty(signatureAlgorithm)) {
                tokenSettingsBuilder.idTokenSignatureAlgorithm(signatureAlgorithm);
            }
        }
        return tokenSettingsBuilder.build();
    }

    private OAuth2TokenFormat getTokenFormat() {
        if (securityProperties.getValidate() == Target.REMOTE) {
            return new OAuth2TokenFormat("reference");
        } else {
            return new OAuth2TokenFormat("self-contained");
        }
    }
}
