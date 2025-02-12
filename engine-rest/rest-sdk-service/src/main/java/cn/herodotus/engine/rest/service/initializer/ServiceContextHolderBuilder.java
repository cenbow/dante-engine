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

package cn.herodotus.engine.rest.service.initializer;

import cn.herodotus.engine.assistant.core.context.ServiceContextHolder;
import cn.herodotus.engine.assistant.core.enums.Architecture;
import cn.herodotus.engine.assistant.core.utils.WellFormedUtils;
import cn.herodotus.engine.rest.condition.properties.EndpointProperties;
import cn.herodotus.engine.rest.condition.properties.PlatformProperties;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;

/**
 * <p>Description: ServiceContextHolder 构建器 </p>
 *
 * @author : gengwei.zheng
 * @date : 2023/10/2 18:41
 */
public class ServiceContextHolderBuilder{

    private PlatformProperties platformProperties;
    private EndpointProperties endpointProperties;
    private ServerProperties serverProperties;

    private ServiceContextHolderBuilder() {

    }

    public static ServiceContextHolderBuilder builder() {
        return new ServiceContextHolderBuilder();
    }

    public ServiceContextHolderBuilder platformProperties(PlatformProperties platformProperties) {
        this.platformProperties = platformProperties;
        return this;
    }

    public ServiceContextHolderBuilder endpointProperties(EndpointProperties endpointProperties) {
        this.endpointProperties = endpointProperties;
        return this;
    }

    public ServiceContextHolderBuilder serverProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
        return this;
    }

    public ServiceContextHolder build() {
        ServiceContextHolder holder = ServiceContextHolder.getInstance();
        holder.setPort(String.valueOf(this.getPort()));
        holder.setIp(getHostAddress());
        setProperties(holder);
        return holder;
    }

    private String getHostAddress() {
        String address = WellFormedUtils.getHostAddress();
        if (ObjectUtils.isNotEmpty(serverProperties.getAddress())) {
            address = serverProperties.getAddress().getHostAddress();
        }

        if (StringUtils.isNotBlank(address)) {
            return address;
        } else {
            return "localhost";
        }
    }

    private Integer getPort() {
        Integer port = serverProperties.getPort();
        if (ObjectUtils.isNotEmpty(port)) {
            return port;
        } else {
            return 8080;
        }
    }

    private void setProperties(ServiceContextHolder serviceContextHolder) {
        serviceContextHolder.setArchitecture(platformProperties.getArchitecture());
        serviceContextHolder.setDataAccessStrategy(platformProperties.getDataAccessStrategy());
        serviceContextHolder.setProtocol(platformProperties.getProtocol());

        if (platformProperties.getArchitecture() == Architecture.MONOCOQUE) {
            String issuerUri = endpointProperties.getIssuerUri();
            serviceContextHolder.setGatewayServiceUri(issuerUri);
            serviceContextHolder.setUaaServiceUri(issuerUri);
            serviceContextHolder.setUpmsServiceUri(issuerUri);
            serviceContextHolder.setMessageServiceUri(issuerUri);
            serviceContextHolder.setOssServiceUri(issuerUri);
        } else {
            serviceContextHolder.setUaaServiceName(endpointProperties.getUaaServiceName());
            serviceContextHolder.setUpmsServiceName(endpointProperties.getUpmsServiceName());
            serviceContextHolder.setMessageServiceName(endpointProperties.getMessageServiceName());
            serviceContextHolder.setOssServiceName(endpointProperties.getOssServiceName());
            serviceContextHolder.setGatewayServiceUri(endpointProperties.getGatewayServiceUri());
            serviceContextHolder.setUaaServiceUri(endpointProperties.getUaaServiceUri());
            serviceContextHolder.setUpmsServiceUri(endpointProperties.getUpmsServiceUri());
            serviceContextHolder.setMessageServiceUri(endpointProperties.getMessageServiceUri());
            serviceContextHolder.setOssServiceUri(endpointProperties.getOssServiceUri());
        }

        serviceContextHolder.setAuthorizationUri(endpointProperties.getAuthorizationUri());
        serviceContextHolder.setAuthorizationEndpoint(endpointProperties.getAuthorizationEndpoint());
        serviceContextHolder.setAccessTokenUri(endpointProperties.getAccessTokenUri());
        serviceContextHolder.setAccessTokenEndpoint(endpointProperties.getAccessTokenEndpoint());
        serviceContextHolder.setJwkSetUri(endpointProperties.getJwkSetUri());
        serviceContextHolder.setJwkSetEndpoint(endpointProperties.getJwkSetEndpoint());
        serviceContextHolder.setTokenRevocationUri(endpointProperties.getTokenRevocationUri());
        serviceContextHolder.setTokenRevocationEndpoint(endpointProperties.getTokenRevocationEndpoint());
        serviceContextHolder.setTokenIntrospectionUri(endpointProperties.getTokenIntrospectionUri());
        serviceContextHolder.setTokenIntrospectionEndpoint(endpointProperties.getTokenIntrospectionEndpoint());
        serviceContextHolder.setDeviceAuthorizationUri(endpointProperties.getDeviceAuthorizationUri());
        serviceContextHolder.setDeviceAuthorizationEndpoint(endpointProperties.getDeviceAuthorizationEndpoint());
        serviceContextHolder.setDeviceVerificationUri(endpointProperties.getDeviceVerificationUri());
        serviceContextHolder.setDeviceVerificationEndpoint(endpointProperties.getDeviceVerificationEndpoint());
        serviceContextHolder.setOidcClientRegistrationUri(endpointProperties.getOidcClientRegistrationUri());
        serviceContextHolder.setOidcClientRegistrationEndpoint(endpointProperties.getOidcClientRegistrationEndpoint());
        serviceContextHolder.setOidcLogoutUri(endpointProperties.getOidcLogoutUri());
        serviceContextHolder.setOidcLogoutEndpoint(endpointProperties.getOidcLogoutEndpoint());
        serviceContextHolder.setOidcUserInfoUri(endpointProperties.getOidcUserInfoUri());
        serviceContextHolder.setOidcUserInfoEndpoint(endpointProperties.getOidcUserInfoEndpoint());
        serviceContextHolder.setIssuerUri(endpointProperties.getIssuerUri());
    }
}
