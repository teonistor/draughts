package io.github.teonistor.connectn.data;

import org.junit.jupiter.api.Test;

import static io.github.teonistor.connectn.data.Color.Red;
import static io.github.teonistor.connectn.data.Color.Yellow;
import static org.assertj.core.api.Assertions.assertThat;

public class ColorTest {

    @Test
    void next() {
        assertThat(Yellow.next()).isSameAs(Red);
        assertThat(Red.next()).isSameAs(Yellow);
    }
}
