package com.bongsco.api.common.utils;

import static com.bongsco.api.common.exception.ErrorCode.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.bongsco.api.common.exception.CustomException;
@Component
public class ExcelUtils {

    public byte[] createExcelFile(List<String> headers, List<List<String>> rows) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Sheet1");

            // 헤더 작성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // 데이터 작성
            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(i + 1);
                List<String> rowData = rows.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    Object val = rowData.get(j);
                    cell.setCellValue(val != null ? val.toString() : "");
                }
            }

            // 파일로 변환
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new CustomException(EXCEL_DOWNLOAD_MAKE);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new CustomException(EXCEL_DOWNLOAD_MAKE);
            }
        }
    }
}

