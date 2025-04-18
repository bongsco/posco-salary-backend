package com.bongsco.web.adjust.common.repository.reflection;

import java.time.LocalDate;

public interface AdjustItemProjection {
    Long getId();

    Integer getYear();

    Integer getMonth();

    String getAdjustType();

    Integer getOrderNumber();

    String getStepName();

    String getDetailStepName();

    Boolean getIsSubmitted();

    LocalDate getBaseDate();

    LocalDate getStartDate();

    LocalDate getEndDate();

    String getAuthor();

    String getUrl();
}
