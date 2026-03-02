package com.example.know_it_all.util

import android.content.Context
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File

object SkillPassportGenerator {
    fun generatePDF(
        context: Context,
        userName: String,
        email: String,
        foundedDate: String,
        skills: List<SkillInfo>,
        trustScore: Float
    ): File? {
        return try {
            val fileName = "Skill_Passport_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)
            
            val title = Paragraph("SKILL PASSPORT")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(24f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(title)
            
            document.add(Paragraph("\n"))
            
            // User info
            val userInfo = Paragraph("User: $userName")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            document.add(userInfo)
            
            val emailInfo = Paragraph("Email: $email")
            document.add(emailInfo)
            
            val dateInfo = Paragraph("Member Since: $foundedDate")
            document.add(dateInfo)
            
            val trustInfo = Paragraph("Trust Score: $trustScore / 5.0")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
            document.add(trustInfo)
            
            document.add(Paragraph("\n"))
            document.add(Paragraph("VERIFIED SKILLS"))
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(14f)
            
            // Skills table
            val table = Table(UnitValue.createPercentArray(floatArrayOf(2f, 2f, 2f, 1f)))
            table.addHeaderCell("Skill")
            table.addHeaderCell("Category")
            table.addHeaderCell("Proficiency")
            table.addHeaderCell("Year")
            
            skills.forEach { skill ->
                table.addCell(skill.name)
                table.addCell(skill.category)
                table.addCell(skill.proficiency)
                table.addCell(skill.year.toString())
            }
            
            document.add(table)
            document.add(Paragraph("\n"))
            
            // Footer
            val footer = Paragraph("This is an official micro-credential from KnowItAll Platform")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE))
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(footer)
            
            document.close()
            pdfDocument.close()
            
            file
        } catch (e: Exception) {
            null
        }
    }

    data class SkillInfo(
        val name: String,
        val category: String,
        val proficiency: String,
        val year: Int
    )
}
