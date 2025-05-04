package com.bongsco.mobile.repository.reflection;

import java.time.LocalDate;

import com.bongsco.mobile.domain.AdjustType;
import com.bongsco.mobile.domain.PaybandAppliedType;
public interface AdjustDetailProjection {
    Integer getYear();

    Integer getOrderNumber();

    AdjustType getAdjustType();

    String getAuthor();

    LocalDate getBaseDate();

    LocalDate getExceptionStartDate();

    LocalDate getExceptionEndDate();

    String getName();

    String getGradeName();

    String getDepartmentName();

    String getPositionName();

    LocalDate getHireDate();

    String getEmploymentTypeName();

    String getRankCode();

    Boolean getIsInHpo();

    Double getSalaryIncrementRate();

    Double getBonusMultiplier();

    Double getHpoSalaryIncrementByRank();

    Double getHpoBonusMultiplier();

    Double getStdSalary();

    Double getHpoBonus();

    PaybandAppliedType getIsPaybandApplied();

    Double getBeforeStdSalary();
}

