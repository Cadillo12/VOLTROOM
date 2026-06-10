package com.voltroom.voltroombackend.service;

import com.voltroom.voltroombackend.dto.DashboardResponse;
import com.voltroom.voltroombackend.entity.Lectura;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final LecturaService lecturaService;
    private final DashboardService dashboardService;

    /**
     * Genera un reporte Excel seguro y formateado utilizando Apache POI.
     * Contiene el historial de lecturas de un sensor específico.
     * 
     * @param sensorId ID del sensor
     * @return ByteArrayInputStream con el archivo binario generado (.xlsx)
     */
    public ByteArrayInputStream exportarLecturasExcel(Long sensorId) {
        List<Lectura> lecturas = lecturaService.listarPorSensor(sensorId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Lecturas");

            // Estilos para encabezado
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Fecha y Hora", "Consumo (kWh)", "Origen", "Observación"};
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Llenar datos
            int rowIdx = 1;
            for (Lectura lectura : lecturas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(lectura.getId());
                row.createCell(1).setCellValue(lectura.getFechaHora() != null ? lectura.getFechaHora().toString() : "N/A");
                row.createCell(2).setCellValue(lectura.getValorKwh() != null ? lectura.getValorKwh().doubleValue() : 0.0);
                row.createCell(3).setCellValue(lectura.getOrigenLectura() != null ? lectura.getOrigenLectura().name() : "N/A");
                row.createCell(4).setCellValue(lectura.getObservacion() != null ? lectura.getObservacion() : "N/A");
            }
            
            // Auto-size columns
            for(int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error crítico al generar el archivo Excel de lecturas", e);
        }
    }

    /**
     * Genera un reporte Excel general con estadísticas globales y todas las lecturas.
     * @return ByteArrayInputStream con el archivo Excel
     */
    public ByteArrayInputStream exportarReporteGeneralExcel() {
        DashboardResponse stats = dashboardService.obtenerDashboard();
        List<Lectura> todasLasLecturas = lecturaService.listar();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // --- HOJA 1: ESTADÍSTICAS ---
            Sheet statsSheet = workbook.createSheet("Estadísticas Globales");
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow1 = statsSheet.createRow(0);
            Cell cellH1 = headerRow1.createCell(0);
            cellH1.setCellValue("Métrica");
            cellH1.setCellStyle(headerStyle);
            
            Cell cellH2 = headerRow1.createCell(1);
            cellH2.setCellValue("Valor");
            cellH2.setCellStyle(headerStyle);

            Object[][] statsData = {
                {"Total Inmuebles", stats.getTotalInmuebles()},
                {"Total Ambientes", stats.getTotalAmbientes()},
                {"Total Sensores (Nodos)", stats.getTotalSensores()},
                {"Lecturas Registradas", stats.getTotalLecturas()},
                {"Consumo Global (kWh)", stats.getConsumoTotalKwh()},
                {"Costo Estimado (S/)", stats.getCostoEstimado()}
            };

            int rowNum = 1;
            for (Object[] stat : statsData) {
                Row row = statsSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat[0].toString());
                if (stat[1] instanceof Number) {
                    row.createCell(1).setCellValue(((Number) stat[1]).doubleValue());
                } else {
                    row.createCell(1).setCellValue(stat[1].toString());
                }
            }
            statsSheet.autoSizeColumn(0);
            statsSheet.autoSizeColumn(1);

            // --- HOJA 2: TODAS LAS LECTURAS ---
            Sheet lecturasSheet = workbook.createSheet("Todas las Lecturas");
            
            CellStyle headerStyle2 = workbook.createCellStyle();
            headerStyle2.setFont(headerFont);
            headerStyle2.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow2 = lecturasSheet.createRow(0);
            String[] columns = {"ID", "Sensor ID", "Fecha y Hora", "Consumo (kWh)", "Origen", "Observación"};
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow2.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerStyle2);
            }

            int rowIdx = 1;
            for (Lectura lectura : todasLasLecturas) {
                Row row = lecturasSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(lectura.getId());
                row.createCell(1).setCellValue(lectura.getSensor() != null ? lectura.getSensor().getId() : 0);
                row.createCell(2).setCellValue(lectura.getFechaHora() != null ? lectura.getFechaHora().toString() : "N/A");
                row.createCell(3).setCellValue(lectura.getValorKwh() != null ? lectura.getValorKwh().doubleValue() : 0.0);
                row.createCell(4).setCellValue(lectura.getOrigenLectura() != null ? lectura.getOrigenLectura().name() : "N/A");
                row.createCell(5).setCellValue(lectura.getObservacion() != null ? lectura.getObservacion() : "N/A");
            }
            
            for(int i = 0; i < columns.length; i++) {
                lecturasSheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el reporte general Excel", e);
        }
    }
}
