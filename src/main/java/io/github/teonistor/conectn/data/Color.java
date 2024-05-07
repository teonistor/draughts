package io.github.teonistor.conectn.data;

public enum Color {
    Red, Yellow;

    public Color next() {
        return this == Red ? Yellow : Red;
    }
}
