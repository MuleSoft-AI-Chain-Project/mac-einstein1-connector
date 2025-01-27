package com.mulesoft.connector.agentforce.api.metadata.quality;

import java.io.Serializable;
import java.util.Objects;

public class ContentQuality implements Serializable {

  private ScanToxicity scanToxicity;

  public void setScanToxicity(ScanToxicity scanToxicity) {
    this.scanToxicity = scanToxicity;
  }

  public ScanToxicity getScanToxicity() {
    return scanToxicity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ContentQuality))
      return false;
    ContentQuality that = (ContentQuality) o;
    return Objects.equals(getScanToxicity(), that.getScanToxicity());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getScanToxicity());
  }
}
