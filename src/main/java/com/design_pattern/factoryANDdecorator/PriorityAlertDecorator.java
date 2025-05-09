package com.design_pattern.factoryANDdecorator;

public class PriorityAlertDecorator extends AlertDecorator {
    private String priorityLevel;

    public PriorityAlertDecorator(Alert alert, String priorityLevel) {
        super(alert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public void trigger() {
        System.out.println("PRIORITY: " + priorityLevel);
        decoratedAlert.trigger();

    }

}
