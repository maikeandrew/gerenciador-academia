package br.ufersa.academia.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ufersa.academia.model.dao.AlunoDAO;

public class Aluno extends Usuario {

    private String matricula;
    private double valorMensal;
    private LocalDate dataFimMatricula;
    private int idInstrutorResponsavel;

    public Aluno() {
        super();
    }

    public Aluno(String nome, String cpf, String login, String senha, String matricula, double valorMensal, int idInstrutorResponsavel) {
        super(nome, cpf, login, senha);
        this.matricula = matricula;
        this.valorMensal = valorMensal;
        this.idInstrutorResponsavel = idInstrutorResponsavel;
        this.setAtivo(true);
        this.dataFimMatricula = LocalDate.now().plusMonths(1);
    }

    public void verAluno() {
        super.verUsuario();
        System.out.println("Matricula: " + this.matricula);
        System.out.println("Vencimento da Matricula: " + this.dataFimMatricula);
        System.out.println("Valor Mensal: R$ " + this.valorMensal);
    }

    public void vincularInstrutor(Instrutor instrutor) {
        if (instrutor != null) {
            this.idInstrutorResponsavel = instrutor.getId();

            if (this.getCpf() != null && !this.getCpf().isBlank()) {
                new AlunoDAO().editar(this);
            }
        }
    }

    public void renovarMatricula() {
        if (this.dataFimMatricula == null || this.dataFimMatricula.isBefore(LocalDate.now())) {
            this.dataFimMatricula = LocalDate.now().plusMonths(1);
        } else {
            this.dataFimMatricula = this.dataFimMatricula.plusMonths(1);
        }
        System.out.println("Matricula renovada! Nova data de termino: " + this.dataFimMatricula);
    }

    public boolean estaVigente() {
        return this.isAtivo()
                && this.dataFimMatricula != null
                && !LocalDate.now().isAfter(this.dataFimMatricula);
    }

    public boolean realizarCheckIn() {
        boolean temPagamentoAtrasado = false;

        if (this.getCpf() != null && !this.getCpf().isBlank()) {
            temPagamentoAtrasado = new AlunoDAO().possuiPagamentoAtrasado(this.getCpf());
        }

        return realizarCheckIn(temPagamentoAtrasado);
    }

    public boolean realizarCheckIn(boolean temPagamentoAtrasado) {
        System.out.println("Analisando dados de acesso para: " + this.getNome());

        if (!this.isAtivo()) {
            System.out.println("Acesso Negado: Esta conta esta desativada no sistema.");
            return false;
        }

        if (this.dataFimMatricula == null || LocalDate.now().isAfter(this.dataFimMatricula)) {
            System.out.println("Acesso Negado: Sua matricula expirou em " + this.dataFimMatricula + ". Regularize na recepcao.");
            return false;
        }

        if (temPagamentoAtrasado) {
            System.out.println("Acesso Negado: Voce possui mensalidades em ATRASADO. Procure a recepcao.");
            return false;
        }

        System.out.println("Check-in Liberado! Catraca aberta. Bom treino!");
        return true;
    }

    public static Aluno criarAluno(String nome, String cpf, String login, String senha, String matricula, double valorMensal, int idInstrutorResponsavel) {
        Aluno aluno = new Aluno(nome, cpf, login, senha, matricula, valorMensal, idInstrutorResponsavel);
        new AlunoDAO().cadastrar(aluno);
        return aluno;
    }

    public static Aluno criarAluno(String nome, String cpf, String login, String senha, Instrutor instrutor, LocalDate dataNascimento, String telefone, String email, double valorMensal) {
        int idInstrutor = instrutor != null ? instrutor.getId() : 0;
        Aluno aluno = new Aluno(nome, cpf, login, senha, gerarMatricula(cpf), valorMensal, idInstrutor);
        new AlunoDAO().cadastrar(aluno);
        return aluno;
    }

    public static List<Aluno> listarAlunos() {
        return new AlunoDAO().listar();
    }

    public static void editarAluno(Aluno aluno, String nome, String cpf, String login, String senha, boolean ativo, String matricula) {
        if (aluno != null) {
            Usuario.editarUsuario(aluno, nome, cpf, login, senha, ativo);
            aluno.setMatricula(matricula);
            new AlunoDAO().editar(aluno);
        }
    }

    public static void editarAluno(Aluno aluno, String nome, String cpf, String login, String senha, boolean ativo, Instrutor instrutor, LocalDate dataNascimento, String telefone, String email) {
        if (aluno != null) {
            Usuario.editarUsuario(aluno, nome, cpf, login, senha, ativo);
            aluno.setIdInstrutorResponsavel(instrutor != null ? instrutor.getId() : 0);
            new AlunoDAO().editar(aluno);
        }
    }

    public static void excluirAluno(Aluno aluno) {
        if (aluno != null && aluno.getCpf() != null && !aluno.getCpf().isBlank()) {
            new AlunoDAO().desativar(aluno.getCpf());
            aluno.setAtivo(false);
        }
    }

    public static List<Aluno> listarAlunosVinculados(Instrutor instrutor) {
        if (instrutor == null) {
            return new ArrayList<>();
        }

        List<Aluno> alunosVinculados = new ArrayList<>();
        for (Aluno aluno : new AlunoDAO().listar()) {
            if (aluno.getIdInstrutorResponsavel() == instrutor.getId()) {
                alunosVinculados.add(aluno);
            }
        }
        return alunosVinculados;
    }

    public static List<Aluno> listarAlunosInadimplentes() {
        AlunoDAO alunoDAO = new AlunoDAO();
        List<Aluno> alunosInadimplentes = new ArrayList<>();

        for (Aluno aluno : alunoDAO.listar()) {
            if (aluno.getCpf() != null && alunoDAO.possuiPagamentoAtrasado(aluno.getCpf())) {
                alunosInadimplentes.add(aluno);
            }
        }
        return alunosInadimplentes;
    }

    private static String gerarMatricula(String cpf) {
        if (cpf != null && !cpf.isBlank()) {
            String digitos = cpf.replaceAll("\\D", "");
            if (!digitos.isBlank()) {
                return "MAT-" + digitos;
            }
        }

        return "MAT-" + System.currentTimeMillis();
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public LocalDate getDataFimMatricula() {
        return dataFimMatricula;
    }

    public void setDataFimMatricula(LocalDate dataFimMatricula) {
        this.dataFimMatricula = dataFimMatricula;
    }

    public int getIdInstrutorResponsavel() {
        return idInstrutorResponsavel;
    }

    public void setIdInstrutorResponsavel(int idInstrutorResponsavel) {
        this.idInstrutorResponsavel = idInstrutorResponsavel;
    }
}
