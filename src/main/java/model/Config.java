package model;

import java.util.List;

public class Config {

    private String folderPathname;
    private String printerName;
    private List<String> availablePrinters;

    public String getFolderPathname() {
        return folderPathname;
    }

    public String getPrinterName() {
        return printerName;
    }

    public List<String> getAvailablePrinters() {
        return availablePrinters;
    }

    public void setAvailablePrinters(List<String> printers) {
        this.availablePrinters = printers;
    }
}
