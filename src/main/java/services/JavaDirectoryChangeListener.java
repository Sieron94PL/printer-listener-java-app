package services;


import handlers.JsonHandler;
import model.Config;
import org.apache.commons.io.FileUtils;


import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class JavaDirectoryChangeListener implements Runnable {

    private static Path folderDirectory;
    private static String resourcesPathname = new File("config.json").getAbsolutePath();
    private static String printerName;
    private static String folderPathname;

    public static void main(String[] args) throws IOException {

        JsonHandler jsonHandler = new JsonHandler();
        Config config = jsonHandler.getConfig(new File(resourcesPathname));

        if (PrintingService.findPrintService(config.getPrinterName()) == null) {
            config.setAvailablePrinters(PrintingService.getAvailablePrintServices());
            jsonHandler.setConfig(config);
            System.out.println("Nie znaleziono drukarki, lista dostępnych drukarek znajduje się w pliku config.json");
        } else {
            printerName = config.getPrinterName();
            folderPathname = config.getFolderPathname();

            if (Files.exists(FileSystems.getDefault().getPath(folderPathname)) && !folderPathname.isEmpty()) {
                folderDirectory = FileSystems.getDefault().getPath(folderPathname);
                FileUtils.cleanDirectory(new File(folderPathname));
                System.out.println("Usunięto dotychczasowe pliki w " + folderPathname);
                Thread thread = new Thread(new JavaDirectoryChangeListener());
                thread.start();
                System.out.println("Rozpoczęto nasłuchiwanie " + folderPathname);
            } else {
                System.out.println("Nie znaleziono ścieżki do folderu, wprowadź poprawną ścieżkę w pliku config.json");
            }
        }
    }

    @Override
    public void run() {
        try {
            WatchService watchService = folderDirectory.getFileSystem().newWatchService();
            folderDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            //Start infinite loop to watch changes on the directory
            while (true) {
                WatchKey watchKey = watchService.take();
                // poll for file system events on the WatchKey
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    //Calling method
                    takeActionOnChangeEvent(event);
                }
                //Break out of the loop if watch directory got deleted
                if (!watchKey.reset()) {
                    watchKey.cancel();
                    watchService.close();
                    System.out.println("Folder nasłuchiwany został usunięty");
                    //Break out from the loop
                    break;
                }
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private void takeActionOnChangeEvent(WatchEvent<?> event) throws PrinterException, IOException {

        WatchEvent.Kind<?> kind = event.kind();
        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            Path entryCreated = (Path) event.context();
            String directoryFile = folderDirectory.toString() + "\\" + entryCreated.toString();
            System.out.println("Dodano nowy plik " + entryCreated.toString());
            PrintingService.printPDF(directoryFile, printerName);
        }
    }

}
