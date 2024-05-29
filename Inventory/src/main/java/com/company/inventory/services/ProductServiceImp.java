package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.util.Util;

@Service
public class ProductServiceImp implements IProductService {

	private ICategoryDao categoryDao;
	private IProductDao productDao;

	public ProductServiceImp(ICategoryDao categoryDao, IProductDao productDao) {
		super();
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}

	@Override
	public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();

		try {
			// Search category to set in the product object
			Optional<Category> category = categoryDao.findById(categoryId);
			if (category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("respuesta NO ok", "-1", "Categoría asociada no encontrada");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

			// Save the product
			Product productSaved = productDao.save(product);

			if (productSaved != null) {
				list.add(productSaved);
				response.getProduct().setProduct(list);
				response.setMetadata("respuesta ok", "00", "Producto guardado");

			} else {
				response.setMetadata("respuesta NO ok", "-1", "Error al guardar el producto");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta NO ok", "-1", "Error al guardar el producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchById(Long id) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();

		try {
			// Search product by id
			Optional<Product> product = productDao.findById(id);
			if (product.isPresent()) {
				byte[] imageDecompressed = Util.decompressZLib(product.get().getPicture());
				product.get().setPicture(imageDecompressed);
				list.add(product.get());
				response.getProduct().setProduct(list);
				response.setMetadata("respuesta ok", "00", "Producto encontrado");
			} else {
				response.setMetadata("respuesta NO ok", "-1", "Producto no encontrado");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta NO ok", "-1", "Error al buscar el producto por id");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchByName(String name) {
		System.out.println("searchByName");
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		List<Product> listAux = new ArrayList<>();

		try {
			// Search product by id
			listAux = productDao.findByNameContainingIgnoreCase(name);
			if (listAux.size() > 0) {
				listAux.stream().forEach((product) -> {
					byte[] imageDecompressed = Util.decompressZLib(product.getPicture());
					product.setPicture(imageDecompressed);
					list.add(product);

				});
				response.getProduct().setProduct(list);
				response.setMetadata("respuesta ok", "00", "Productos encontrados por nombre");
			} else {
				response.setMetadata("respuesta NO ok", "-1", "Productos no encontrados por nombre");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta NO ok", "-1", "Error al buscar los productos por nombre");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> deleteById(Long id) {
		ProductResponseRest response = new ProductResponseRest();

		try {
			// Delete product by id
			productDao.deleteById(id);
			response.setMetadata("respuesta ok", "00", "Producto eliminado");

		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta NO ok", "-1", "Error al eliminar el producto por id");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> search() {
		System.out.println("searchByName");
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		List<Product> listAux = new ArrayList<>();

		try {
			// Search product by id
			listAux = (List<Product>) productDao.findAll();
			if (listAux.size() > 0) {
				listAux.stream().forEach((product) -> {
					byte[] imageDecompressed = Util.decompressZLib(product.getPicture());
					product.setPicture(imageDecompressed);
					list.add(product);
				});
				response.getProduct().setProduct(list);
				response.setMetadata("respuesta ok", "00", "Productos encontrados");
			} else {
				response.setMetadata("respuesta NO ok", "-1", "Productos no encontrados");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta NO ok", "-1", "Error al buscar los productos");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> update(Product product, Long categoryId, Long id) {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();

		try {
			// Search category to set in the product object
			Optional<Category> category = categoryDao.findById(categoryId);
			if (category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("respuesta NO ok", "-1", "Categoría asociada no encontrada");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}


			Optional<Product> productSearch = productDao.findById(id);
			if (productSearch.isPresent()) {
				productSearch.get().setName(product.getName());
				productSearch.get().setPrice(product.getPrice());
				productSearch.get().setQuantity(product.getQuantity());
				productSearch.get().setCategory(product.getCategory());
				productSearch.get().setPicture(product.getPicture());
				
				Product updatedProduct = productDao.save(productSearch.get());
				
				if(updatedProduct != null) {
					list.add(updatedProduct);
					response.getProduct().setProduct(list);
					response.setMetadata("respuesta ok", "00", "Producto actualizado");
				}else {
					response.setMetadata("respuesta NO ok", "-1", "Producto no actualizado");
					return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);
				}
			} else {
				response.setMetadata("respuesta NO ok", "-1", "Producto no encontrado");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			e.getStackTrace();
			response.setMetadata("respuesta NO ok", "-1", "Error al actualizar el producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}

}
