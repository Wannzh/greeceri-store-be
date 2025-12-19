package com.greeceri.store.models.enums;

public enum DeliverySlot {
    MORNING("07:00 - 10:00", "Pagi"),
    AFTERNOON("14:00 - 17:00", "Siang");

    private final String timeRange;
    private final String label;

    DeliverySlot(String timeRange, String label) {
        this.timeRange = timeRange;
        this.label = label;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getLabel() {
        return label;
    }
}
