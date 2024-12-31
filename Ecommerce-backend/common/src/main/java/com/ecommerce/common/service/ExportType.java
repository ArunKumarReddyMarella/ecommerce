package com.ecommerce.common.service;

public enum ExportType {
    PDF(new PdfExportService()),
    CSV(new CsvExportService());

    private final FileExportService service;

    ExportType(FileExportService service){
        this.service = service;
    }

    public FileExportService getService(){
        return service;
    }
}
