package io.github.teonistor.draughts.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Import({DraughtsConfig.class, DraughtsCtrl.class})
@Retention(RUNTIME)
public @interface EnableDraughtsCore {}
