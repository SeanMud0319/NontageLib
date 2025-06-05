package top.nontage.nontagelib.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();
    String[] aliases() default {};
    String permission() default "";
    String description() default "";
    boolean override() default false;
    boolean shouldLoad() default true;
}
