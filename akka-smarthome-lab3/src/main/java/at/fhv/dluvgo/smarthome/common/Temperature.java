package at.fhv.dluvgo.smarthome.common;

public class Temperature {
    public enum Unit {
        CELSIUS,
    }

    private final Unit unit;
    private final float value;

    public Temperature(Unit unit, float value) {
        this.unit = unit;
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public float getValue() {
        return value;
    }
}
