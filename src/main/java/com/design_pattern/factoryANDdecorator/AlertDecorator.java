package com.design_pattern.factoryANDdecorator;

public class AlertDecorator implements Alert {
    public Alert decoratedAlert;

    public AlertDecorator(Alert decoratedAlert) {
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public void trigger() {
        decoratedAlert.trigger();
    }
}
