package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.ContagensResponse;
import com.projeto.estagio.mackenzie.service.RelatorioService;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    private final RelatorioService relatorioService;
    private final UsuarioService usuarioService;

    public RelatorioController(RelatorioService relatorioService, UsuarioService usuarioService) {
        this.relatorioService = relatorioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/contagens")
    public ContagensResponse contagens(@AuthenticationPrincipal UserDetails principal) {
        return ContagensResponse.from(
                relatorioService.contagens(usuarioService.buscarPorEmail(principal.getUsername())));
    }
}
