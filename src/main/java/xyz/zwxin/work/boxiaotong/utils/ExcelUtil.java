package xyz.zwxin.work.boxiaotong.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

public class ExcelUtil {

    /**
     * 根据指定列的值将工作簿拆分为多个工作簿，每个工作簿包含指定列值相同的行
     *
     * @param workbook   要拆分的工作簿
     * @param columnName 指定列名
     * @return 包含拆分后的工作簿的映射，其中键为指定列值，值为包含对应行的工作簿
     * @throws IOException 如果创建新工作簿时出现 I/O 异常
     */
    public static Map<String, Workbook> splitWorkbook(Workbook workbook, String columnName) throws IOException {
        Sheet sheet = workbook.getSheetAt(0);
        int columnNumber = getColumnNumber(sheet, columnName);
        Map<String, Workbook> workbooks = new HashMap<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(columnNumber);
            if (cell == null) {
                continue;
            }
            String cellValue = cell.getStringCellValue();
            Workbook targetWorkbook = workbooks.get(cellValue);
            if (targetWorkbook == null) {
                targetWorkbook = WorkbookFactory.create(true);
                workbooks.put(cellValue, targetWorkbook);
            }
            Sheet targetSheet = targetWorkbook.createSheet();
            targetSheet = targetWorkbook.getSheetAt(0);
            Row targetRow = targetSheet.createRow(targetSheet.getLastRowNum() + 1);
            // 复制第一行表头
            if (targetSheet.getLastRowNum() == 0) {
                Row firstRow = sheet.getRow(0);
                for (int j = 0; j < firstRow.getLastCellNum(); j++) {
                    Cell sourceCell = firstRow.getCell(j);
                    if (sourceCell == null) {
                        continue;
                    }
                    Cell targetCell = targetRow.createCell(j);
                    CellStyle newCellStyle = targetCell.getSheet().getWorkbook().createCellStyle();
                    if (sourceCell instanceof HSSFCell && newCellStyle instanceof HSSFCellStyle) {
                        newCellStyle.cloneStyleFrom((HSSFCellStyle) sourceCell.getCellStyle());
                    } else if (sourceCell instanceof XSSFCell && newCellStyle instanceof XSSFCellStyle) {
                        newCellStyle.cloneStyleFrom((XSSFCellStyle) sourceCell.getCellStyle());
                    }
                    targetCell.setCellStyle(newCellStyle);
                    targetCell.setCellValue(sourceCell.getStringCellValue());
                }
                targetRow = targetSheet.createRow(targetSheet.getLastRowNum() + 1);
            }
            // 复制数据
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell sourceCell = row.getCell(j);
                if (sourceCell == null) {
                    continue;
                }
                Cell targetCell = targetRow.createCell(j);
                CellStyle newCellStyle = targetCell.getSheet().getWorkbook().createCellStyle();
                if (sourceCell instanceof HSSFCell && newCellStyle instanceof HSSFCellStyle) {
                    newCellStyle.cloneStyleFrom((HSSFCellStyle) sourceCell.getCellStyle());
                } else if (sourceCell instanceof XSSFCell && newCellStyle instanceof XSSFCellStyle) {
                    newCellStyle.cloneStyleFrom((XSSFCellStyle) sourceCell.getCellStyle());
                }
                targetCell.setCellStyle(newCellStyle);
                switch (sourceCell.getCellType()) {
                    case BLANK:
                        targetCell.setCellValue("");
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        break;
                    case ERROR:
                        targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        break;
                    case NUMERIC:
                        targetCell.setCellValue(sourceCell.getNumericCellValue());
                        break;
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        break;
                    default:
                        targetCell.setCellValue("");
                }
            }
        }
        return workbooks;
    }


    /**
     * 获取指定列的列号
     *
     * @param sheet      工作表对象
     * @param columnName 列名
     * @return 如果列名不存在于工作表的第一行中抛出
     */
    private static int getColumnNumber(Sheet sheet, String columnName) {
        Row row = sheet.getRow(0);
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                continue;
            }
            String cellValue = cell.getStringCellValue();
            if (columnName.equals(cellValue)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }


    /**
     * 将多个Workbook对象写入到一个zip文件中
     *
     * @param workbooks    包含多个Workbook对象的Map
     * @param outputStream 输出流
     * @throws IOException 如果写入zip文件时出现异常
     */
    public static void writeToZip(Map<String, Workbook> workbooks, ByteArrayOutputStream outputStream) throws IOException {
        try (ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(outputStream)) {
            for (String workbookName : workbooks.keySet()) {
                Workbook workbook = workbooks.get(workbookName);
                ByteArrayOutputStream workbookOutputStream = new ByteArrayOutputStream();
                workbook.write(workbookOutputStream);
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(workbookName + ".xlsx");
                zipOutputStream.putArchiveEntry(zipEntry);
                IOUtils.copy(new ByteArrayInputStream(workbookOutputStream.toByteArray()), zipOutputStream);
                zipOutputStream.closeArchiveEntry();
            }
        }
    }


    /**
     * 将输入文件中的商品sku详情拆分成多行，并将结果写入到新文件中
     *
     * @param inputFile 输入文件
     * @return 输出文件
     * @throws Exception 如果文件读写出现异常
     */
    public static File splitSKU(File inputFile) throws Exception {
        File outputFile = processExcelFile(inputFile);

        outputFile = processExcel(inputFile, outputFile);

        return outputFile;
    }

    /**
     * 从输入文件中读取数据，将每个商品sku详情拆分成多行，并将数据写入到输出文件中
     *
     * @param inputFile  输入文件
     * @param outputFile 输出文件
     * @return 输出文件
     * @throws IOException 如果文件读写出现异常
     */

    public static File processExcel(File inputFile, File outputFile) throws IOException {
        //读取Excel文件
        FileInputStream inputStream = new FileInputStream(inputFile);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        //获取列名行
        Row columnNameRow = sheet.getRow(0);
        //获取商品sku详情列的索引
        int skuDetailColumnIndex = -1;
        for (int i = 0; i < columnNameRow.getLastCellNum(); i++) {
            Cell cell = columnNameRow.getCell(i);
            if (cell.getStringCellValue().equals("商品sku详情")) {
                skuDetailColumnIndex = i;
                break;
            }
        }
        if (skuDetailColumnIndex == -1) {
            throw new IllegalArgumentException("商品sku详情列不存在");
        }


        //获取所有的行数据
        List<Map<String, String>> rowDataList = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            List<Map<String, Object>> skuList = parseSkuDetail(sheet.getRow(i).getCell(skuDetailColumnIndex).getStringCellValue());
            for (int j = 0; j < skuList.size(); j++) {
                Map<String, String> rowData = new HashMap<>();
                for (int k = 0; k < row.getLastCellNum(); k++) {
                    String columnName = columnNameRow.getCell(k).getStringCellValue();
                    Cell cell = row.getCell(k);
                    if (k == skuDetailColumnIndex) {
                        rowData.put("sku_id", String.valueOf(skuList.get(j).get("sku_id")));
                        rowData.put("sku名称", String.valueOf(skuList.get(j).get("sku_name")));
                        rowData.put("sku现价", String.valueOf(skuList.get(j).get("sku_current_price")));
                        rowData.put("sku原价", String.valueOf(skuList.get(j).get("sku_original_price")));
                        rowData.put("sku库存", String.valueOf(skuList.get(j).get("sku_stock")));
                    } else {
                        try {
                            rowData.put(columnName, cell.getStringCellValue());
                        } catch (IllegalStateException e) {
                            rowData.put(columnName, String.valueOf(cell.getNumericCellValue()));
                        }
                    }
                }
                rowDataList.add(rowData);
            }
        }

        writeToFile(rowDataList, outputFile);
        return outputFile;
    }


    /**
     * 将数据写入到Excel文件中，如果文件已经存在，则按照已有的列名顺序写入，否则新建一个空的工作簿
     *
     * @param rowDataList 数据列表，每个元素为一个Map，表示一行数据，Map的key为列名，value为对应的值
     * @param file        要写入的文件
     * @throws IOException 如果文件读写出现异常
     */
    public static void writeToFile(List<Map<String, String>> rowDataList, File file) throws IOException {
        XSSFWorkbook workbook;
        Sheet sheet;
        Map<String, Integer> colIndexMap = new LinkedHashMap<>();

        if (file.exists()) {
            // 如果文件已经存在，读取已有的列名顺序
            try (FileInputStream inputStream = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(inputStream);
                sheet = workbook.getSheetAt(0);
                Row firstRow = sheet.getRow(0);
                for (Cell cell : firstRow) {
                    colIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
                }
            }
        } else {
            // 如果文件不存在，新建一个空的工作簿
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet();
        }

        // 写入数据
        int rowIndex = sheet.getLastRowNum() + 1;
        for (Map<String, String> rowData : rowDataList) {
            Row row = sheet.createRow(rowIndex++);
            for (Map.Entry<String, String> entry : rowData.entrySet()) {
                String colName = entry.getKey();
                String value = entry.getValue();
                Integer colIndex = colIndexMap.get(colName);
                if (colIndex != null) {
                    Cell cell = row.createCell(colIndex);
                    cell.setCellValue(value != null ? value : "");
                }
            }
        }

        // 保存文件
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }
    }


    /**
     * 解析商品sku详情字符串，返回一个Map列表，每个Map表示一个sku的详情
     *
     * @param skuDetail 商品sku详情字符串，格式为JSON数组
     * @return 包含sku详情的Map列表
     */
    public static List<Map<String, Object>> parseSkuDetail(String skuDetail) {
        List<Map<String, Object>> result = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(skuDetail);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("sku_id", jsonObject.getString("sku_id"));
            skuMap.put("sku_name", jsonObject.getString("sku_name"));
            skuMap.put("sku_current_price", jsonObject.getDouble("sku_current_price"));
            skuMap.put("sku_original_price", jsonObject.getDouble("sku_original_price"));
            skuMap.put("sku_stock", jsonObject.getInteger("sku_stock"));
            result.add(skuMap);
        }
        return result;
    }


    /**
     * 处理Excel文件，提取所需列并生成新的Excel文件
     *
     * @param inputFile 待处理的Excel文件
     * @return 生成的新Excel文件
     * @throws IOException 如果读取或写入文件时发生错误，则抛出IOException
     */
    public static File processExcelFile(File inputFile) throws IOException {
        // 读取Excel文件
        Workbook workbook = WorkbookFactory.create(inputFile);
        Sheet sheet = workbook.getSheetAt(0);

        // 获取第一行的列名
        Row firstRow = sheet.getRow(0);
        int columnCount = firstRow.getLastCellNum();
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            Cell cell = firstRow.getCell(i);
            if (cell != null) {
                String columnName = cell.getStringCellValue();
                if (!"商品sku详情".equals(columnName)) {
                    columnNames.add(columnName);
                } else {
                    columnNames.add("sku_id");
                    columnNames.add("sku名称");
                    columnNames.add("sku现价");
                    columnNames.add("sku原价");
                    columnNames.add("sku库存");
                }
            }
        }

        // 创建新的Excel文件并添加列名
        XSSFWorkbook newWorkbook = new XSSFWorkbook();
        XSSFSheet newSheet = newWorkbook.createSheet();
        XSSFRow newRow = newSheet.createRow(0);
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            XSSFCell newCell = newRow.createCell(i);
            newCell.setCellValue(columnName);
        }

        // 保存新的Excel文件
        File outputFile = new File("output.xlsx");
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        newWorkbook.write(outputStream);
        outputStream.close();

        return outputFile;
    }


}













