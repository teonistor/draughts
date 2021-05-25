package io.github.teonistor.ttt.launch;

import io.github.teonistor.ttt.ws.EnableTtt;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@EnableTtt
@SpringBootApplication
public class MultiGameWebLauncher {
    public static void main(final String[] arg) {
        run(MultiGameWebLauncher.class, arg);
    }
}
