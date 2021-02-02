package io.github.teonistor.ttt.ws;

import org.springframework.context.annotation.ComponentScan;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ComponentScan(basePackageClasses=EnableTtt.class)
@Retention(RUNTIME)
public @interface EnableTtt {}
