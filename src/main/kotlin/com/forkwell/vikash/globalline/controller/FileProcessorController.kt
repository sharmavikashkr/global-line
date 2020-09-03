package com.forkwell.vikash.globalline.controller

import com.forkwell.vikash.globalline.dto.EmailReport
import com.forkwell.vikash.globalline.service.FileProcessorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/files")
class FileProcessorController @Autowired constructor(
        val fileProcessorService: FileProcessorService
) {

    @PostMapping("/process")
    fun process(@RequestPart file: MultipartFile): EmailReport {
        return fileProcessorService.process(file);
    }
}