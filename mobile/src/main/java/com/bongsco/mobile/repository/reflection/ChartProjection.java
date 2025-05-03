package com.bongsco.mobile.repository.reflection;

public interface ChartProjection {
    Integer getYear();
    Integer getOrderNumber();
    Double getStdSalary();
    Double getHpoBonus();
    Double getBonusPrice();
    Boolean getInHpo();
    Double getSalaryIncrementRate();
    Double getBonusMultiplier();
    Double getHpoSalaryIncrementByRank();
    Double getHpoBonusMultiplier();
}
