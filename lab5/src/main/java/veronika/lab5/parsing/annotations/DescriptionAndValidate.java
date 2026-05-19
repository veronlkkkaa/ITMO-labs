package veronika.lab5.parsing.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//навесим аннотацию над нашей аннотацией, которая будет отвечать за то, где будет использоваться наша аннотация DescriptionForHuman.
//В данном случае используем аннотации для проверки наших данных на валидность.
//
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DescriptionAndValidate {
    String name() default "";

    String validation() default "";

}
