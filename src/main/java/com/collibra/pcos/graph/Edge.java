package com.collibra.pcos.graph;

import java.util.Objects;

/**
 * Immutable Graph's Edge
 */
public final class Edge {

    private final Integer weight;
    private final String from;
    private final String to;

    public Edge(String from, String to, Integer weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Edge other = (Edge) o;
        return Objects.equals(from, other.from)
            && Objects.equals(to, other.to)
            && Objects.equals(weight, other.weight);
    }
    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    @Override
    public String toString() {
        return String.format("%s-(%d)->%s", from, weight, to);
    }

}
