package service;

import java.util.Map;
import model.Usuario;

public interface RelatorioService {
    /** Rótulo -> quantidade (estudantes, empresas, vagas abertas, vagas encerradas, candidaturas). */
    Map<String, Long> contagens(Usuario ator);
}
