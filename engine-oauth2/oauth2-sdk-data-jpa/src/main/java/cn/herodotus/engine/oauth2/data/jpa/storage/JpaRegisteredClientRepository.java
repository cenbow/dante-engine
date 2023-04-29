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

package cn.herodotus.engine.oauth2.data.jpa.storage;

import cn.herodotus.engine.oauth2.data.jpa.entity.HerodotusRegisteredClient;
import cn.herodotus.engine.oauth2.data.jpa.jackson2.HerodotusJackson2Module;
import cn.herodotus.engine.oauth2.data.jpa.jackson2.OAuth2JacksonProcessor;
import cn.herodotus.engine.oauth2.data.jpa.jackson2.OAuth2TokenJackson2Module;
import cn.herodotus.engine.oauth2.data.jpa.service.HerodotusRegisteredClientService;
import cn.herodotus.engine.oauth2.data.jpa.utils.OAuth2AuthorizationUtils;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: 基于Jpa 的 RegisteredClient服务 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/2/25 21:27
 */
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaRegisteredClientRepository.class);

    private final HerodotusRegisteredClientService herodotusRegisteredClientService;
    private final PasswordEncoder passwordEncoder;

    private final OAuth2JacksonProcessor jacksonProcessor;

    public JpaRegisteredClientRepository(HerodotusRegisteredClientService herodotusRegisteredClientService, PasswordEncoder passwordEncoder) {
        this.herodotusRegisteredClientService = herodotusRegisteredClientService;
        this.passwordEncoder = passwordEncoder;

        this.jacksonProcessor = new OAuth2JacksonProcessor();
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        log.debug("[Herodotus] |- Jpa Registered Client Repository save entity.");
        this.herodotusRegisteredClientService.save(toEntity(registeredClient));
    }

    @Override
    public RegisteredClient findById(String id) {
        log.debug("[Herodotus] |- Jpa Registered Client Repository findById.");
        HerodotusRegisteredClient herodotusRegisteredClient = this.herodotusRegisteredClientService.findById(id);
        if (ObjectUtils.isNotEmpty(herodotusRegisteredClient)) {
            return toObject(herodotusRegisteredClient);
        }
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        log.debug("[Herodotus] |- Jpa Registered Client Repository findByClientId.");
        return this.herodotusRegisteredClientService.findByClientId(clientId).map(this::toObject).orElse(null);
    }

    public void remove(String id) {
        log.debug("[Herodotus] |- Jpa Registered Client Repository remove.");
        this.herodotusRegisteredClientService.deleteById(id);
    }

    private RegisteredClient toObject(HerodotusRegisteredClient herodotusRegisteredClient) {
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(
                herodotusRegisteredClient.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(
                herodotusRegisteredClient.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(
                herodotusRegisteredClient.getRedirectUris());
        Set<String> postLogoutRedirectUris = StringUtils.commaDelimitedListToSet(
                herodotusRegisteredClient.getPostLogoutRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(
                herodotusRegisteredClient.getScopes());

        RegisteredClient.Builder builder = RegisteredClient.withId(herodotusRegisteredClient.getId())
                .clientId(herodotusRegisteredClient.getClientId())
                .clientIdIssuedAt(DateUtil.toInstant(herodotusRegisteredClient.getClientIdIssuedAt()))
                .clientSecret(herodotusRegisteredClient.getClientSecret())
                .clientSecretExpiresAt(DateUtil.toInstant(herodotusRegisteredClient.getClientSecretExpiresAt()))
                .clientName(herodotusRegisteredClient.getClientName())
                .clientAuthenticationMethods(authenticationMethods ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(OAuth2AuthorizationUtils.resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(OAuth2AuthorizationUtils.resolveAuthorizationGrantType(grantType))))
                .redirectUris((uris) -> uris.addAll(redirectUris))
                .postLogoutRedirectUris((uris) -> uris.addAll(postLogoutRedirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));

        Map<String, Object> clientSettingsMap = parseMap(herodotusRegisteredClient.getClientSettings());
        builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());

        Map<String, Object> tokenSettingsMap = parseMap(herodotusRegisteredClient.getTokenSettings());
        builder.tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());

        return builder.build();
    }

    private HerodotusRegisteredClient toEntity(RegisteredClient registeredClient) {
        List<String> clientAuthenticationMethods = new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
                clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
                authorizationGrantTypes.add(authorizationGrantType.getValue()));

        HerodotusRegisteredClient entity = new HerodotusRegisteredClient();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(DateUtil.toLocalDateTime(registeredClient.getClientIdIssuedAt()));
        entity.setClientSecret(encode(registeredClient.getClientSecret()));
        entity.setClientSecretExpiresAt(DateUtil.toLocalDateTime(registeredClient.getClientSecretExpiresAt()));
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        entity.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        entity.setPostLogoutRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getPostLogoutRedirectUris()));
        entity.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));
        entity.setClientSettings(writeMap(registeredClient.getClientSettings().getSettings()));
        entity.setTokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()));

        return entity;
    }

    private String encode(String value) {
        if (value != null) {
            return this.passwordEncoder.encode(value);
        }
        return null;
    }

    private Map<String, Object> parseMap(String data) {
        return jacksonProcessor.parseMap(data);
    }

    private String writeMap(Map<String, Object> data) {
        return jacksonProcessor.writeMap(data);
    }
}
