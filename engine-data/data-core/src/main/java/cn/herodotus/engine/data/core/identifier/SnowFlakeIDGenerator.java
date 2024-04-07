package cn.herodotus.engine.data.core.identifier;

import cn.herodotus.engine.data.core.annotation.SnowIdGenerator;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.data.id.Snowflake;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.factory.spi.StandardGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * 雪花主键生成器，使用 hutool 的雪花主键生成器
 *
 * @author lkhsh
 * @date 2023-07-14
 */
public class SnowFlakeIDGenerator implements IdentifierGenerator, StandardGenerator {
    private final Snowflake snowflake;
    private final Class<?> propertyType;

    public SnowFlakeIDGenerator(SnowIdGenerator config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
        // 工具获取的主键生成器是个单例，也就是同一个运行实例，理论上 dataCenter 和 workerId 不会重复
        snowflake = IdUtil.getSnowflake();
        // 初始化主键的类型
        if (idMember instanceof Method) {
            propertyType = ((Method) idMember).getReturnType();
        } else {
            propertyType = ((Field) idMember).getType();
        }
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        if (String.class.isAssignableFrom(propertyType)) {
            return snowflake.nextIdStr();
        }
        return snowflake.nextId();
    }
}
