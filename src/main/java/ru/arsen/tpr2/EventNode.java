package ru.arsen.tpr2;

public class EventNode {
    private String name;
    private EventType type;
    private double probability;
    private EventNode parent;

    public EventNode(String name, EventType type) {
        this.name = name;
        this.type = type;
        this.probability = (type == EventType.INIT) ? 0.5 : 0.0; // Значение по умолчанию
    }

    public String getName() { return name; }
    public EventType getType() { return type; }
    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }
    public void setParent(EventNode parent) { this.parent = parent; }

    @Override
    public String toString() { return name + " (" + type + ")"; }
}
