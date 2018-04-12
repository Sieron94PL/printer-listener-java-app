package services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintingService {

    public static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    public static List<String> getAvailablePrintServices() {
        List<String> availablePrinters = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            availablePrinters.add(printService.getName());
        }
        return availablePrinters;
    }

    public static void printPDF(String pathname, String printerName) throws PrinterException, IOException {
        try {
            PDDocument document = PDDocument.load(new File(pathname));
            PrintService myPrintService;
            if (findPrintService(printerName) != null) {
                myPrintService = findPrintService(printerName);
            } else {
                myPrintService = PrintServiceLookup.lookupDefaultPrintService();
            }
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            job.setPrintService(myPrintService);
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Dodaj plik w formacie PDF");
        }
    }
}
