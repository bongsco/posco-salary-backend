package com.bongsco.api.common.utils;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "엑셀 다운로드 관련 API", description = "모든 페이지에 대한 엑셀 다운로드 처리 - RequestParam 이용")

@RestController
@RequestMapping("/adjust/{adjustId}/excel")
@RequiredArgsConstructor
public class ExcelDownloadController {
    private final ExcelDownloadService excelDownloadService;

    @GetMapping("/download")
    public void downloadExcel(
        @PathVariable Long adjustId,
        @RequestParam String pageType,
        HttpServletResponse response
    ) throws IOException {
        excelDownloadService.generateAndWriteExcel(adjustId, pageType, response);
    }
}
