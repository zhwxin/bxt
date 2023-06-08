package xyz.zwxin.work.boxiaotong.controller;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.zwxin.work.boxiaotong.utils.ExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static xyz.zwxin.work.boxiaotong.utils.ExcelUtil.splitWorkbook;
import static xyz.zwxin.work.boxiaotong.utils.ExcelUtil.writeToZip;

@RestController
public class ExcelController {
    @PostMapping("/splitExcel")
    public ResponseEntity<byte[]> splitExcel(@RequestParam("file") MultipartFile file,
                                             @RequestParam("column") String columnName) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Map<String, Workbook> workbooks = splitWorkbook(workbook, columnName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeToZip(workbooks, outputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "split_excel.zip");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    @PostMapping("/skuSplit")
    public ResponseEntity<byte[]> splitSKU(MultipartFile file) throws Exception {
        File inputFile = File.createTempFile("input", ".xlsx");
        file.transferTo(inputFile);
        File resultFile = ExcelUtil.splitSKU(inputFile);
        byte[] resultBytes = Files.readAllBytes(resultFile.toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", resultFile.getName());
        headers.setContentLength(resultBytes.length);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resultBytes);
    }


}
