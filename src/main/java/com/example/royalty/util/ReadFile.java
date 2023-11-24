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
    public static List<String[]> readCSV(InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        return csvReader.readAll();
    }

    public static List<String[]> readExcel(InputStream inputStream) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        rowData.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        rowData.add(String.valueOf((int)cell.getNumericCellValue()));
                        break;
                    default:
                        rowData.add("");
                }
            }
            rows.add(rowData.toArray(new String[0]));
        }
        workbook.close();
        rows.remove(0);
        return rows;
    }
}
