package com.forkwell.vikash.globalline.service

import com.forkwell.vikash.globalline.validator.FileValidator
import com.forkwell.vikash.globalline.dto.EmailReport
import com.forkwell.vikash.globalline.helper.XLSXParser
import com.forkwell.vikash.globalline.store.DataStore
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct


@Service
class FileProcessorService @Autowired constructor(
        val fileValidator: FileValidator,
        val ftpService: FTPService
) {

    lateinit var dataStore: DataStore
    lateinit var compareStore: DataStore
    var LOG = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        dataStore = DataStore()
        compareStore = DataStore()
    }

    // process XLSX file
    fun process(file: MultipartFile): EmailReport {
        val fileName = fileValidator.validate(file)
        LOG.info("New file received: {}", fileName)
        if (compareStore.isEmpty()) {
            LOG.info("Datastore is empty, loading previous file {} to store", fileName)
            loadPreviousFile(fileName)
        }
        LOG.info("Creating a temp file {} to work on", fileName)
        val tempFile = File(fileName)
        FileUtils.copyInputStreamToFile(file.inputStream, tempFile)

        LOG.info("Uploading new file to FTP: {}", fileName)
        ftpService.upload(fileName, tempFile)

        LOG.info("Retriveing the new file content: {}", fileName)
        val newContent = XLSXParser.parse(tempFile)

        LOG.info("Checking if content updated: {}", fileName)
        val contentUpdated = pushToCompareStoreAndCheck(newContent)
        if (contentUpdated) {
            LOG.info("Push updated content to the DataStore: {}", fileName)
            updateContentInStores(newContent)
        }
        val timeNow = SimpleDateFormat("yyyyMMdd-HHmm").format(Date())
        val processedFileName = fileName.substring(0, fileName.length.minus(4)) + timeNow + ".xlsx"

        LOG.info("Uploading file with appended timestamp: {}", processedFileName)
        ftpService.upload(processedFileName, tempFile)

        LOG.info("Deleting tempFile: {}", fileName)
        tempFile.delete()

        LOG.info("Generating email report")
        return generateEmailReport()
    }

    // download previously uploaded file to FTP and load DataStore
    fun loadPreviousFile(fileName: String) {
        try {
            if (ftpService.fetch(fileName)) {
                val previousFile = File(fileName + "_prev")
                val content = XLSXParser.parse(previousFile)
                previousFile.delete()
                updateContentInStores(content)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // load content to CompareStore
    @Synchronized
    fun pushToCompareStoreAndCheck(content: List<String>): Boolean {
        val oldSize = compareStore.size()
        val newSize = content.size
        var updated = oldSize != newSize;
        for (row: String in content) {
            val cells = row.split(",")
            updated = updated || compareStore.putVal(cells[0].trim(), cells[1].trim())
        }
        return updated;
    }

    // clean and load content to DataStore and CompareStore
    @Synchronized
    fun updateContentInStores(content: List<String>) {
        dataStore.clear()
        compareStore.clear()
        for (row: String in content) {
            val cells = row.split(",")
            dataStore.putVal(cells[0].trim(), cells[1].trim())
            compareStore.putVal(cells[0].trim(), cells[1].trim())
        }
    }

    // generate email report for the new content
    fun generateEmailReport(): EmailReport {
        val subject = "Global Line Report | " + SimpleDateFormat("yyyyMMdd-HHmm").format(Date())
        println(subject)
        val reportBuilder = StringBuilder("PFB the updated records").append("/n");
        for ((key, value) in dataStore.getStore()) {
            reportBuilder.append(key + "\t\t" + value).append("/n")
            println(key + "\t\t" + value)
        }
        return EmailReport(subject, reportBuilder.toString())
    }
}