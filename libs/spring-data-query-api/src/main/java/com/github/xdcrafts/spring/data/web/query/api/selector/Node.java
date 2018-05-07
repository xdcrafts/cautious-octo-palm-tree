package com.github.xdcrafts.spring.data.web.query.api.selector;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Node class.
 *
 * @author Vadim Dubs
 */
public final class Node {

    private final String name;
    private final Map<String, Node> children = new LinkedHashMap<>();
    public Node(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    /**
     * Get child node by name.
     */
    public Node getChild(String childName) {
        return this.children.get(childName);
    }

    /**
     * If this node has child with a name.
     */
    public boolean hasChild(String childName) {
        return this.children.containsKey(childName);
    }

    /**
     * Get children nodes.
     */
    public Collection<Node> getChildren() {
        return this.children.values();
    }

    /**
     * Add child node.
     */
    public Node addChild(Node child) {
        this.children.put(child.getName(), child);
        return this;
    }

    /**
     * If this node does not have any children.
     */
    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
