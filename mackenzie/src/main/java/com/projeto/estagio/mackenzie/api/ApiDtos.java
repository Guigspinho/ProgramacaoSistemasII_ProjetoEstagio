package com.projeto.estagio.mackenzie.api;

import com.projeto.estagio.mackenzie.model.Area;
import com.projeto.estagio.mackenzie.model.Candidatura;
import com.projeto.estagio.mackenzie.model.Empresa;
import com.projeto.estagio.mackenzie.model.Estudante;
import com.projeto.estagio.mackenzie.model.Perfil;
import com.projeto.estagio.mackenzie.model.Usuario;
import com.projeto.estagio.mackenzie.model.Vaga;
import java.time.LocalDate;

public final class ApiDtos {
    private ApiDtos() { }

    public static <T> T exigirCorpo(T corpo) {
        if (corpo == null) {
            throw new IllegalArgumentException("Corpo da requisição obrigatório.");
        }
        return corpo;
    }

    public record EstudanteRequest(String nome, String email, String curso) { }
    public record EmpresaRequest(String nome, String email, String nomeEmpresa) { }
    public record AreaRequest(String nome) { }
    public record VagaRequest(String titulo, String descricao, Long areaId, String localizacao,
                              com.projeto.estagio.mackenzie.model.Modalidade modalidade,
                              int cargaHorariaSemanal) { }

    public record UsuarioResponse(Long id, String nome, String email, Perfil perfil,
                                  String curso, String nomeEmpresa) {
        public static UsuarioResponse from(Usuario usuario) {
            String curso = usuario instanceof Estudante estudante ? estudante.getCurso() : null;
            String empresa = usuario instanceof Empresa empresaUsuario
                    ? empresaUsuario.getNomeEmpresa() : null;
            return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                    usuario.getPerfil(), curso, empresa);
        }
    }

    public record AreaResponse(Long id, String nome) {
        public static AreaResponse from(Area area) {
            return new AreaResponse(area.getId(), area.getNome());
        }
    }

    public record VagaResponse(Long id, Long empresaId, String nomeEmpresa, String titulo,
                               String descricao, AreaResponse area, String localizacao,
                               com.projeto.estagio.mackenzie.model.Modalidade modalidade,
                               int cargaHorariaSemanal,
                               com.projeto.estagio.mackenzie.model.SituacaoVaga situacao) {
        public static VagaResponse from(Vaga vaga) {
            return new VagaResponse(vaga.getId(), vaga.getEmpresa().getId(),
                    vaga.getEmpresa().getNomeEmpresa(), vaga.getTitulo(), vaga.getDescricao(),
                    AreaResponse.from(vaga.getArea()), vaga.getLocalizacao(), vaga.getModalidade(),
                    vaga.getCargaHorariaSemanal(), vaga.getSituacao());
        }
    }

    public record CandidaturaResponse(Long id, Long estudanteId, String nomeEstudante,
                                      String emailEstudante, String curso, Long vagaId,
                                      String tituloVaga, LocalDate dataInscricao) {
        public static CandidaturaResponse from(Candidatura candidatura) {
            Estudante estudante = candidatura.getEstudante();
            return new CandidaturaResponse(candidatura.getId(), estudante.getId(),
                    estudante.getNome(), estudante.getEmail(), estudante.getCurso(),
                    candidatura.getVaga().getId(), candidatura.getVaga().getTitulo(),
                    candidatura.getDataInscricao());
        }
    }

    public record ContagensResponse(long estudantes, long empresas, long vagasAbertas,
                                    long vagasEncerradas, long candidaturas) {
        public static ContagensResponse from(long[] contagens) {
            return new ContagensResponse(contagens[0], contagens[1], contagens[2],
                    contagens[3], contagens[4]);
        }
    }

    public record ErroResponse(java.time.Instant timestamp, int status, String erro,
                               String mensagem, String caminho) { }
}
