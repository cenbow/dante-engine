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

package cn.herodotus.engine.oauth2.management.controller;

import cn.herodotus.engine.assistant.core.domain.Result;
import cn.herodotus.engine.data.core.service.WriteableService;
import cn.herodotus.engine.oauth2.management.converter.OAuth2ApplicationToDtoConverter;
import cn.herodotus.engine.oauth2.management.converter.OAuth2ApplicationToEntityConverter;
import cn.herodotus.engine.oauth2.management.dto.OAuth2ApplicationDto;
import cn.herodotus.engine.oauth2.management.entity.OAuth2Application;
import cn.herodotus.engine.oauth2.management.service.OAuth2ApplicationService;
import cn.herodotus.engine.rest.core.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Description: OAuth2应用管理接口 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/3/1 18:52
 */
@RestController
@RequestMapping("/authorize/application")
@Tags({
        @Tag(name = "OAuth2 认证服务接口"),
        @Tag(name = "OAuth2 应用管理接口")
})
public class OAuth2ApplicationController extends BaseController<OAuth2Application, String> {

    private final OAuth2ApplicationService applicationService;
    private final Converter<OAuth2Application, OAuth2ApplicationDto> toDto;
    private final Converter<OAuth2ApplicationDto, OAuth2Application> toEntity;

    public OAuth2ApplicationController(OAuth2ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.toEntity = new OAuth2ApplicationToEntityConverter();
        this.toDto = new OAuth2ApplicationToDtoConverter();
    }

    @Override
    public WriteableService<OAuth2Application, String> getWriteableService() {
        return this.applicationService;
    }

    @Operation(summary = "获取OAuth2Application分页数据", description = "通过pageNumber和pageSize获取分页数据")
    @Parameters({
            @Parameter(name = "pageNumber", required = true, description = "当前页数"),
            @Parameter(name = "pageSize", required = true, description = "每页显示数据条目")
    })
    @GetMapping
    @Override
    public Result<Map<String, Object>> findByPage(
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize) {

        Page<OAuth2Application> pages = applicationService.findByPage(pageNumber, pageSize);
        if (ObjectUtils.isNotEmpty(pages) && CollectionUtils.isNotEmpty(pages.getContent())) {
            List<OAuth2ApplicationDto> auth2Applications = pages.getContent().stream().map(toDto::convert).collect(Collectors.toList());
            return result(getPageInfoMap(auth2Applications, pages.getTotalPages(), pages.getTotalElements()));
        }

        return Result.failure("查询数据失败！");
    }

    @Operation(summary = "保存或更新OAuth2应用", description = "接收JSON数据，转换为OauthClientDetails实体，进行更新")
    @Parameters({
            @Parameter(name = "oauthClientDetails", required = true, description = "可转换为OauthClientDetails实体的json数据")
    })
    @PostMapping
    public Result<OAuth2Application> saveOrUpdate(@RequestBody OAuth2ApplicationDto domain) {
        OAuth2Application oAuth2Application = applicationService.saveAndFlush(toEntity.convert(domain));
        return result(oAuth2Application);
    }

    @Operation(summary = "删除OAuth2应用", description = "根据应用ID删除OAuth2应用，以及相关联的关系数据")
    @Parameters({
            @Parameter(name = "applicationId", required = true, description = "applicationId")
    })
    @DeleteMapping
    @Override
    public Result<String> delete(@RequestBody String applicationId) {
        applicationService.deleteById(applicationId);
        return Result.success("删除成功");
    }

    @Operation(summary = "给应用分配Scope", description = "给应用分配Scope")
    @Parameters({
            @Parameter(name = "appKey", required = true, description = "appKey"),
            @Parameter(name = "scopes[]", required = true, description = "Scope对象组成的数组")
    })
    @PutMapping
    public Result<OAuth2Application> authorize(@RequestParam(name = "applicationId") String scopeId, @RequestParam(name = "scopes[]") String[] scopes) {
        OAuth2Application application = applicationService.authorize(scopeId, scopes);
        return result(application);
    }
}
