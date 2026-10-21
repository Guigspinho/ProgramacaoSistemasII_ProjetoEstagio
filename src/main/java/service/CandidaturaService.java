package service;

import java.util.List;
import model.*;

public interface CandidaturaService {
    Candidatura candidatar(Usuario ator, Long vagaId);
    List<Candidatura> listarMinhas(Usuario ator);
    List<Candidatura> listarCandidatos(Usuario ator, Long vagaId);
}
