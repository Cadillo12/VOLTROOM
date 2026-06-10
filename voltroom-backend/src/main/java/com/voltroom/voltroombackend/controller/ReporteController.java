package com.voltroom.voltroombackend.controller;

import com.voltroom.voltroombackend.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Endpoint REST para exportar las lecturas de un sensor a formato Excel (.xlsx).
     * Retorna un archivo binario generado mediante Apache POI.
     * 
     * @param sensorId Identificador del sensor
     * @return Archivo Excel adjunto en la respuesta HTTP
     */
    @GetMapping("/lecturas/excel/{sensorId}")
    public ResponseEntity<InputStreamResource> descargarReporteLecturas(@PathVariable Long sensorId) {
        InputStreamResource file = new InputStreamResource(reporteService.exportarLecturasExcel(sensorId));

        String filename = "lecturas_sensor_" + sensorId + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    /**
     * Endpoint REST para exportar todas las estadísticas y lecturas a formato Excel (.xlsx).
     * @return Archivo Excel adjunto en la respuesta HTTP
     */
    @GetMapping("/general/excel")
    public ResponseEntity<InputStreamResource> descargarReporteGeneral() {
        InputStreamResource file = new InputStreamResource(reporteService.exportarReporteGeneralExcel());

        String filename = "reporte_general_voltroom.xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
