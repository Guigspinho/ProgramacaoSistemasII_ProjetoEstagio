package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.CandidaturaResponse;
import com.projeto.estagio.mackenzie.service.CandidaturaService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {
    private final CandidaturaService candidaturaService;
    private final UsuarioService usuarioService;

    public CandidaturaController(CandidaturaService candidaturaService, UsuarioService usuarioService) {
        this.candidaturaService = candidaturaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/minhas")
    public List<CandidaturaResponse> listarMinhas(@AuthenticationPrincipal UserDetails principal) {
        return candidaturaService.listarMinhas(usuarioService.buscarPorEmail(principal.getUsername()))
                .stream().map(CandidaturaResponse::from).toList();
    }
}
