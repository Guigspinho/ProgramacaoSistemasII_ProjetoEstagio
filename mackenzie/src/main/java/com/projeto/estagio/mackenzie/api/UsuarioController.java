package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.EmpresaRequest;
import com.projeto.estagio.mackenzie.api.ApiDtos.EstudanteRequest;
import com.projeto.estagio.mackenzie.api.ApiDtos.UsuarioResponse;

import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/estudantes")
    public ResponseEntity<UsuarioResponse> cadastrarEstudante(@RequestBody EstudanteRequest request) {
        request = ApiDtos.exigirCorpo(request);
        Estudante estudante = usuarioService.cadastrarEstudante(
                request.nome(), request.email(), request.curso());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.from(estudante));
    }

    @PostMapping("/empresas")
    public ResponseEntity<UsuarioResponse> cadastrarEmpresa(@RequestBody EmpresaRequest request) {
        request = ApiDtos.exigirCorpo(request);
        Empresa empresa = usuarioService.cadastrarEmpresa(
                request.nome(), request.email(), request.nomeEmpresa());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.from(empresa));
    }
}
