package br.ufersa.academia;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import br.ufersa.academia.model.Aluno;
import br.ufersa.academia.model.Instrutor;
import br.ufersa.academia.model.Pagamento;
import br.ufersa.academia.model.dao.AlunoDAO;
import br.ufersa.academia.model.dao.InstrutorDAO;
import br.ufersa.academia.model.dao.PagamentoDAO;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InstrutorDAO instrutorDAO = new InstrutorDAO();
        AlunoDAO alunoDAO = new AlunoDAO();
        PagamentoDAO pagamentoDAO = new PagamentoDAO(); 
        
        // --- REGRA DO RELATÓRIO: Ação obrigatória ao iniciar o sistema ---
        System.out.println("Inicializando sistema e verificando pendências...");
        pagamentoDAO.verificaPagamentosAtrasados(); 
        
        Object usuarioLogado = null;
        String perfilDeAcesso = "";

        System.out.println("\n=== SISTEMA ACADEMIA ===");
        
        // --- FASE 1: AUTENTICAÇÃO ---
        while (usuarioLogado == null) {
            System.out.print("Usuário/Login: ");
            String login = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            Instrutor func = instrutorDAO.autenticar(login, senha);
            if (func != null) {
                usuarioLogado = func;
                perfilDeAcesso = func.isEhGerente() ? "GERENTE" : "INSTRUTOR";
            } else {
                Aluno aluno = alunoDAO.autenticar(login, senha);
                if (aluno != null) {
                    usuarioLogado = aluno;
                    perfilDeAcesso = "ALUNO";
                }
            }

            if (usuarioLogado == null) {
                System.out.println("❌ Login ou senha inválidos. Tente novamente.\n");
            }
        }

        System.out.println("\n🎉 Login realizado com sucesso! Perfil: " + perfilDeAcesso);

        // --- FASE 2: DIRECIONAMENTO DE TELAS ---
        switch (perfilDeAcesso) {
            case "GERENTE":
                menuGerente(scanner, (Instrutor) usuarioLogado, alunoDAO, instrutorDAO, pagamentoDAO);
                break;
            case "INSTRUTOR":
                menuInstrutor(scanner, (Instrutor) usuarioLogado, alunoDAO, pagamentoDAO);
                break;
            case "ALUNO":
                menuAluno(scanner, (Aluno) usuarioLogado, pagamentoDAO, alunoDAO);
                break;
        }

        System.out.println("Sessão encerrada. Até logo!");
        scanner.close();
    }

    // ==========================================================
    // MENUS PRINCIPAIS
    // ==========================================================
    
    private static void menuGerente(Scanner scanner, Instrutor gerente, AlunoDAO alunoDAO, InstrutorDAO instrutorDAO, PagamentoDAO pgDAO) {
        int opcao = 0;
        while (opcao != 4) {
            System.out.println("\n--- PAINEL DO GERENTE: " + gerente.getNome().toUpperCase() + " ---");
            System.out.println("1. Gerenciar Instrutores");
            System.out.println("2. Gerenciar Alunos");
            System.out.println("3. Gerenciar Mensalidades/Pagamentos");
            System.out.println("4. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt(); scanner.nextLine();

            switch(opcao) {
                case 1: submenuInstrutores(scanner, instrutorDAO); break;
                case 2: submenuAlunos(scanner, alunoDAO, gerente.getId()); break;
                case 3: submenuPagamentos(scanner, pgDAO, alunoDAO); break;
                case 4: break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void menuInstrutor(Scanner scanner, Instrutor instrutor, AlunoDAO alunoDAO, PagamentoDAO pgDAO) {
        int opcao = 0;
        while (opcao != 3) {
            System.out.println("\n--- PAINEL DO INSTRUTOR: " + instrutor.getNome().toUpperCase() + " ---");
            System.out.println("1. Gerenciar Alunos");
            System.out.println("2. Gerenciar Mensalidades/Pagamentos");
            System.out.println("3. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt(); scanner.nextLine();

            switch(opcao) {
                case 1: submenuAlunos(scanner, alunoDAO, instrutor.getId()); break;
                case 2: submenuPagamentos(scanner, pgDAO, alunoDAO); break;
                case 3: break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private static void menuAluno(Scanner scanner, Aluno aluno, PagamentoDAO pgDAO, AlunoDAO alunoDAO) {
        int opcao = 0;
        while (opcao != 4) {
            System.out.println("\n--- PORTAL DO ALUNO: " + aluno.getNome().toUpperCase() + " ---");
            System.out.println("1. Consultar Meus Dados");
            System.out.println("2. Ver Meu Histórico de Mensalidades");
            System.out.println("3. Realizar Check-In na Academia");
            System.out.println("4. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt(); scanner.nextLine();

            switch(opcao) {
                case 1: 
                    aluno.verUsuario(); // Usa o método do Model!
                    break;
                case 2: 
                    System.out.println("\n--- MEU HISTÓRICO ---"); 
                    List<Pagamento> historico = pgDAO.listarPorAluno(aluno.getCpf());
                    if(historico.isEmpty()) System.out.println("Nenhum pagamento registrado.");
                    for (Pagamento p : historico) {
                        System.out.println("Vencimento: " + p.getDataVencimento() + " | Status: " + p.getStatus() + " | Valor: " + p.getValor());
                    }
                    break;
                case 3: 
                    System.out.println("\n--- PROCESSANDO CHECK-IN ---");
                    boolean atrasado = alunoDAO.possuiPagamentoAtrasado(aluno.getCpf());
                    aluno.realizarCheckIn(atrasado);
                    break;
                case 4: break;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    // ==========================================================
    // SUBMENUS DE GERENCIAMENTO 
    // ==========================================================
    
    private static void submenuInstrutores(Scanner scanner, InstrutorDAO instrutorDAO) {
        System.out.println("\n[ GERENCIAR INSTRUTORES ]");
        System.out.println("1. Listar Instrutores | 2. Cadastrar Novo | 3. Voltar");
        System.out.print("Opção: ");
        int op = scanner.nextInt(); scanner.nextLine();
        
        if (op == 1) {
            for(Instrutor i : instrutorDAO.listar()) {
                System.out.println("CPF: " + i.getCpf() + " | Nome: " + i.getNome() + " | Gerente: " + i.isEhGerente());
            }
        } else if (op == 2) {
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("CPF: "); String cpf = scanner.nextLine();
            System.out.print("Login: "); String log = scanner.nextLine();
            System.out.print("Senha: "); String sen = scanner.nextLine();
            System.out.print("É gerente? (true/false): "); boolean ger = scanner.nextBoolean();
            
            Instrutor novo = new Instrutor(nome, cpf, log, sen, ger);
            if (instrutorDAO.cadastrar(novo)) System.out.println("✅ Cadastrado com sucesso!");
        }
    }

    private static void submenuAlunos(Scanner scanner, AlunoDAO alunoDAO, int idInstrutorResponsavel) {
        System.out.println("\n[ GERENCIAR ALUNOS ]");
        System.out.println("1. Listar Alunos | 2. Cadastrar Novo | 3. Voltar");
        System.out.print("Opção: ");
        int op = scanner.nextInt(); scanner.nextLine();
        
        if (op == 1) {
            for(Aluno a : alunoDAO.listar()) {
                System.out.println("CPF: " + a.getCpf() + " | Nome: " + a.getNome() + " | Vencimento: " + a.getDataFimMatricula());
            }
        } else if (op == 2) {
            System.out.print("Nome: "); String nome = scanner.nextLine();
            System.out.print("CPF: "); String cpf = scanner.nextLine();
            System.out.print("Login: "); String log = scanner.nextLine();
            System.out.print("Senha: "); String sen = scanner.nextLine();
            System.out.print("Matrícula: "); String mat = scanner.nextLine();
            System.out.print("Valor Mensalidade: "); double valor = scanner.nextDouble();
            
            Aluno novo = new Aluno(nome, cpf, log, sen, mat, valor, idInstrutorResponsavel);
            if (alunoDAO.cadastrar(novo)) System.out.println("✅ Aluno cadastrado com sucesso!");
        }
    }

    private static void submenuPagamentos(Scanner scanner, PagamentoDAO pgDAO, AlunoDAO alunoDAO) {
        System.out.println("\n[ GERENCIAR PAGAMENTOS ]");
        System.out.println("1. Gerar Nova Mensalidade | 2. Listar Todos | 3. Confirmar Pagamento | 4. Voltar");
        System.out.print("Opção: ");
        int op = scanner.nextInt(); scanner.nextLine();
        
        if (op == 1) {
            System.out.print("Digite o CPF do aluno: ");
            String cpf = scanner.nextLine();
            
            // Busca o aluno pelo CPF para pegar o ID e o Valor dele
            Aluno alunoEncontrado = null;
            int idAlunoNoBanco = 0; // Precisamos do ID real dele no banco
            
            for(Aluno a : alunoDAO.listar()) {
                if(a.getCpf().equals(cpf)) {
                    alunoEncontrado = a;
                    // Como não colocamos getId() no Aluno explícito no loop, você pode precisar 
                    // adaptar se o Aluno não tiver o atributo 'id' mapeado da classe Usuario
                    // O ideal é que o Aluno tenha o getId() herdado de Usuario.
                    idAlunoNoBanco = a.getId(); 
                    break;
                }
            }
            
            if (alunoEncontrado != null) {
                // Cria com vencimento para daqui a 30 dias
                Pagamento novoPg = new Pagamento(alunoEncontrado, alunoEncontrado.getValorMensal(), LocalDate.now().plusMonths(1));
                if (pgDAO.cadastrar(novoPg, idAlunoNoBanco)) {
                    System.out.println("✅ Mensalidade gerada com sucesso!");
                }
            } else {
                System.out.println("❌ Aluno não encontrado.");
            }
            
        } else if (op == 2) {
            for(Pagamento p : pgDAO.listar()) {
                System.out.println("ID: " + p.getId() + " | Valor: " + p.getValor() + " | Status: " + p.getStatus() + " | Vencimento: " + p.getDataVencimento());
            }
        } else if (op == 3) {
            System.out.print("Digite o ID do Pagamento a confirmar: ");
            int idPg = scanner.nextInt(); scanner.nextLine();
            System.out.print("Digite o CPF do aluno pagador: ");
            String cpfAluno = scanner.nextLine();

            if (pgDAO.confirmarPagamento(idPg)) {
                List<Aluno> alunos = alunoDAO.listar();
                for (Aluno a : alunos) {
                    if (a.getCpf().equals(cpfAluno)) {
                        a.renovarMatricula(); 
                        alunoDAO.editar(a);   
                        System.out.println("✅ Pagamento registrado e matrícula renovada com sucesso!");
                        break;
                    }
                }
            } else {
                System.out.println("❌ Falha ao confirmar pagamento.");
            }
        }
    }
}