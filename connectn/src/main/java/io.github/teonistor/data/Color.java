package io.github.teonistor.connectn.data;

public enum Color {
    Red, Yellow;

    public Color next() {
        return this == Red ? Yellow : Red;
    }
}
