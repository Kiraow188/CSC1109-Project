package com.sitatm.sitatm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


public class receiptPrinter {
    private static String name;
    private static String date;
    private static String account_no;
    private static int transaction_id;
    private static double depositwithdraw;
    private static double balance;
    private static int option;

    public receiptPrinter(String name, String date, String account_no, int transaction_id, double depositwithdraw, double balance, int option) {
        this.name = name;
        this.date = date;
        this.account_no = account_no;
        this.transaction_id = transaction_id;
        this.depositwithdraw = depositwithdraw;
        this.balance = balance;
        this.option = option;

    }

    public void printReceipt(){
        try {
            //Create Document instance.
            Document document = new Document();

            String filePath = System.getProperty("user.dir") + "\\Test.pdf";

            //Create OutputStream instance.
            OutputStream outputStream =
                    new FileOutputStream(new File(filePath));

            //Create PDFWriter instance.
            PdfWriter.getInstance(document, outputStream);

            //Open the document.
            document.open();

            String info = String.format("%s             %d", date, transaction_id);

            //adding paragraphs to the PDF
            document.add(new Paragraph("             SIT ATM            "));
            document.add(new Paragraph("                                       "));
            document.add(new Paragraph("TRANSACTION RECEIPT"));
            document.add(new Paragraph("                                       "));
            document.add(new Paragraph("Date                       REF"));
            document.add(new Paragraph(info));
            document.add(new Paragraph("Account Holder Name: " + name));
            document.add(new Paragraph("Account Number: " + account_no));
            if (option == 0) {
                document.add(new Paragraph("Deposit Amount          Available Bal"));
            }else{
                document.add(new Paragraph("Withdraw Amount         Available Bal"));
            }
            document.add(new Paragraph(String.format("%.2f                    %.2f", depositwithdraw, balance)));

            //Close document and outputStream.
            document.close();
            outputStream.close();

            System.out.println("Pdf created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}