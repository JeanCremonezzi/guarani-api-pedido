package com.example.pedido_service.service;

import com.example.pedido_service.dto.CreateProductDTO;
import com.example.pedido_service.dto.ProductDTO;
import com.example.pedido_service.dto.UpdateProductDTO;
import com.example.pedido_service.model.Product;
import com.example.pedido_service.repository.ProductRepository;
import com.example.pedido_service.specification.ProductSpecifications;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO createProduct(CreateProductDTO productDTO) throws RuntimeException {
        productRepository.findByDescription(productDTO.getDescription()).ifPresent((e) -> {
            throw new DuplicateKeyException("Duplicated description");
        });

        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);

        return convertToDto(savedProduct);
    }

    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDTO findProductById(Long id) throws RuntimeException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return convertToDto(product);
    }

    public ProductDTO updateProduct(Long id, UpdateProductDTO productDTO) throws RuntimeException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setQuantityStock(productDTO.getQuantityStock());
        product.setDisabled(productDTO.isDisabled());
        product = productRepository.save(product);

        return convertToDto(product);
    }

    public void deleteProductById (Long id) throws RuntimeException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setDisabled(true);

        productRepository.save(product);
    }

    public List<ProductDTO> findFilteredProducts(String description, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> spec = Specification.where(ProductSpecifications.descriptionContains(description))
                .and(ProductSpecifications.categoryEquals(category))
                .and(ProductSpecifications.priceGreaterThanOrEqualTo(minPrice))
                .and(ProductSpecifications.priceLessThanOrEqualTo(maxPrice));

        return productRepository.findAll(spec).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Product convertToEntity(CreateProductDTO productDTO) {
        Product product = new Product();
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setQuantityStock(productDTO.getQuantityStock());
        return product;
    }

    private ProductDTO convertToDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getQuantityStock(),
                product.isDisabled()
        );
    }
}
