package com.bongsco.web.adjust.annual.repository.reflection;

import com.bongsco.web.adjust.common.entity.PaybandAppliedType;
public interface MainResultProjection {
    String getEmpNum();

    String getName();

    String getGradeName();

    String getPositionName();

    String getDepName();

    String getRankCode();

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

    Double getBeforeStdSalary();

    Double getBeforeHpoBonus();

    Double getUpperBoundMemo();

    Double getLowerBoundMemo();
}
