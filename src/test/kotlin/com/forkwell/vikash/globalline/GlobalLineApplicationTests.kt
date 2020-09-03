package com.forkwell.vikash.globalline

import com.forkwell.vikash.globalline.controller.FileProcessorController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.util.ResourceUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.function.RequestPredicates.contentType
import org.springframework.web.servlet.function.RequestPredicates.path
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files


@SpringBootTest
class GlobalLineApplicationTests {

	@Autowired
	lateinit var fileProcessorController: FileProcessorController

	@Test
	fun processFile() {
		val file1: File = ResourceUtils.getFile("classpath:example_day_1_users.xlsx")
		val inputStream1 = FileInputStream(file1)
		val multipartFile1: MultipartFile = MockMultipartFile("example_users",
				"example_users.xlsx", "application/vnd.ms-excel", inputStream1)
		fileProcessorController.process(multipartFile1)

		val file2: File = ResourceUtils.getFile("classpath:example_day_2_users.xlsx")
		val inputStream2 = FileInputStream(file2)
		val multipartFile2: MultipartFile = MockMultipartFile("example_users",
				"example_users.xlsx", "application/vnd.ms-excel", inputStream2)
		fileProcessorController.process(multipartFile2)
	}

}
