package io.github.teonistor.chess.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@EnableChessCore
@Import(ChessWsConfig.class)
@Retention(RUNTIME)
public @interface EnableChess {}
