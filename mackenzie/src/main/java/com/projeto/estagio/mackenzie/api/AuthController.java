package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.api.ApiDtos.CsrfResponse;
import com.projeto.estagio.mackenzie.api.ApiDtos.LoginRequest;
import com.projeto.estagio.mackenzie.api.ApiDtos.UsuarioResponse;
import com.projeto.estagio.mackenzie.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                          SecurityContextRepository contextRepository,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken csrfToken) {
        return new CsrfResponse(csrfToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse) {
        if (request == null || request.email() == null || request.senha() == null) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.email(), request.senha()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            servletRequest.getSession(true);
            servletRequest.changeSessionId();
            contextRepository.saveContext(context, servletRequest, servletResponse);
            return ResponseEntity.ok(UsuarioResponse.from(
                    usuarioService.buscarPorEmail(authentication.getName())));
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos.");
        } catch (IllegalStateException exception) {
            SecurityContextHolder.clearContext();
            throw new AuthenticationServiceException("Não foi possível iniciar a sessão.");
        }
    }

    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal UserDetails principal) {
        return UsuarioResponse.from(usuarioService.buscarPorEmail(principal.getUsername()));
    }
}