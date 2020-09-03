package com.forkwell.vikash.globalline.service

import com.forkwell.vikash.globalline.props.FTPProperties
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.*

@Service
class FTPService @Autowired constructor(
        val ftpProperties: FTPProperties
) {

    lateinit var ftpClient: FTPClient
    var LOG = LoggerFactory.getLogger(javaClass)

    // check if FTP connection is still available
    fun touch() {
        try {
            if (!this::ftpClient.isInitialized || !ftpClient.isAvailable()) {
                ftpClient = FTPClient()
                ftpClient.connect(ftpProperties.host)
                if (ftpClient.login(ftpProperties.username, ftpProperties.password)) {
                    ftpClient.enterLocalPassiveMode()
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                }
            }
        } catch (ex: Exception) {
            if (this::ftpClient.isInitialized) {
                ftpClient.logout()
                ftpClient.disconnect()
            }
            LOG.error("Error occurred while getting FTP connection: ", ex)
        }
    }

    // fetch file from FTP
    fun fetch(fileName: String): Boolean {
        try {
            touch()
            val previousFile = File(fileName)
            val outputStream: OutputStream = BufferedOutputStream(FileOutputStream(previousFile))
            var downloaded = ftpClient.retrieveFile(ftpProperties.workspace + fileName, outputStream)
            outputStream.close()
            return downloaded;
        } catch (ex: Exception) {
            LOG.error("Error occurred while downloading file {} from FTP: ", fileName, ex)
            return false
        }
    }

    // upload file to FTP
    fun upload(fileName: String, file: File) {
        try {
            touch()
            val fis = FileInputStream(file)
            ftpClient.storeFile(ftpProperties.workspace + fileName, fis)
            fis.close()
        } catch (ex: Exception) {
            LOG.error("Error occurred while uploading file {} to FTP: ", fileName, ex)
        }
    }

}