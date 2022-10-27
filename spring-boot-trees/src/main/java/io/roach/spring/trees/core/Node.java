package io.roach.spring.trees.core;

import java.util.Deque;

/**
 * Represents a node or vertex in an undirected graph (tree) structure.
 *
 * @param <T> the node type
 */
public interface Node<T extends Comparable<T>> {
    int getDescendants();

    int getLevel();

    Deque<T> getPath();

    boolean isRoot();
}
