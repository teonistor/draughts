package io.github.teonistor.conectn.data;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorTest {

    @Test
    void next() {
        assertThat(Yellow.next()).isSameAs(Red);
        assertThat(Red.next()).isSameAs(Yellow);
    }
}
