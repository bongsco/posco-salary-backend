package com.bongsco.api.common.utils;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "엑셀 다운로드 관련 API", description = "모든 페이지에 대한 엑셀 다운로드 처리 - RequestParam 이용")

@RestController
@RequestMapping("/adjust/excel")
@RequiredArgsConstructor
public class ExcelDownloadController {
    private final ExcelDownloadService excelDownloadService;

    @GetMapping("/download")
    public void downloadExcel(
        @RequestParam(required = false) Long adjustId,
        @Parameter(
            description = """
                엑셀 다운로드 페이지 타입 선택:
                - 대상자 편성: 대상자(`Subject`), 비대상자(`NonSubject`)
                - 고성과조직 대상자 목록: `highOrgSubject`
                - Payband: 상한초과자(`upperPayband`), 하한초과자(`lowerPayband`)
                - 정기 연봉조정 결과: `adjustResult`
                """,
            schema = @Schema(
                allowableValues = {
                    "Subject", "NonSubject", "highOrgSubject",
                    "upperPayband", "lowerPayband", "adjustResult"
                }
            )
        )
        @RequestParam String pageType,
        HttpServletResponse response
    ) throws IOException {
        excelDownloadService.generateAndWriteExcel(adjustId, pageType, response);
    }
}
