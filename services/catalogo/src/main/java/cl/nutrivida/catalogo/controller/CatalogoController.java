package cl.nutrivida.catalogo.controller;

import cl.nutrivida.catalogo.dto.CategoriaRequestDTO;
import cl.nutrivida.catalogo.dto.CategoriaResponseDTO;
import cl.nutrivida.catalogo.dto.ServicioRequestDTO;
import cl.nutrivida.catalogo.dto.ServicioResponseDTO;
import cl.nutrivida.catalogo.service.CatalogoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Catálogo", description = "Administración de categorías y servicios de NutriVida")
public class CatalogoController {
    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    private boolean esAdmin(Jwt jwt) {
        return jwt != null && "ADMIN".equals(jwt.getClaimAsString("rol"));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias(
            @RequestParam(defaultValue = "false") boolean todos, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(catalogoService.categorias(todos, esAdmin(jwt)));
    }

    @GetMapping("/categorias/{id}")
    @Operation(summary = "Buscar una categoría por su ID")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoria(@PathVariable String id) {
        return ResponseEntity.ok(catalogoService.categoria(id));
    }

    @PostMapping("/categorias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una categoría")
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
            @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogoService.crearCategoria(request));
    }

    @PutMapping("/categorias/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar una categoría")
    public ResponseEntity<CategoriaResponseDTO> editarCategoria(
            @PathVariable String id, @Valid @RequestBody CategoriaRequestDTO request) {
        return ResponseEntity.ok(catalogoService.editarCategoria(id, request));
    }

    @DeleteMapping("/categorias/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una categoría sin servicios asociados")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable String id) {
        catalogoService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/servicios")
    @Operation(summary = "Listar o filtrar servicios")
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios(
            @RequestParam(defaultValue = "false") boolean todos,
            @RequestParam(defaultValue = "") String buscar,
            @RequestParam(defaultValue = "") String categoria,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(catalogoService.servicios(todos, buscar, categoria, esAdmin(jwt)));
    }

    @GetMapping("/servicios/{id}")
    @Operation(summary = "Buscar un servicio por su ID")
    public ResponseEntity<ServicioResponseDTO> obtenerServicio(
            @PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(catalogoService.servicio(id, esAdmin(jwt)));
    }

    @PostMapping("/servicios")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un servicio")
    public ResponseEntity<ServicioResponseDTO> crearServicio(
            @Valid @RequestBody ServicioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(request));
    }

    @PutMapping("/servicios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar un servicio")
    public ResponseEntity<ServicioResponseDTO> editarServicio(
            @PathVariable String id, @Valid @RequestBody ServicioRequestDTO request) {
        return ResponseEntity.ok(catalogoService.editar(id, request));
    }

    @DeleteMapping("/servicios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar un servicio")
    public ResponseEntity<Void> eliminarServicio(@PathVariable String id) {
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
