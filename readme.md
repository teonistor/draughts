Poor man's arbitrary-dimension terminal-based draughts:
1. `mvn clean verify`
2. `mvn -Dexec.classpathScope=test -Dexec.mainClass=io.github.teonistor.draughts.EvolvingIT exec:java`
3. Type `new game` to be prompted for settings

For the smallest meaningful 3D game enter:
* Starting rows: `1`
* Dimensions: `3 3 3`
