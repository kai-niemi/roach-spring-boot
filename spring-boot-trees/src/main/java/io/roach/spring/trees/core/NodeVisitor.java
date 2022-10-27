package io.roach.spring.trees.core;

public interface NodeVisitor<T extends Comparable<T>> {
    void visit(T node);
}
