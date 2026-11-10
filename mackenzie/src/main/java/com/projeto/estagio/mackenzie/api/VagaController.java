package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.CandidaturaResponse;
import com.projeto.estagio.mackenzie.api.ApiDtos.VagaRequest;
import com.projeto.estagio.mackenzie.api.ApiDtos.VagaResponse;

import com.projeto.estagio.mackenzie.model.Modalidade;
import com.projeto.estagio.mackenzie.model.Vaga;
import com.projeto.estagio.mackenzie.service.CandidaturaService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import com.projeto.estagio.mackenzie.service.VagaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vagas")
public class VagaController {
    private final VagaService vagaService;
    private final CandidaturaService candidaturaService;
    private final UsuarioService usuarioService;

    public VagaController(VagaService vagaService, CandidaturaService candidaturaService,
                          UsuarioService usuarioService) {
        this.vagaService = vagaService;
        this.candidaturaService = candidaturaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<VagaResponse> listarAbertas(
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Modalidade modalidade) {
        return vagaService.listarAbertas(areaId, modalidade).stream().map(VagaResponse::from).toList();
    }

    @GetMapping("/minhas")
    public List<VagaResponse> listarDaEmpresa(@RequestHeader("X-Usuario-Id") Long usuarioId) {
        return vagaService.listarDaEmpresa(usuarioService.buscarPorId(usuarioId))
                .stream().map(VagaResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<VagaResponse> publicar(@RequestHeader("X-Usuario-Id") Long usuarioId,
                                                  @RequestBody VagaRequest request) {
        request = ApiDtos.exigirCorpo(request);
        Vaga vaga = vagaService.publicar(usuarioService.buscarPorId(usuarioId),
                request.titulo(), request.descricao(), request.areaId(), request.localizacao(),
                request.modalidade(), request.cargaHorariaSemanal());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VagaResponse.from(vaga));
    }

    @PutMapping("/{id}")
    public VagaResponse editar(@RequestHeader("X-Usuario-Id") Long usuarioId,
                               @PathVariable Long id, @RequestBody VagaRequest request) {
        request = ApiDtos.exigirCorpo(request);
        return VagaResponse.from(vagaService.editar(usuarioService.buscarPorId(usuarioId), id,
                request.titulo(), request.descricao(), request.areaId(), request.localizacao(),
                request.modalidade(), request.cargaHorariaSemanal()));
    }

    @PatchMapping("/{id}/encerramento")
    public ResponseEntity<Void> encerrar(@RequestHeader("X-Usuario-Id") Long usuarioId,
                                         @PathVariable Long id) {
        vagaService.encerrar(usuarioService.buscarPorId(usuarioId), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/candidaturas")
    public ResponseEntity<CandidaturaResponse> candidatar(
            @RequestHeader("X-Usuario-Id") Long usuarioId, @PathVariable Long id) {
        CandidaturaResponse response = CandidaturaResponse.from(
                candidaturaService.candidatar(usuarioService.buscarPorId(usuarioId), id));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}/candidaturas")
    public List<CandidaturaResponse> listarCandidatos(
            @RequestHeader("X-Usuario-Id") Long usuarioId, @PathVariable Long id) {
        return candidaturaService.listarCandidatos(usuarioService.buscarPorId(usuarioId), id)
                .stream().map(CandidaturaResponse::from).toList();
    }
}
