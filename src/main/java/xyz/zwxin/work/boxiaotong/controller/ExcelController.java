package xyz.zwxin.work.boxiaotong.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static xyz.zwxin.work.boxiaotong.utils.ExcelUtil.splitWorkbook;
import static xyz.zwxin.work.boxiaotong.utils.ExcelUtil.writeToZip;

@RestController
public class ExcelController {


    @PostMapping("/splitExcel")
    public ResponseEntity<byte[]> splitExcel(@RequestParam("file") MultipartFile file,
                                             @RequestParam("column") String columnName) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Workbook workbook = WorkbookFactory.create(file.getInputStream(), "UTF-8")) {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("上传的文件不能为空".getBytes(StandardCharsets.UTF_8));
            }
            Map<String, Workbook> workbooks = splitWorkbook(workbook, columnName);
            writeToZip(workbooks, outputStream);
            HttpHeaders headers = new HttpHeaders();
            // 使用URL编码来处理文件名
            headers.setContentDispositionFormData("attachment",
                    URLEncoder.encode("split_excel.zip", "UTF-8"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("处理Excel文件时出现IO异常:" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // 删除临时文件
            try {
                Files.deleteIfExists(new File("split_excel.zip").toPath());
            } catch (IOException ex) {
                // do nothing
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("处理Excel文件时出现未知异常:" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }


    @PostMapping("/skuSplit")
    public ResponseEntity<byte[]> splitSKU(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("上传的文件不能为空".getBytes(StandardCharsets.UTF_8));
        }
        File inputFile = File.createTempFile("input", ".xlsx");
        file.transferTo(inputFile);

        try {
            File resultFile = ExcelUtil.splitSKU(inputFile);


            byte[] resultBytes = Files.readAllBytes(resultFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", resultFile.getName());
            headers.setContentLength(resultBytes.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resultBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("处理Excel文件失败，查看文件可能是空文件或格式不正确：\n" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
