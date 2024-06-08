package com.example.royalty.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {
    public static List<String[]> readCSV(InputStream inputStream, String[] headers) throws IOException {
        Reader reader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> rows = csvReader.readAll();

        // check headers
        String[] headerRow = rows.get(0);
        for (int i = 0; i < headers.length; i++) {
            if (!headerRow[i].replaceAll("\\s", "").equalsIgnoreCase(headers[i].replaceAll("\\s", ""))) {
                throw new IOException("Invalid file headers");
            }
        }
        // Handle missing values and add empty string
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] == null) {
                    row[i] = "";
                }
            }
        }

        return rows;
    }

    public static List<String[]> readExcel(InputStream inputStream, String[] headers) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            int lastCellNum = row.getLastCellNum();

            for (int i = 0; i < lastCellNum; i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                switch (cell.getCellType()) {
                    case STRING:
                        rowData.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        rowData.add(String.valueOf((int) cell.getNumericCellValue()));
                        break;
                    default:
                        rowData.add("");
                }
            }

            rows.add(rowData.toArray(new String[0]));
        }
        //check headers
        String[] headerRow = rows.get(0);
        for (int i = 0; i < headers.length; i++) {
            if (!headerRow[i].replaceAll("\\s", "").equalsIgnoreCase(headers[i].replaceAll("\\s", ""))) {
                throw new IOException("Invalid file headers");
            }
        }

        workbook.close();
        rows.remove(0); // Assuming the first row is a header and needs to be removed
        return rows;
    }
}
