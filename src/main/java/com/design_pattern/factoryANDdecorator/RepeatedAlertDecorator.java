package com.design_pattern.factoryANDdecorator;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;

    public RepeatedAlertDecorator(Alert alert, int repeatCount) {
        super(alert);
        this.repeatCount = repeatCount;
    }

    @Override
    public void trigger() {
        for (int i = 0; i < repeatCount; i++) {
            decoratedAlert.trigger();
        }
    }
}
