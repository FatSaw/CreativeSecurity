package gasha.creativesecurity;

import java.util.Objects;

class Pair<A, B> {
    private final A first;
    private final B second;

    Pair(A first, B seconds) {
        this.first = first;
        this.second = seconds;
    }

    A getFirst() {
        return this.first;
    }

    B getSecond() {
        return this.second;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair)o;
        return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public String toString() {
        return "Pair{first=" + this.first + ", second=" + this.second + '}';
    }
}

