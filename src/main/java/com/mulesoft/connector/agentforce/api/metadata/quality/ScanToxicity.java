package com.mulesoft.connector.agentforce.api.metadata.quality;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ScanToxicity implements Serializable {

  private boolean isDetected;
  private List<Categories> categories;

  public void setIsDetected(boolean isDetected) {
    this.isDetected = isDetected;
  }

  public void setCategories(List<Categories> categories) {
    this.categories = categories;
  }

  public boolean getIsDetected() {
    return isDetected;
  }

  public List<Categories> getCategories() {
    return categories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScanToxicity)) return false;
    ScanToxicity that = (ScanToxicity) o;
    return isDetected == that.isDetected && Objects.equals(getCategories(), that.getCategories());
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDetected, getCategories());
  }
}
