package cn.herodotus.engine.data.core.annotation;

import cn.herodotus.engine.data.core.identifier.SnowFlakeIDGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 雪花主键ID
 *
 * @author lkhsh
 * @date 2023-07-14
 */
@IdGeneratorType(SnowFlakeIDGenerator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface SnowIdGenerator {
}
