package com.bongsco.api.common.utils;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bongsco.api.adjust.annual.dto.MainResultExcelDto;
import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.common.repository.AdjustRepository;
import com.bongsco.api.adjust.common.repository.AdjustSubjectRepository;

import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class ExcelDataProvider {

    private final AdjustSubjectRepository adjustSubjectRepository;
    private final AdjustRepository adjustRepository;

    public List<String> getHeadersByPageType(String pageType) {
        return switch (pageType) {
            case "main" -> List.of("연도", "월", "조정유형", "상태", "통합인사반영여부", "진행단계", "등록일", "등록자");
            case "Subject", "NonSubject" -> List.of("직번", "성명", "채용일자", "평가등급");
            case "highOrgSubject" ->
                List.of("직번", "성명", "부서", "직급", "평가등급", "고성과조직 가산", "평가차등 연봉 인상률", "평가차등 경영성과금 지급률");
            case "upperPayband" -> List.of("직번", "성명", "부서", "직급", "평가등급", "기준연봉 월할액", "상한 금액", "Payband 적용", "비고");
            case "lowerPayband" -> List.of("직번", "성명", "부서", "직급", "평가등급", "기준연봉 월할액", "하한 금액", "Payband 적용", "비고");
            case "adjustResult" ->
                List.of("직번", "성명", "직급", "직책", "부서", "평가", "평차연봉인상률", "평차금인상률", "기준연봉 인상률", "Payband", "기준연봉 조정전",
                    "기준연봉 조정후", "계약연봉 조정전", "계약연봉 조정후");
            default -> throw new IllegalArgumentException("알 수 없는 pageType: " + pageType);
        };
    }

    public List<List<String>> getDataByPageType(Long adjustId, String pageType) {
        return switch (pageType) {

            case "Subject", "NonSubject" -> {
                boolean requiredSubjectStatus = pageType.equals("Subject");

                // 전체 조회 후 isSubject 값 기준으로 필터링
                List<EmployeeResponse> all = adjustSubjectRepository.findAllEmployeeResponsesByAdjustInfoId(adjustId);

                yield all.stream()
                    .filter(e -> e.getSubjectUse() == requiredSubjectStatus)
                    .map(e -> List.of(
                        e.getEmpNum(),
                        e.getName(),
                        e.getHireDate().toString(),
                        e.getRankName()
                    ))
                    .toList();
            }
            case "adjustResult" -> {
                List<MainResultExcelDto> resultList = adjustSubjectRepository.findAllResultDtoByAdjustId(adjustId);

                yield resultList.stream()
                    .map(dto -> List.of(
                        dto.getEmpNum(),                                 // 직번
                        dto.getName(),                                   // 성명
                        dto.getRankCode(),                               // 직급
                        dto.getPositionName(),                           // 직책
                        dto.getDepName(),                                // 부서
                        dto.getGradeName(),                              // 평가등급

                        String.valueOf(dto.getSalaryIncrementRate()),    // 평차연봉인상률
                        String.valueOf(dto.getBonusMultiplier()),         // 평차금인상률
                        String.valueOf(dto.getStdSalaryIncrementRate()), // 기준연봉 인상률

                        dto.getIsPaybandApplied() == null ? "미적용" :
                            switch (dto.getIsPaybandApplied()) {
                                case UPPER -> "적용(상한)";
                                case LOWER -> "적용(하한)";
                                default -> "미적용";
                            },

                        String.valueOf(0.0), // 기준연봉 조정전 → dto에 없음 → 나중에 계산된 값 반영하려면 수정 필요
                        String.valueOf(dto.getFinalStdSalary()),         // 기준연봉 조정후

                        String.valueOf(0.0), // 계약연봉 조정전 → 마찬가지로 없음
                        String.valueOf(
                            Optional.ofNullable(dto.getFinalStdSalary()).orElse(0.0) +
                                Optional.ofNullable(dto.getHpoBonus()).orElse(0.0)        // 계약연봉 조정후
                        )
                    ))
                    .toList();
            }

            default -> throw new IllegalArgumentException("알 수 없는 pageType: " + pageType);
        };
    }
}

