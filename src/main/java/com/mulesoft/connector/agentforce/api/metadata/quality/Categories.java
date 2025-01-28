package com.mulesoft.connector.agentforce.api.metadata.quality;

import java.io.Serializable;
import java.util.Objects;

public class Categories implements Serializable {

  private String categoryName;
  private String score;

  public String getCategoryName() {
    return categoryName;
  }

  public String getScore() {
    return score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Categories))
      return false;
    Categories that = (Categories) o;
    return Objects.equals(getCategoryName(), that.getCategoryName()) && Objects.equals(getScore(), that.getScore());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCategoryName(), getScore());
  }
}
