package com.track.toy.graph;

public class NodeHandler<T, R extends Comparable<R>, K, E> {
    private Graph<T, R, K, E> graph;

    NodeHandler(Graph<T, R, K, E> graph) {
        this.graph = graph;
    }

    public K newNode(T data) {
        return graph.newNode(data);
    }

    public K removeNode(T data, boolean force) {
        return graph.removeNode(data, force);
    }

    public K removeNodeByKey(K key, boolean force) {
        return graph.removeNodeByKey(key, false);
    }

    public T getNode(K key) {
        return graph.getNode(key);
    }

    public K getNodeKey(T data) {
        return graph.getNodeKey(data);
    }
}