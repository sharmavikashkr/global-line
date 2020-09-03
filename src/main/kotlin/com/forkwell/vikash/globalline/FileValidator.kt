package com.forkwell.vikash.globalline

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.multipart.MultipartFile

@Component
class FileValidator {

    var LOG = LoggerFactory.getLogger(javaClass)

    // validate the input file
    fun validate(file: MultipartFile): String {
        if (file.isEmpty) {
            LOG.error("Empty File uploaded")
            throw HttpClientErrorException(HttpStatus.BAD_REQUEST, "Empty file uploaded")
        }
        val fileName = file.originalFilename
        if (fileName == null || fileName.isBlank()) {
            LOG.error("Invalid fileName: {}. Must be a valid file", fileName)
            throw HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid fileName")
        }
        if (!".xlsx".equals(fileName.substring(fileName.length.minus(5)))) {
            LOG.error("Invalid file type: {}. Must be an XLSX file", fileName)
            throw HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid file type")
        }
        return fileName
    }
}