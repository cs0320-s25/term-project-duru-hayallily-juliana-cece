package main.edu.brown.cs.student.main.server.model;

public class Ingredient {
  private int id;
  private String name;
  private String aisle;
  private double amount;
  private String unit;
  private String originalString;

  // For allergen and intolerance checking
  private boolean containsAllergen;
  private String[] possibleAllergens;

  public Ingredient() {
  }

  public Ingredient(int id, String name, String aisle, double amount, String unit) {
    this.id = id;
    this.name = name;
    this.aisle = aisle;
    this.amount = amount;
    this.unit = unit;
    this.originalString = amount + " " + unit + " " + name;
    this.containsAllergen = false;
    this.possibleAllergens = new String[0];
  }

  // Getters and setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAisle() {
    return aisle;
  }

  public void setAisle(String aisle) {
    this.aisle = aisle;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getOriginalString() {
    return originalString;
  }

  public void setOriginalString(String originalString) {
    this.originalString = originalString;
  }

  public boolean isContainsAllergen() {
    return containsAllergen;
  }

  public void setContainsAllergen(boolean containsAllergen) {
    this.containsAllergen = containsAllergen;
  }

  public String[] getPossibleAllergens() {
    return possibleAllergens;
  }

  public void setPossibleAllergens(String[] possibleAllergens) {
    this.possibleAllergens = possibleAllergens;
  }

  @Override
  public String toString() {
    return originalString + (containsAllergen ? " (Contains allergen)" : "");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Ingredient that = (Ingredient) obj;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
