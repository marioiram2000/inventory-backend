package com.company.inventory.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.inventory.model.Product;
import com.company.inventory.response.CategoryResponseRest;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.services.IProductService;
import com.company.inventory.util.CategoryExcelExportet;
import com.company.inventory.util.ProductEscelExporter;
import com.company.inventory.util.Util;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api/v1")
public class ProductRestController {

	private IProductService productService;

	public ProductRestController(IProductService productService) {
		super();
		this.productService = productService;
	}

	/**
	 * save
	 * 
	 * @param picture
	 * @param name
	 * @param price
	 * @param quantity
	 * @param categoryId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/products")
	public ResponseEntity<ProductResponseRest> save(@RequestParam("picture") MultipartFile picture,
			@RequestParam("name") String name, @RequestParam("price") int price, @RequestParam("quantity") int quantity,
			@RequestParam("categoryId") Long categoryId) throws IOException {

		Product product = new Product();
		product.setName(name);
		product.setQuantity(quantity);
		product.setPrice(price);
		product.setPicture(Util.compressZLib(picture.getBytes()));

		ResponseEntity<ProductResponseRest> response = productService.save(product, categoryId);

		return response;
	}

	/**
	 * searchById
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/products/{id}")
	public ResponseEntity<ProductResponseRest> searchById(@PathVariable Long id) {
		ResponseEntity<ProductResponseRest> response = productService.searchById(id);
		return response;
	}

	/**
	 * searchByName
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/products/filter/{name}")
	public ResponseEntity<ProductResponseRest> searchByName(@PathVariable String name) {
		System.out.println("searchByNameController");
		ResponseEntity<ProductResponseRest> response = productService.searchByName(name);
		return response;
	}

	/**
	 * deleteById
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/products/{id}")
	public ResponseEntity<ProductResponseRest> deleteById(@PathVariable Long id) {
		ResponseEntity<ProductResponseRest> response = productService.deleteById(id);
		return response;
	}

	/**
	 * search
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/products")
	public ResponseEntity<ProductResponseRest> search() {
		ResponseEntity<ProductResponseRest> response = productService.search();
		return response;
	}

	/**
	 * update
	 * 
	 * @param picture
	 * @param name
	 * @param price
	 * @param quantity
	 * @param categoryId
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@PutMapping("/products/{id}")
	public ResponseEntity<ProductResponseRest> update(@RequestParam("picture") MultipartFile picture,
			@RequestParam("name") String name, @RequestParam("price") int price, @RequestParam("quantity") int quantity,
			@RequestParam("categoryId") Long categoryId, @PathVariable Long id) throws IOException {

		Product product = new Product();
		product.setName(name);
		product.setQuantity(quantity);
		product.setPrice(price);
		product.setPicture(Util.compressZLib(picture.getBytes()));

		ResponseEntity<ProductResponseRest> response = productService.update(product, categoryId, id);

		return response;
	}

	/**
	 * exportToExcell
	 * 
	 * @param response
	 * @throws IOException
	 */
	@GetMapping("/products/export/excel")
	public void exportToExcell(HttpServletResponse response) throws IOException {
		response.setContentType("application/octect-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=product_result.xlsx";
		response.setHeader(headerKey, headerValue);

		ResponseEntity<ProductResponseRest> productResponse = productService.search();
		
		ProductEscelExporter excelExporter = new ProductEscelExporter(
				productResponse.getBody().getProduct().getProduct());

		excelExporter.export(response);
	}
}
