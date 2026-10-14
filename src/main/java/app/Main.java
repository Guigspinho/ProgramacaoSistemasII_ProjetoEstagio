package app;

import java.time.LocalDate;
import model.*;

public class Main {
    public static void main(String[] args) {
        Area tecnologia = new Area(1L, "Tecnologia");
        Empresa empresa = new Empresa(1L, "Maria RH", "rh@acme.com", "ACME Software");
        Estudante estudante = new Estudante(2L, "João Silva", "joao@aluno.edu", "Sistemas de Informação");
        Administrador admin = new Administrador(3L, "Admin", "admin@portal.com");

        Vaga vaga = new Vaga(1L, empresa, "Estágio em Desenvolvimento Java",
                "Apoio ao time de back-end", tecnologia, "Itápolis - SP", Modalidade.HIBRIDA, 30);
        Candidatura candidatura = new Candidatura(1L, estudante, vaga, LocalDate.now());

        System.out.println("=== Portal de Estágios ===");
        System.out.println(admin);
        System.out.println(empresa + " (" + empresa.getNomeEmpresa() + ")");
        System.out.println(estudante + " - curso: " + estudante.getCurso());
        System.out.println(vaga);
        System.out.println(candidatura);

        vaga.encerrar();
        System.out.println("Após encerrar -> aberta? " + vaga.estaAberta());
    }
}
