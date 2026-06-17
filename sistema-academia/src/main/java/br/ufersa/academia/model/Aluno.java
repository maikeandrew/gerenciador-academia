package br.ufersa.academia.model;

import java.time.LocalDate;
import java.util.List;

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

    

    @Override
    public void verUsuario() {
        super.verUsuario();
        System.out.println("Matrícula: " + this.matricula);
        System.out.println("Vencimento da Matrícula: " + this.dataFimMatricula);
        System.out.println("Valor Mensal: R$ " + this.valorMensal);
    }

    

    
    public void renovarMatricula() {
        if (this.dataFimMatricula == null || this.dataFimMatricula.isBefore(LocalDate.now())) {
            
            this.dataFimMatricula = LocalDate.now().plusMonths(1);
        } else {
            
            this.dataFimMatricula = this.dataFimMatricula.plusMonths(1);
        }
        System.out.println("🔄 Matrícula renovada! Nova data de término: " + this.dataFimMatricula);
    }

    
    public boolean realizarCheckIn(boolean temPagamentoAtrasado) {
        System.out.println("Analisando dados de acesso para: " + this.getNome());
        
      
        if (!this.isAtivo()) {
            System.out.println("❌ Acesso Negado: Esta conta está desativada no sistema.");
            return false;
        }

        
        if (this.dataFimMatricula == null || LocalDate.now().isAfter(this.dataFimMatricula)) {
            System.out.println("❌ Acesso Negado: Sua matrícula expirou em " + this.dataFimMatricula + ". Regularize na recepção.");
            return false;
        }
        
        
        if (temPagamentoAtrasado) {
            System.out.println("❌ Acesso Negado: Você possui mensalidades em ATRASADO. Procure a recepção.");
            return false;
        }
        
        System.out.println("✅ Check-in Liberado! Catraca aberta. Bom treino!");
        return true;
    }

   

    public static List<Aluno> listarAlunos() {
        return null;
    }

    public static void editarAluno(Aluno aluno, String nome, String cpf, String login, String senha, boolean ativo, String matricula) {
        if (aluno != null) {
            Usuario.editarUsuario(aluno, nome, cpf, login, senha, ativo);
            aluno.setMatricula(matricula);
        }
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