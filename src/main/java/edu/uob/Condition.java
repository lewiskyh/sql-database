package edu.uob;

public class Condition {

    private String attributeName;

    private String comparator; // ">", "<", "==", "!=", ">=", "<=", "LIKE"

    private String baseValue;

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public void setBaseValue(String baseValue) {
        this.baseValue = baseValue;
    }

    public String getBaseValue() {
        return baseValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getComparator() {
        return comparator;
    }

    public boolean compareData (String valueToCompare) {
        switch (comparator) {
            case ">":
                return valueToCompare.compareTo(baseValue) > 0;
            case "<":
                return valueToCompare.compareTo(baseValue) < 0;
            case "==":
                return valueToCompare.compareTo(baseValue) == 0;
            case "!=":
                return valueToCompare.compareTo(baseValue) != 0;
            case ">=":
                return valueToCompare.compareTo(baseValue) >= 0;
            case "<=":
                return valueToCompare.compareTo(baseValue) <= 0;
            case "LIKE":
                return valueToCompare.contains(baseValue);
            default:
                return false;
        }
    }

}
