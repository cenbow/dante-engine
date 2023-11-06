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

package cn.herodotus.engine.message.core.constants;

import cn.herodotus.engine.assistant.core.exception.feedback.NotAcceptableFeedback;

/**
 * <p>Description: WebSocket 统一错误代码定义 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/12/29 15:57
 */
public interface MessageErrorCodes {

    NotAcceptableFeedback ILLEGAL_CHANNEL = new NotAcceptableFeedback("WebSocket Channel 设置错误");
    NotAcceptableFeedback PRINCIPAL_NOT_FOUND = new NotAcceptableFeedback("WebSocket 无法获取用户身份信息");
}
