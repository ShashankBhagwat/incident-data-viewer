package com.example.demo.controller;

import com.example.demo.model.DynamicTableData;
import com.example.demo.service.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class UIController {

    private final DatabaseService db;

    public UIController(DatabaseService db) {
        this.db = db;
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "✅ ECS MVC OUTPUT IS WORKING";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tables", db.listTables());
        return "index";
    }

    @PostMapping("/query")
    public String queryTables(HttpServletRequest request, Model model) throws Exception {

        String[] selected = request.getParameterValues("selectedTables");
        List<String> finalTables;
        if (selected == null || selected.length == 0) {
            finalTables = db.listTables();
        } else {
            finalTables = Arrays.asList(selected);
        }
        var data = db.queryMultipleTables(finalTables);

        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(data);

        model.addAttribute("tables", db.listTables());
        model.addAttribute("result", data);
        model.addAttribute("selectedTables", finalTables);
        model.addAttribute("resultJson", jsonData);

        return "index";
    }

    @PostMapping("/download")
    public void downloadTables(
            @RequestParam("jsonData") String jsonData,
            @RequestParam("incidentId") String incidentId,
            HttpServletResponse response) throws Exception {

        if (incidentId == null || incidentId.isBlank()) {
            incidentId = "unknown";
        }

        ObjectMapper mapper = new ObjectMapper();

        List<DynamicTableData> data = mapper.readValue(
                jsonData,
                mapper.getTypeFactory().constructCollectionType(List.class, DynamicTableData.class)
        );

        String fileName = "incident_" + incidentId + ".csv";

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        var out = response.getWriter();

        for (var table : data) {
            out.println("TABLE: " + table.getTableName());
            out.println(String.join(",", table.getColumns()));

            for (var row : table.getRows()) {
                List<String> vals = new ArrayList<>();
                for (var col : table.getColumns()) {
                    vals.add(String.valueOf(row.get(col)));
                }
                out.println(String.join(",", vals));
            }

            out.println();
        }

        out.flush();
    }

    @PostMapping("/downloadExcel")
    public void downloadExcel(@RequestParam("jsonData") String jsonData,
                              @RequestParam("incidentId") String incidentId,
                              HttpServletResponse response) throws Exception {

        if (incidentId == null || incidentId.isBlank()) {
            incidentId = "unknown";
        }

        ObjectMapper mapper = new ObjectMapper();
        List<DynamicTableData> data =
                mapper.readValue(jsonData,
                        mapper.getTypeFactory().constructCollectionType(List.class, DynamicTableData.class));

        String fileName = "incident_" + incidentId + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Incident Data");

        // ---- STYLE DEFINITIONS ----
        var headerStyle = workbook.createCellStyle();
        var headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor((short) 22); // light gray background
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        var cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);

        int rowNum = 0;

        for (var table : data) {

            // Table Title Row
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("TABLE: " + table.getTableName());

            // Header Row
            Row headerRow = sheet.createRow(rowNum++);
            int colIndex = 0;
            for (String col : table.getColumns()) {
                var cell = headerRow.createCell(colIndex++);
                cell.setCellValue(col);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            for (var rowData : table.getRows()) {
                Row row = sheet.createRow(rowNum++);
                colIndex = 0;
                for (String col : table.getColumns()) {
                    Object value = rowData.get(col);
                    var cell = row.createCell(colIndex++);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(cellStyle);
                }
            }

            rowNum++; // blank space between tables
        }

        // Auto-size columns
        for (int i = 0; i < 50; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception ignored) {}
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}