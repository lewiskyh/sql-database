package edu.uob;

public class Condition {

    private String attributeName;

    private String comparator; // ">", "<", "==", "!=", ">=", "<=", "LIKE", "="

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

    public String getAttributeName() {
        return attributeName;
    }


    public boolean isNumeber (String value) {
        if(value == null) { return false; }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean compareData (String valueToCompare) throws DatabaseException {
        if (isNumeber(valueToCompare) && isNumeber(baseValue)) {
            Double numericValueToCompare = Double.parseDouble(valueToCompare);
            Double numericBaseValue = Double.parseDouble(baseValue);

            switch (comparator) {
                case ">":
                    return numericValueToCompare > numericBaseValue;
                case "<":
                    return numericValueToCompare < numericBaseValue;
                case "==":
                    return numericValueToCompare.equals(numericBaseValue);
                case "!=":
                    return !numericValueToCompare.equals(numericBaseValue);
                case ">=":
                    return numericValueToCompare >= numericBaseValue;
                case "<=":
                    return numericValueToCompare <= numericBaseValue;
                default:
                    throw new DatabaseException("Invalid comparator: " + comparator);
            }
        }
            switch (comparator) {
                case "==":
                    return valueToCompare.compareTo(baseValue) == 0;
                case "!=":
                    return valueToCompare.compareTo(baseValue) != 0;
                case "LIKE":
                    return valueToCompare.contains(baseValue);

            }
        throw new DatabaseException("Invalid comparator: " + comparator);

    }



}
