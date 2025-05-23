package com.bongsco.web.common.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ExcelDownloadService {

    private final ExcelUtils excelUtils;
    private final ExcelDataProvider excelDataProvider;

    public void generateAndWriteExcel(Long adjustId, String pageType, HttpServletResponse response) throws IOException {
        List<String> headers = excelDataProvider.getHeadersByPageType(pageType);
        List<List<String>> data = excelDataProvider.getDataByPageType(adjustId, pageType);

        byte[] excelFile = excelUtils.createExcelFile(headers, data);

        String fileName = URLEncoder.encode(
            "bongsco_" + pageType + "_" + LocalDate.now(),
            StandardCharsets.UTF_8
        );

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        response.getOutputStream().write(excelFile);
        response.flushBuffer();
    }
}

