package com.forkwell.vikash.globalline.helper

import org.apache.commons.io.FileUtils
import org.apache.poi.ss.formula.functions.NumericFunction.LOG
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import java.io.File

class XLSXParser {

    var LOG = LoggerFactory.getLogger(javaClass)

    companion object {
        var LOG = LoggerFactory.getLogger(javaClass)

        fun parse(file: File): List<String> {
            LOG.info("Parsing XLSX file : {}", file.name)
            val fis = FileUtils.openInputStream(file)
            val wb = XSSFWorkbook(fis)
            val sheet: XSSFSheet = wb.getSheetAt(0)
            val rowList = ArrayList<String>()
            for (row: Row in sheet) {
                val sb = StringBuilder()
                for (cell: Cell in row) {
                    sb.append(cell.getStringCellValue()).append(",")
                }
                rowList.add(sb.toString())
            }
            fis.close()
            return rowList
        }
    }
}