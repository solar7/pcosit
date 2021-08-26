package com.collibra.pcos.session;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable class that represents command execution result
 */
public final class ExecResult {

    /**
     * INTERMEDIATE - to proceed, TERMINATE - to finish
     */
    public enum ExecCode {
        INTERMEDIATE,
        TERMINATE
    }

    private final String out;
    private final ExecCode code;

    public static ExecResult intermediate() {
        return new ExecResult(null, ExecCode.INTERMEDIATE);
    }

    public static ExecResult terminate() {
        return new ExecResult(null, ExecCode.TERMINATE);
    }

    public static ExecResult intermediate(String out) {
        return new ExecResult(out, ExecCode.INTERMEDIATE);
    }

    public static ExecResult terminate(String out) {
        return new ExecResult(out, ExecCode.TERMINATE);
    }

    private ExecResult(String out, ExecCode code) {
        this.out = out;
        this.code = code;
    }

    public Optional<String> getOut() {
        return Optional.ofNullable(out);
    }

    public ExecCode getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ExecResult that = (ExecResult) o;
        return Objects.equals(out, that.out) &&
                code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(out, code);
    }

    @Override
    public String toString() {
        return "ExecResult{" +
                "out='" + out + '\'' +
                ", code=" + code +
                '}';
    }
}
