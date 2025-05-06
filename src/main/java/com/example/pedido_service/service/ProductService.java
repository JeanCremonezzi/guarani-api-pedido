package com.example.pedido_service.service;

import com.example.pedido_service.dto.ProductDTO;
import com.example.pedido_service.model.Product;
import com.example.pedido_service.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO createProduct(ProductDTO productDTO) throws Exception {
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

    public ProductDTO findProductById(Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return convertToDto(product);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setQuantityStock(productDTO.getQuantityStock());
        product = productRepository.save(product);

        return convertToDto(product);
    }

    public void deleteProductById (Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        productRepository.deleteById(id);
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getId());
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
                product.getQuantityStock()
        );
    }
}
