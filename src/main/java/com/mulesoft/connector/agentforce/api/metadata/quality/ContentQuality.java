package com.mulesoft.connector.agentforce.api.metadata.quality;

import java.io.Serializable;

public class ContentQuality implements Serializable {

  private ScanToxicity scanToxicity;

  public void setScanToxicity(ScanToxicity scanToxicity) {
    this.scanToxicity = scanToxicity;
  }

  public ScanToxicity getScanToxicity() {
    return scanToxicity;
  }
}
