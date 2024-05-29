package com.company.inventory.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.company.inventory.model.Product;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;


public class ProductEscelExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private List<Product> products;

	public ProductEscelExporter(List<Product> products) {
		this.products = products;
		workbook = new XSSFWorkbook();
	}

	private void writeHEaderLine() {
		sheet = workbook.createSheet("Resultado");
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();

		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);

		style.setFont(font);

		createCell(row, 0, "ID", style);
		createCell(row, 1, "Nombre", style);
		createCell(row, 2, "Precio", style);
		createCell(row, 3, "Cantidad", style);
		createCell(row, 4, "Categoría", style);
		createCell(row, 5, "Imagen", style);
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);

		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}
	
	private void writeDataLines() {
		int rowCount = 1;
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		
		for(Product product: products) {
			Row row= sheet.createRow(rowCount ++);
			int columnCount = 0;
			createCell(row, columnCount++, String.valueOf(product.getId()), style);
			createCell(row, columnCount++, String.valueOf(product.getName()), style);
			createCell(row, columnCount++, String.valueOf(product.getPrice()), style);
			createCell(row, columnCount++, String.valueOf(product.getQuantity()), style);
			createCell(row, columnCount++, String.valueOf(product.getCategory().getName()), style);
			// Insertar imagen en la celda correspondiente
	        int imageHeightInPoints = insertBase64Image(row, columnCount++, product.getPicture());
	        
	        // Ajustar la altura de la fila para que coincida con la altura de la imagen
	        row.setHeightInPoints(imageHeightInPoints);
	    }
	}

	// Método para insertar una imagen en formato Base64 en una celda
	private int insertBase64Image(Row row, int columnCount, byte[] imageBytes) {
	    int imageHeightInPoints = 0;
	    try {
	        // Convertir la imagen Base64 a BufferedImage
	        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

	        // Escalar la imagen si es necesario
	        int width = bufferedImage.getWidth();
	        int height = bufferedImage.getHeight();
	        double aspectRatio = (double) width / height;
	        double targetWidth = 100; // Ancho objetivo de la imagen en píxeles
	        double targetHeight = targetWidth / aspectRatio;
	        BufferedImage scaledImage = new BufferedImage((int) targetWidth, (int) targetHeight, BufferedImage.TYPE_INT_RGB);
	        scaledImage.createGraphics().drawImage(bufferedImage.getScaledInstance((int) targetWidth, (int) targetHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

	        // Convertir la imagen BufferedImage a un arreglo de bytes
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        ImageIO.write(scaledImage, "png", byteArrayOutputStream);
	        byte[] bytes = byteArrayOutputStream.toByteArray();

	        // Agregar la imagen al libro de Excel
	        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
	        CreationHelper helper = workbook.getCreationHelper();
	        Drawing<?> drawing = row.getSheet().createDrawingPatriarch();
	        ClientAnchor anchor = helper.createClientAnchor();
	        anchor.setCol1(columnCount);
	        anchor.setRow1(row.getRowNum());
	        Picture pict = drawing.createPicture(anchor, pictureIdx);
	        pict.resize(); // Cambiar el tamaño de la imagen según el tamaño de la celda
	        
	        // Calcular la altura de la imagen en puntos
	        imageHeightInPoints = (int) (targetHeight * ((double) sheet.getDefaultRowHeightInPoints() / scaledImage.getHeight()));

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return imageHeightInPoints;
	}

	
	public void export(HttpServletResponse response) throws IOException {
		writeHEaderLine();
		writeDataLines();
		ServletOutputStream servletOutput = response.getOutputStream();
		workbook.write(servletOutput);
		workbook.close();
		servletOutput.close();
	}
}
