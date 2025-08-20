package ru.graviton.profiles.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("test")
public class TestController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void consumeFiles(@RequestPart("data") MultipartFile data) throws IOException {
        File file = new File("d:\\1111.jpeg");
        FileUtils.writeByteArrayToFile(file, data.getBytes());

        log.info("File received: {}", data.getOriginalFilename());
    }
}
