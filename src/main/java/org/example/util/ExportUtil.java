package org.example.util;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.Transacao;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ExportUtil {
    private static final DateTimeFormatter FORMATADOR_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void gerarPlanilhaExcel(List<Transacao> transacoes, String caminhoXlsx) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transações");

        Row cabecalho = sheet.createRow(0);
        cabecalho.createCell(0).setCellValue("ID");
        cabecalho.createCell(1).setCellValue("Tipo");
        cabecalho.createCell(2).setCellValue("Valor");
        cabecalho.createCell(3).setCellValue("Categoria");
        cabecalho.createCell(4).setCellValue("Data");
        cabecalho.createCell(5).setCellValue("Descrição");

        int linha = 1;
        double somaReceitas = 0, somaDespesas = 0;
        for (Transacao t : transacoes) {
            Row row = sheet.createRow(linha++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getTipo());
            row.createCell(2).setCellValue(t.getValor());
            row.createCell(3).setCellValue(t.getCategoria().getNome());
            row.createCell(4).setCellValue(t.getData().format(FORMATADOR_DATA));
            row.createCell(5).setCellValue(t.getDescricao());
            if ("Receita".equalsIgnoreCase(t.getTipo())) {
                somaReceitas += t.getValor();
            } else {
                somaDespesas += t.getValor();
            }
        }

        linha += 1;
        Row rowReceitas = sheet.createRow(linha++);
        rowReceitas.createCell(1).setCellValue("Total Receitas:");
        rowReceitas.createCell(2).setCellValue(somaReceitas);

        Row rowDespesas = sheet.createRow(linha++);
        rowDespesas.createCell(1).setCellValue("Total Despesas:");
        rowDespesas.createCell(2).setCellValue(somaDespesas);

        Row rowSaldo = sheet.createRow(linha++);
        rowSaldo.createCell(1).setCellValue("Saldo Final:");
        rowSaldo.createCell(2).setCellValue(somaReceitas - somaDespesas);

        for (int c = 0; c <= 5; c++) {
            sheet.autoSizeColumn(c);
        }

        try (FileOutputStream fos = new FileOutputStream(caminhoXlsx)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }
    }

    public static void gerarPdf(List<Transacao> transacoes, String caminhoPdf) throws IOException {
        try (PdfWriter writer = new PdfWriter(caminhoPdf);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            float[] colWidths = {30f, 50f, 50f, 70f, 50f, 100f};
            Table tabela = new Table(colWidths);
            tabela.setWidth(UnitValue.createPercentValue(100f));

            tabela.addHeaderCell(
                    new Cell().add(new Paragraph("ID"))
                            .setTextAlignment(TextAlignment.CENTER)
            );
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph("Tipo"))
                            .setTextAlignment(TextAlignment.CENTER)
            );
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph("Valor"))
                            .setTextAlignment(TextAlignment.CENTER)
            );
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph("Categoria"))
                            .setTextAlignment(TextAlignment.CENTER)
            );
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph("Data"))
                            .setTextAlignment(TextAlignment.CENTER)
            );
            tabela.addHeaderCell(
                    new Cell().add(new Paragraph("Descrição"))
                            .setTextAlignment(TextAlignment.CENTER)
            );

            double somaReceitas = 0, somaDespesas = 0;
            for (Transacao t : transacoes) {
                tabela.addCell(new Cell().add(new Paragraph(String.valueOf(t.getId()))));
                tabela.addCell(new Cell().add(new Paragraph(t.getTipo())));
                tabela.addCell(new Cell().add(new Paragraph(
                        String.format(new Locale("pt", "BR"), "R$ %.2f", t.getValor())
                )));
                tabela.addCell(new Cell().add(new Paragraph(t.getCategoria().getNome())));
                tabela.addCell(new Cell().add(new Paragraph(t.getData().format(FORMATADOR_DATA))));
                tabela.addCell(new Cell().add(new Paragraph(t.getDescricao())));

                if ("Receita".equalsIgnoreCase(t.getTipo())) {
                    somaReceitas += t.getValor();
                } else {
                    somaDespesas += t.getValor();
                }
            }

            document.add(tabela);
            document.add(new Paragraph("\n"));

            Paragraph pReceitas = new Paragraph(
                    String.format("Total Receitas: R$ %.2f", somaReceitas)
            ).setBold();
            Paragraph pDespesas = new Paragraph(
                    String.format("Total Despesas: R$ %.2f", somaDespesas)
            ).setBold();
            Paragraph pSaldo = new Paragraph(
                    String.format("Saldo Final: R$ %.2f", somaReceitas - somaDespesas)
            ).setBold();

            document.add(pReceitas);
            document.add(pDespesas);
            document.add(pSaldo);
        }
    }
}
