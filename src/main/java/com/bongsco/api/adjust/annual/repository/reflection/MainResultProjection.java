package com.bongsco.api.adjust.annual.repository.reflection;

import com.bongsco.api.adjust.common.entity.PaybandAppliedType;
public interface MainResultProjection {
    String getEmpNum();

    String getName();

    String getGradeName();

    String getPositionName();

    String getDepName();

    String getRankCode();

    Double getStdSalaryIncrementRate();

    Double getFinalStdSalary();

    Double getStdSalary();

    Double getHpoBonus();

    Boolean getIsInHpo();

    Long getEmpId();

    Long getAdjustSubjectId();

    Long getGradeId();

    Long getRankId();

    Long getAdjustGradeId();

    Double getBonusMultiplier();

    Double getSalaryIncrementRate();

    PaybandAppliedType getIsPaybandApplied();

    Double getTotalSalary();
}
