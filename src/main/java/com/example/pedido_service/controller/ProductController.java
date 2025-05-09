package com.example.pedido_service.controller;

import com.example.pedido_service.dto.CreateProductDTO;
import com.example.pedido_service.dto.ProductDTO;
import com.example.pedido_service.dto.UpdateProductDTO;
import com.example.pedido_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
            description = "Apenas ADMIN ou OPERADOR podem criar um novo produto",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid CreateProductDTO productDTO) throws RuntimeException {
        ProductDTO createdProduct = productService.createProduct(productDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(
            summary = "Busca todos os Produtos registrados",
            description = "Usuários ADMIN, OPERADOR, ou CLIENTE podem acessar todos os produtos registrados"
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAllProducts();

        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Busca um Produto",
            description = "Usuários ADMIN, OPERADOR, ou CLIENTE podem acessar detalhes de um produto específico"
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR', 'SCOPE_CLIENTE')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") Long id) throws RuntimeException {
        ProductDTO product = productService.findProductById(id);

        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Atualiza um Produto",
            description = "Apenas ADMIN ou OPERADOR podem atualizar um produto"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_OPERADOR')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long id, @RequestBody @Valid UpdateProductDTO productDTO) throws RuntimeException {
        ProductDTO updated = productService.updateProduct(id, productDTO);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Deleta um Produto",
            description = "Apenas ADMIN pode desativar um produto"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) throws RuntimeException {
        productService.deleteProductById(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Busca Produtos através de filtros",
            description = "Usuários ADMIN, OPERADOR, ou CLIENTE podem buscar produtos filtrados por descrição, categoria, preço mínimo e preço máximo"
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
