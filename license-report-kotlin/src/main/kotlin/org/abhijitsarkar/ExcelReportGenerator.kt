package org.abhijitsarkar

import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.IndexedColors.RED
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment.CENTER
import org.apache.poi.ss.usermodel.VerticalAlignment.TOP
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import java.io.File
import java.io.FileOutputStream


/**
 * @author Abhijit Sarkar
 */
class ExcelReportGenerator {
    private val headers = arrayOf("License", "License URL", "License URL Status", "Components")
    private val licenseReport = "License Report.xlsx"
    private val log = LoggerFactory.getLogger(ExcelReportGenerator::class.java)

    @EventListener(LicenseGeneratedEvent::class)
    fun generateReport(event: LicenseGeneratedEvent): Unit {
        log.info("Received LicenseGeneratedEvent.")
        val licenses = event.licenses

        val workbook = XSSFWorkbook()

        val regularCs = newCellStyle(workbook, ColumnFormatting.REGULAR)
        val hyperlinkCs = newCellStyle(workbook, ColumnFormatting.HYPERLINK)
        val warnCs = newCellStyle(workbook, ColumnFormatting.WARN)
        val createHelper = workbook.creationHelper
        val headerCs = newCellStyle(workbook, ColumnFormatting.HEADER)

        licenses
                .forEach { name, license ->
                    var rowNum = 0
                    val sheet = workbook.createSheet("$name")

                    sheet.createRow(rowNum++).let { row ->
                        (0..headers.size - 1).forEach { col ->
                            newFormattedCell(sheet, row, headerCs, col)
                                    .setCellValue(headers[col])
                        }
                    }

                    license.forEach { (name, url, components, valid) ->
                        val row = sheet.createRow(rowNum++).apply {
                            heightInPoints *= components.size
                        }

                        newFormattedCell(sheet, row, regularCs, 0).apply {
                            setCellValue(name)
                        }

                        if (url.isNotEmpty()) {
                            val link = createHelper.createHyperlink(HyperlinkType.URL).apply {
                                address = url
                            }

                            newFormattedCell(sheet, row, hyperlinkCs, 1).apply {
                                hyperlink = link
                                setCellValue(url)
                            }
                        }

                        newFormattedCell(sheet, row, regularCs, 3).apply {
                            val comp = components.joinToString(System.getProperty("line.separator"))

                            setCellValue(comp)
                        }

                        val cell = newFormattedCell(sheet, row, regularCs, 2)

                        if (valid) {
                            cell.setCellValue("Valid")
                        } else {
                            log.warn("License link: {} is absent or broken, project: {}.", url, name)

                            (row.firstCellNum..row.lastCellNum - 1)
                                    .map { row.getCell(it) }
                                    .forEach { it?.setCellStyle(warnCs) }

                            cell.setCellValue("Invalid")
                        }
                    }
                }

        if (workbook.numberOfSheets == 0) {
            log.warn("No license files found; skipping report generation.")
        } else {
            workbook.use {
                val reports = File("build", "reports")

                if (reports.exists() && reports.deleteRecursively() || !reports.exists()) {
                    if (reports.mkdirs()) {
                        val licenseReport = File(reports, licenseReport)
                        FileOutputStream(licenseReport).use {
                            workbook.write(it)
                        }

                        log.info("Generated license report at: {}.", licenseReport.absolutePath)
                    } else {
                        log.warn("Failed to create reports directory.")
                    }
                } else {
                    log.warn("Failed to delete reports directory.")
                }
            }
        }
    }

    private fun newCellStyle(workbook: Workbook, colFormatting: ColumnFormatting): CellStyle {
        val cs = workbook.createCellStyle().apply { wrapText = true }

        return when (colFormatting) {
            ColumnFormatting.HEADER -> {
                val headerFont = workbook.createFont().apply {
                    bold = true
                }
                cs.apply {
                    setFont(headerFont)
                    setVerticalAlignment(CENTER)
                }
            }
            ColumnFormatting.HYPERLINK -> {
                val hyperlinkFont = workbook.createFont().apply {
                    underline = Font.U_SINGLE
                    color = IndexedColors.BLUE.index
                }
                cs.apply {
                    setFont(hyperlinkFont)
                    setVerticalAlignment(TOP)
                }
            }
            ColumnFormatting.WARN -> {
                val warnFont = workbook.createFont().apply {
                    color = RED.getIndex()
                }
                cs.apply {
                    setFont(warnFont)
                    setVerticalAlignment(TOP)
                }
            }
            else -> cs.apply { setVerticalAlignment(TOP) }
        }
    }

    private fun newFormattedCell(sheet: Sheet, r: Row, cs: CellStyle, colNum: Int): Cell {
        sheet.autoSizeColumn(colNum)

        return r.createCell(colNum).apply { cellStyle = cs }
    }

    private enum class ColumnFormatting {
        REGULAR, HEADER, HYPERLINK, WARN
    }
}