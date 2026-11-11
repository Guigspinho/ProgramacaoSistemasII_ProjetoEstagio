package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.AreaRequest;
import com.projeto.estagio.mackenzie.api.ApiDtos.AreaResponse;

import com.projeto.estagio.mackenzie.model.Area;
import com.projeto.estagio.mackenzie.service.AreaService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/areas")
public class AreaController {
    private final AreaService areaService;
    private final UsuarioService usuarioService;

    public AreaController(AreaService areaService, UsuarioService usuarioService) {
        this.areaService = areaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<AreaResponse> listar() {
        return areaService.listar().stream().map(AreaResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<AreaResponse> cadastrar(
            @AuthenticationPrincipal UserDetails principal, @RequestBody AreaRequest request) {
        request = ApiDtos.exigirCorpo(request);
        Area area = areaService.cadastrar(usuarioService.buscarPorEmail(principal.getUsername()), request.nome());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AreaResponse.from(area));
    }

    @PutMapping("/{id}")
    public AreaResponse editar(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable Long id, @RequestBody AreaRequest request) {
        request = ApiDtos.exigirCorpo(request);
        return AreaResponse.from(areaService.editar(
                usuarioService.buscarPorEmail(principal.getUsername()), id, request.nome()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@AuthenticationPrincipal UserDetails principal,
                                        @PathVariable Long id) {
        areaService.excluir(usuarioService.buscarPorEmail(principal.getUsername()), id);
        return ResponseEntity.noContent().build();
    }
}
