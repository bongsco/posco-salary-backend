package com.bongsco.web.adjust.annual.service;

import static com.bongsco.web.common.exception.ErrorCode.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bongsco.web.adjust.annual.entity.PaybandCriteria;
import com.bongsco.web.adjust.annual.entity.SalaryIncrementByRank;
import com.bongsco.web.adjust.annual.repository.PaybandCriteriaRepository;
import com.bongsco.web.adjust.annual.repository.SalaryIncrementByRankRepository;
import com.bongsco.web.adjust.common.domain.AdjustType;
import com.bongsco.web.adjust.common.dto.request.AdjustPostRequest;
import com.bongsco.web.adjust.common.dto.request.AdjustSearchRequest;
import com.bongsco.web.adjust.common.dto.request.AdjustUpdateRequest;
import com.bongsco.web.adjust.common.dto.response.AdjustResponse;
import com.bongsco.web.adjust.common.dto.response.AdjustResponse.AdjustItemDto;
import com.bongsco.web.adjust.common.dto.response.SingleAdjustResponse;
import com.bongsco.web.adjust.common.entity.Adjust;
import com.bongsco.web.adjust.common.entity.AdjustEmploymentType;
import com.bongsco.web.adjust.common.entity.AdjustGrade;
import com.bongsco.web.adjust.common.entity.AdjustStep;
import com.bongsco.web.adjust.common.entity.Step;
import com.bongsco.web.adjust.common.repository.AdjustEmploymentTypeRepository;
import com.bongsco.web.adjust.common.repository.AdjustGradeRepository;
import com.bongsco.web.adjust.common.repository.AdjustRepository;
import com.bongsco.web.adjust.common.repository.AdjustStepRepository;
import com.bongsco.web.adjust.common.repository.StepRepository;
import com.bongsco.web.adjust.common.repository.reflection.AdjustItemProjection;
import com.bongsco.web.common.exception.CustomException;
import com.bongsco.web.employee.entity.EmploymentType;
import com.bongsco.web.employee.entity.Grade;
import com.bongsco.web.employee.entity.Rank;
import com.bongsco.web.employee.repository.EmploymentTypeRepository;
import com.bongsco.web.employee.repository.GradeRepository;
import com.bongsco.web.employee.repository.RankRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdjustService {
    private final AdjustRepository adjustRepository;
    private final AdjustGradeRepository adjustGradeRepository;
    private final AdjustEmploymentTypeRepository adjustEmploymentTypeRepository;
    private final AdjustStepRepository adjustStepRepository;
    private final GradeRepository gradeRepository;
    private final EmploymentTypeRepository employmentTypeRepository;
    private final StepRepository stepRepository;
    private final RankRepository rankRepository;
    private final SalaryIncrementByRankRepository salaryIncrementByRankRepository;
    private final PaybandCriteriaRepository paybandCriteriaRepository;

    public SingleAdjustResponse getAdjust(Long adjustId) {
        Adjust adjust = adjustRepository.findById(adjustId).orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        String title = String.format(
            "%d년 %d차 %s",
            adjust.getYear(),
            adjust.getOrderNumber(),
            adjust.getAdjustType().getDisplayName()
        );

        return SingleAdjustResponse.builder().title(title).build();
    }

    public AdjustResponse getAdjustInfo(AdjustSearchRequest adjustSearchRequest) {
        /* 페이지 정보 세팅 */
        int pageNumber = (adjustSearchRequest.getPage() != null && adjustSearchRequest.getPage() > 0) ?
            adjustSearchRequest.getPage() - 1 : 0; // 0-based index
        int pageSize = (adjustSearchRequest.getSize() != null && adjustSearchRequest.getSize() > 0) ?
            adjustSearchRequest.getSize() : 10; // 기본 페이지 크기 10

        /* 정렬 조건 세팅 */
        List<Map<String, String>> sorts = adjustSearchRequest.getSorts();
        List<Sort.Order> sortOrders = new ArrayList<>(); //초기값은 id
        if (sorts != null && !sorts.isEmpty()) {
            for (Map<String, String> sort : sorts) {
                for (Map.Entry<String, String> entry : sort.entrySet()) {
                    String field = entry.getKey();
                    String direction = entry.getValue();

                    if ("내림차순".equals(direction)) {
                        sortOrders.add(Sort.Order.desc(field));
                    } else if ("오름차순".equals(direction)) {
                        sortOrders.add(Sort.Order.asc(field));
                    }
                }
            }
        }
        /* 모두 다 같으면 id를 기준으로 정렬되도록 id 조건 마지막에 추가 */
        sortOrders.add(Sort.Order.desc("id"));

        /* Pageable 객체 생성 */
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortOrders));

        /* 데이터 가져오기 */
        /* AdjustType가 null인 경우에 SQL Parameter 추정에서 오류가 발생해서 findAdjustItem으로 할 때 str을 넘김. */
        List<String> adjustTypeStr = new ArrayList<>();
        if (adjustSearchRequest.getAdjType() != null) {
            adjustTypeStr = adjustSearchRequest.getAdjType().stream()
                .filter(item -> item != null)
                .map(AdjustType::name)
                .toList();
        }

        Page<AdjustItemProjection> adjustPageInfo = adjustRepository.findAdjustItemsWithNextStepPaginated(
            (adjustSearchRequest.getYear() == null || adjustSearchRequest.getYear().isEmpty()) ? null :
                adjustSearchRequest.getYear().toArray(new Integer[0]),
            (adjustSearchRequest.getMonth() == null || adjustSearchRequest.getMonth().isEmpty()) ? null :
                adjustSearchRequest.getMonth().toArray(new Integer[0]),
            (adjustTypeStr == null || adjustTypeStr.isEmpty()) ? null :
                adjustTypeStr.toArray(new String[0]),
            (adjustSearchRequest.getState() == null || adjustSearchRequest.getState().isEmpty()) ? null :
                adjustSearchRequest.getState().toArray(new Boolean[0]),
            (adjustSearchRequest.getIsSubmitted() == null || adjustSearchRequest.getIsSubmitted().isEmpty()) ? null :
                adjustSearchRequest.getIsSubmitted().toArray(new Boolean[0]),
            (adjustSearchRequest.getAuthor() == null || adjustSearchRequest.getAuthor().isEmpty()) ? null :
                adjustSearchRequest.getAuthor().stream().map(author -> "%" + author + "%").toArray(String[]::new),
            pageable);

        /* 페이지 정보 가져오기 */
        long totalPages = adjustPageInfo.getTotalPages();

        /* Page 정보를 List<AdjustItemDto>로 Mapping */
        List<AdjustItemDto> adjustItemDtoList = adjustPageInfo.getContent()
            .stream()
            .map(projection -> AdjustItemDto.builder()
                .id(projection.getId())
                .year(projection.getYear())
                .month(projection.getMonth())
                .adjustType(projection.getAdjustType())
                .orderNumber(projection.getOrderNumber())
                .stepName(projection.getStepName())
                .detailStepName(projection.getDetailStepName())
                .isSubmitted(projection.getIsSubmitted())
                .baseDate(projection.getBaseDate())
                .startDate(projection.getStartDate())
                .endDate(projection.getEndDate())
                .author(projection.getAuthor())
                .url(projection.getUrl())
                .build() // DTO 객체 생성
            )
            .toList();

        // AdjustResponse 객체 생성 및 반환
        return AdjustResponse.builder()
            .totalPage(totalPages)
            .adjustItems(adjustItemDtoList)
            .build();
    }

    public void updateAdjust(Long id, AdjustUpdateRequest request) {
        Adjust adjust = adjustRepository.findById(id)
            .orElseThrow(() -> new CustomException(RESOURCE_NOT_FOUND));

        adjust.toBuilder().isSubmitted(request.getIsSubmitted());

        adjustRepository.save(adjust);
    }

    public void deleteAdjustInfo(Long adjustId) {
        adjustRepository.deleteById(adjustId);
    }

    @Transactional
    public void postAdjustInfo(AdjustPostRequest postRequest) {
        /* 차수 정보 가져오기 */
        int yearMaxOrderNumber = Optional.ofNullable(
            adjustRepository.findMaxOrderNumberByYear(postRequest.getStartDate().getYear(), postRequest.getType())
        ).orElse(0).intValue();

        LocalDate today = LocalDate.now();
        /* adjust 추가 */
        Adjust adjust = Adjust.builder()
            .year(postRequest.getStartDate().getYear())
            .month(postRequest.getStartDate().getMonthValue())
            .adjustType(postRequest.getType())
            .baseDate(today)
            .isSubmitted(false)
            .orderNumber(yearMaxOrderNumber + 1) // 현재 해당년도의 Max 차수에 +1을 해서 다음 차수 정보 세팅
            .startDate(postRequest.getStartDate())
            .endDate(postRequest.getEndDate())
            .exceptionStartDate(today)
            .exceptionEndDate(LocalDate.of(today.getYear(), Month.DECEMBER, 31))
            .author(postRequest.getAuthor())
            .build();
        Adjust savedAdjust = adjustRepository.save(adjust);

        /* adjustGrade 모든 데이터 추가(카티션 곱) */
        List<Grade> allGrades = gradeRepository.findAll();
        List<AdjustGrade> adjustGradesToSave = new ArrayList<>();
        for (Grade grade : allGrades) {
            AdjustGrade adjustGrade = AdjustGrade.builder()
                .adjust(savedAdjust)
                .grade(grade)
                .build();
            adjustGradesToSave.add(adjustGrade);
        }
        List<AdjustGrade> savedAdjustGrade = adjustGradeRepository.saveAll(adjustGradesToSave);

        /* adjustEmploymentType 모든 데이터 추가(카티션 곱) */
        List<EmploymentType> allEmpTypes = employmentTypeRepository.findAll();
        List<AdjustEmploymentType> adjustEmpTypesToSave = new ArrayList<>();
        for (EmploymentType empType : allEmpTypes) {
            AdjustEmploymentType adjustEmpType = AdjustEmploymentType.builder()
                .adjust(savedAdjust)
                .employmentType(empType)
                .build();
            adjustEmpTypesToSave.add(adjustEmpType);
        }
        adjustEmploymentTypeRepository.saveAll(adjustEmpTypesToSave);

        /* adjustStep 모든 데이터 추가(카디션 곱) 상태(isDone)은 모두 False */
        List<Step> allSteps = stepRepository.findAll();
        List<AdjustStep> adjustStepsToSave = new ArrayList<>();
        for (Step step : allSteps) {
            AdjustStep adjustStep = AdjustStep.builder()
                .adjust(savedAdjust)
                .step(step)
                .isDone(false)
                .build();
            adjustStepsToSave.add(adjustStep);
        }
        adjustStepRepository.saveAll(adjustStepsToSave);

        /* salary_increment_by_rank 테이블 모든 값을 0으로 초기화해서 추가 */
        List<Rank> allRanks = rankRepository.findAll();
        List<SalaryIncrementByRank> ratesToSave = new ArrayList<>();
        for (AdjustGrade adjustGrade : savedAdjustGrade) { // 위에서 저장한 AdjustGrade 리스트 사용
            for (Rank rank : allRanks) {
                SalaryIncrementByRank rateByRank = SalaryIncrementByRank.builder()
                    .adjustGrade(adjustGrade)
                    .rank(rank)
                    .salaryIncrementRate(0.0)
                    .bonusMultiplier(0.0)
                    .build();
                ratesToSave.add(rateByRank);
            }
        }
        salaryIncrementByRankRepository.saveAll(ratesToSave);

        /* Payband_criteria table 추가 조정차수, 직급 카티션 곱 하한값: 80, 상한값 : 120 */
        List<PaybandCriteria> paybandCriteriaToSave = new ArrayList<>();
        for (AdjustGrade adjustGrade : savedAdjustGrade) {
            PaybandCriteria paybandCriteria = PaybandCriteria.builder()
                .adjustGrade(adjustGrade)
                .upperBound(120.0)
                .lowerBound(80.0)
                .build();
            paybandCriteriaToSave.add(paybandCriteria);
        }
        paybandCriteriaRepository.saveAll(paybandCriteriaToSave);
    }

    public Long getBeforeAdjInfoId(Long adjInfoId) {
        List<Adjust> adjusts = adjustRepository.findLatestAdjustInfo(adjInfoId)
            .stream()
            .filter(adjInfo1 -> !adjInfo1.getDeleted())
            .toList();
        if (adjusts.isEmpty()) {
            throw new CustomException(CANNOT_NULL_INPUT);
        }
        Long beforeAdjInfoId = adjusts.get(0).getId();
        return beforeAdjInfoId;
    }
}
