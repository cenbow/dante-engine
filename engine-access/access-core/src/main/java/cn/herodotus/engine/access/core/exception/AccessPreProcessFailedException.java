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

package cn.herodotus.engine.access.core.exception;

import cn.herodotus.engine.access.core.constants.AccessErrorCodes;
import cn.herodotus.engine.assistant.definition.domain.Feedback;
import cn.herodotus.engine.assistant.definition.exception.PlatformRuntimeException;

/**
 * <p>Description: 接入预操作失败错误 </p>
 *
 * @author : gengwei.zheng
 * @date : 2022/1/26 11:10
 */
public class AccessPreProcessFailedException extends PlatformRuntimeException {

    public AccessPreProcessFailedException() {
    }

    public AccessPreProcessFailedException(String message) {
        super(message);
    }

    public AccessPreProcessFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessPreProcessFailedException(Throwable cause) {
        super(cause);
    }

    public AccessPreProcessFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Feedback getFeedback() {
        return AccessErrorCodes.ACCESS_PRE_PROCESS_FAILED_EXCEPTION;
    }
}
