package com.example.pedido_service.controller;

import com.example.pedido_service.dto.CreateProductDTO;
import com.example.pedido_service.dto.ProductDTO;
import com.example.pedido_service.dto.UpdateProductDTO;
import com.example.pedido_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Products", description = "Products endpoints")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Cria um novo Produto",
            description = "Recebe a descrição (nome), preço, categoria e quantidade em estoque"
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid CreateProductDTO productDTO) throws RuntimeException {
        ProductDTO createdProduct = productService.createProduct(productDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(
            summary = "Busca todos os Produtos registrados"
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAllProducts();

        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Busca um Produto",
            description = "Recebe o ID do Produto buscado"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long id) throws RuntimeException {
        ProductDTO product = productService.findProductById(id);

        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Atualiza um Produto",
            description = "Recebe o ID e altera os campos permitidos"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long id, @RequestBody @Valid UpdateProductDTO productDTO) throws RuntimeException {
        ProductDTO updated = productService.updateProduct(id, productDTO);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Deleta um Produto",
            description = "Busca um Produto pelo ID e o desativa"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) throws RuntimeException {
        productService.deleteProductById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Busca Produtos através de filtros",
            description = "Retorna todos os Produtos que se encaixem nos filtros fornecidos"
    )
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public List<ProductDTO> getFilteredProducts(
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice) {
        return productService.findFilteredProducts(description, category, minPrice, maxPrice);
    }
}
