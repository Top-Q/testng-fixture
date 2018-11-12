package il.co.topq.fixture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Test method annotation that allows attaching fixture classes to test methods
 * 
 * @author Itai Agmon
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WithFixture {

	Class<? extends Fixture> value();

	String[] params() default {};

}
