package cl.nutrivida.catalogo.service;

import cl.nutrivida.catalogo.client.ProfesionalClient;
import cl.nutrivida.catalogo.dto.CategoriaRequestDTO;
import cl.nutrivida.catalogo.dto.CategoriaResponseDTO;
import cl.nutrivida.catalogo.dto.ServicioRequestDTO;
import cl.nutrivida.catalogo.dto.ServicioResponseDTO;
import cl.nutrivida.catalogo.model.Categoria;
import cl.nutrivida.catalogo.model.Servicio;
import cl.nutrivida.catalogo.repository.CategoriaRepository;
import cl.nutrivida.catalogo.repository.ServicioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class CatalogoService {
    private final CategoriaRepository categoriaRepository;
    private final ServicioRepository servicioRepository;
    private final ProfesionalClient profesionalClient;

    public CatalogoService(
            CategoriaRepository categoriaRepository,
            ServicioRepository servicioRepository,
            ProfesionalClient profesionalClient) {
        this.categoriaRepository = categoriaRepository;
        this.servicioRepository = servicioRepository;
        this.profesionalClient = profesionalClient;
    }

    public List<CategoriaResponseDTO> categorias(boolean todos, boolean esAdmin) {
        List<CategoriaResponseDTO> resultado = new ArrayList<>();
        for (Categoria categoria : categoriaRepository.findAll()) {
            if (Boolean.TRUE.equals(categoria.getActivo()) || (todos && esAdmin)) {
                resultado.add(convertirCategoria(categoria));
            }
        }
        return resultado;
    }

    public CategoriaResponseDTO categoria(String id) {
        Categoria categoria = buscarCategoria(id);
        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
        }
        return convertirCategoria(categoria);
    }

    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request) {
        return guardarCategoria(new Categoria(), request);
    }

    public CategoriaResponseDTO editarCategoria(String id, CategoriaRequestDTO request) {
        return guardarCategoria(buscarCategoria(id), request);
    }

    private CategoriaResponseDTO guardarCategoria(
            Categoria categoria, CategoriaRequestDTO request) {
        if (!request.activo()
                && categoria.getIdCategoria() != null
                && servicioRepository.existsByIdCategoria(categoria.getIdCategoria())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La categoría tiene servicios asociados");
        }
        categoria.setNombre(request.nombre().strip());
        categoria.setActivo(request.activo());
        Categoria guardada = categoriaRepository.saveAndFlush(categoria);
        return convertirCategoria(guardada);
    }

    @Transactional
    public void eliminarCategoria(String id) {
        if (servicioRepository.existsByIdCategoria(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La categoría tiene servicios asociados");
        }
        categoriaRepository.delete(buscarCategoria(id));
    }

    public List<ServicioResponseDTO> servicios(
            boolean todos, String buscar, String categoria, boolean esAdmin) {
        Map<String, String> profesionales = profesionalClient.obtenerProfesionales();
        String busqueda = buscar.toLowerCase(Locale.ROOT);
        List<ServicioResponseDTO> resultado = new ArrayList<>();
        for (Servicio servicio : servicioRepository.findAll()) {
            boolean visible = Boolean.TRUE.equals(servicio.getActivo()) || (todos && esAdmin);
            boolean mismaCategoria =
                    categoria.isBlank() || categoria.equals(servicio.getIdCategoria());
            String texto =
                    (servicio.getTitulo() + " " + servicio.getDescripcion())
                            .toLowerCase(Locale.ROOT);
            if (visible && mismaCategoria && texto.contains(busqueda)) {
                resultado.add(convertirServicio(servicio, profesionales));
            }
        }
        return resultado;
    }

    public ServicioResponseDTO servicio(String id, boolean esAdmin) {
        Servicio servicio = buscarServicio(id);
        if (!Boolean.TRUE.equals(servicio.getActivo()) && !esAdmin) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado");
        }
        return convertirServicio(servicio, profesionalClient.obtenerProfesionales());
    }

    public ServicioResponseDTO crear(ServicioRequestDTO request) {
        return guardarServicio(new Servicio(), request);
    }

    public ServicioResponseDTO editar(String id, ServicioRequestDTO request) {
        return guardarServicio(buscarServicio(id), request);
    }

    private ServicioResponseDTO guardarServicio(Servicio servicio, ServicioRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.idCategoria()).orElse(null);
        if (categoria == null || !Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona una categoría activa");
        }
        Map<String, String> profesionales = profesionalClient.obtenerProfesionales();
        String profesionalId = request.profesionalId();
        if (profesionalId != null && profesionalId.isBlank()) {
            profesionalId = null;
        }
        if (profesionalId != null && !profesionales.containsKey(profesionalId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Selecciona un profesional activo");
        }

        servicio.setIdCategoria(request.idCategoria());
        servicio.setIdProfesional(profesionalId);
        servicio.setTitulo(request.titulo().strip());
        servicio.setDescripcion(request.descripcion().strip());
        servicio.setPrecio(request.precio());
        servicio.setDescuentoPorcentaje(request.descuento());
        servicio.setModalidad(request.modalidad());
        servicio.setDuracionMinutos((short) request.duracion());
        servicio.setImagenUrl(request.imagen());
        servicio.setActivo(request.activo());
        Servicio guardado = servicioRepository.saveAndFlush(servicio);
        return convertirServicio(guardado, profesionales);
    }

    public void eliminar(String id) {
        Servicio servicio = buscarServicio(id);
        servicio.setActivo(false);
        servicioRepository.save(servicio);
    }

    private Categoria buscarCategoria(String id) {
        return categoriaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    private Servicio buscarServicio(String id) {
        return servicioRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    private CategoriaResponseDTO convertirCategoria(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                Boolean.TRUE.equals(categoria.getActivo()));
    }

    private ServicioResponseDTO convertirServicio(
            Servicio servicio, Map<String, String> profesionales) {
        return new ServicioResponseDTO(
                servicio.getIdServicio(),
                servicio.getIdCategoria(),
                servicio.getIdProfesional(),
                servicio.getTitulo(),
                servicio.getDescripcion(),
                servicio.getPrecio(),
                servicio.getDescuentoPorcentaje(),
                "ONLINE".equals(servicio.getModalidad()) ? "Online" : "Presencial",
                servicio.getDuracionMinutos(),
                servicio.getImagenUrl(),
                profesionales.getOrDefault(servicio.getIdProfesional(), ""),
                Boolean.TRUE.equals(servicio.getActivo()));
    }
}
