package com.bongsco.api.common.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bongsco.api.adjust.annual.dto.response.EmployeeResponse;
import com.bongsco.api.adjust.common.repository.AdjustSubjectRepository;

import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class ExcelDataProvider {

    private final AdjustSubjectRepository adjustSubjectRepository;

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

            default -> throw new IllegalArgumentException("알 수 없는 pageType: " + pageType);
        };
    }
}

