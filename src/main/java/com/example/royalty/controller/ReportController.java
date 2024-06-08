package com.example.royalty.controller;

import com.example.royalty.dao.GenerateReportDAO;
import com.example.royalty.service.ReportService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("")
    public String reportHome(Model model) {
        LocalDateTime startDateTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDateTime = LocalDateTime.now();

        GenerateReportDAO generateReportDAO =
                new GenerateReportDAO(startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("report", generateReportDAO);

        return "report";
    }

    @PostMapping("/master")
    public void reportMaster(GenerateReportDAO generateReportDAO, HttpServletResponse response) throws IOException {
        LocalDateTime startDateTime = parseDate(generateReportDAO.getStartDate() + " 00:00:00", LocalDateTime.now().minusMonths(1));
        LocalDateTime endDateTime = parseDate(generateReportDAO.getEndDate() + " 23:59:59", LocalDateTime.now());

        String fileName = "report_master_" + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "_" +
                endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Master Report");

        List<String[]> rows = reportService.generateMaster(startDateTime, endDateTime);

        int rowCount = 0;

        for (String[] rowData : rows) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            for (String cellData : rowData) {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(cellData);
            }
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private static LocalDateTime parseDate(String dateString, LocalDateTime defaultValue) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
