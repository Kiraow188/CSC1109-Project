package com.sitatm.sitatm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class receiptPrinter {
    public static void printReceipt(String name, Date date, String account_no, int transaction_id, double depositwithdraw, double balance, int option){
        try {
            //Create Document instance.
            Document document = new Document();

            String filePath = System.getProperty("user.dir") + "\\Receipt_RID"+transaction_id+".pdf";

            //Create OutputStream instance.
            OutputStream outputStream =
                    new FileOutputStream(new File(filePath));

            //Create PDFWriter instance.
            PdfWriter.getInstance(document, outputStream);

            //Open the document.
            document.open();

            // Create a table with 2 columns
            PdfPTable table = new PdfPTable(new float[] {5f, 2f});

            // Add table headers
            PdfPCell cell1 = new PdfPCell(new Paragraph("Transaction Receipt"));
            PdfPCell cell2 = new PdfPCell();
            table.addCell(cell1);
            table.addCell(cell2);

            // Add transaction info
            table.addCell(new PdfPCell(new Paragraph("Date:")));
            table.addCell(new PdfPCell(new Paragraph(String.valueOf(date))));

            table.addCell(new PdfPCell(new Paragraph("Transaction ID:")));
            table.addCell(new PdfPCell(new Paragraph(String.valueOf(transaction_id))));

            table.addCell(new PdfPCell(new Paragraph("Account Holder Name:")));
            table.addCell(new PdfPCell(new Paragraph(name)));

            table.addCell(new PdfPCell(new Paragraph("Account Number:")));
            table.addCell(new PdfPCell(new Paragraph(String.valueOf(account_no))));

            if (option == 0) {
                table.addCell(new PdfPCell(new Paragraph("Deposit Amount:")));
            } else {
                table.addCell(new PdfPCell(new Paragraph("Withdraw Amount:")));
            }
            table.addCell(new PdfPCell(new Paragraph(String.format("%.2f", depositwithdraw))));

            table.addCell(new PdfPCell(new Paragraph("Available Balance:")));
            table.addCell(new PdfPCell(new Paragraph(String.format("%.2f", balance))));

            // Add the table to the PDF
            document.add(table);

            //Close document and outputStream.
            document.close();
            outputStream.close();

            System.out.println("Receipt generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}