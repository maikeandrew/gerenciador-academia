package br.ufersa.academia.model;

import java.time.LocalDate;
import java.util.List;

public class Pagamento {

    private int id; 
    private Aluno aluno; 
    private double valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private StatusPagamento status;

    
    public Pagamento() {
    }

    public Pagamento(Aluno aluno, double valor, LocalDate dataVencimento) {
        this.aluno = aluno;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = StatusPagamento.PENDENTE;
    }


    public void verPagamento() {
        System.out.println("--- Detalhes do Pagamento ---");
        System.out.println("ID Fatura: " + this.id);
        System.out.println("Aluno: " + (this.aluno != null ? this.aluno.getNome() : "Não informado"));
        System.out.println("Valor: R$ " + this.valor);
        System.out.println("Data de Vencimento: " + this.dataVencimento);
        System.out.println("Status: " + this.status);
        if (this.dataPagamento != null) {
            System.out.println("Pago em: " + this.dataPagamento);
        }
    }

    public void registrarPagamento() {
        if (this.status != StatusPagamento.PAGO) {
            this.dataPagamento = LocalDate.now(); 
            this.status = StatusPagamento.PAGO;
            System.out.println("Pagamento confirmado para o aluno: " + this.aluno.getNome());
            
            this.aluno.renovarMatricula();
            
        } else {
            System.out.println("Este pagamento já foi realizado anteriormente.");
        }
    }

    public static List<Pagamento> listarPagamentos() {
        return null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
}