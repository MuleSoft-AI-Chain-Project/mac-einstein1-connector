package com.mulesoft.connector.einstein.api.metadata.quality;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ScanToxicity implements Serializable {

  private boolean isDetected;
  private List<Categories> categories;

  public boolean getIsDetected() {
    return isDetected;
  }

  public List<Categories> getCategories() {
    return categories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ScanToxicity))
      return false;
    ScanToxicity that = (ScanToxicity) o;
    return getIsDetected() == that.getIsDetected() && Objects.equals(getCategories(), that.getCategories());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getIsDetected(), getCategories());
  }
}
