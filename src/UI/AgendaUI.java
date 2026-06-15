package UI;

import core.Agenda;
import core.Gerenciador;
import core.Relatorio;
import core.Tarefa;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AgendaUI extends JFrame {

    private Gerenciador gerenciador;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // Componentes da Tela
    private JLabel lblTarefaAtual;
    private JTextArea txtListaTarefas;
    private JTextField txtNomeTarefa, txtDescricaoTarefa, txtHorarioTarefa;
    private JCheckBox chkCiclico;
    private JTextArea txtHistorico;

    // Paleta de Cores (Estilo Dark/Moderno)
    private final Color COR_FUNDO = new Color(245, 246, 248);
    private final Color COR_CARD = Color.WHITE;
    private final Color COR_TEXTO_PADRAO = new Color(33, 37, 41);
    private final Color COR_AZUL_PRINCIPAL = new Color(43, 108, 176);
    private final Color COR_VERDE_SUCESSO = new Color(39, 110, 144);
    private final Color COR_BORDAS = new Color(218, 224, 233);

    public AgendaUI() {
        this.gerenciador = new Gerenciador();

        // Configurações da Janela
        setTitle("Dashboard - Minha Agenda Inteligente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);
        
        // FAZ ABRIR EM TELA CHEIA
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        // Layout principal com margens ao redor da tela (padding)
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Construção das seções
        painelPrincipal.add(criarPainelTarefaAtual(), BorderLayout.NORTH);
        painelPrincipal.add(criarPainelCentral(), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelInferior(), BorderLayout.SOUTH);

        add(painelPrincipal);

        // Atualiza a tela com os dados iniciais
        atualizarInterface();
    }

    // 1. PAINEL SUPERIOR: Banner elegante para a tarefa em destaque
    private JPanel criarPainelTarefaAtual() {
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setBackground(COR_CARD);
        painelTopo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDAS, 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        lblTarefaAtual = new JLabel("Nenhuma tarefa para agora.", JLabel.LEFT);
        lblTarefaAtual.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTarefaAtual.setForeground(COR_AZUL_PRINCIPAL);
        painelTopo.add(lblTarefaAtual, BorderLayout.CENTER);

        // Botões de ação com cores e fontes limpas
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setBackground(COR_CARD);

        JButton btnConcluir = criarBotaoEstilizado("Concluir ✅", new Color(40, 167, 69));
        JButton btnPular = criarBotaoEstilizado("Pular ↩️", new Color(255, 193, 7));
        JButton btnFalhar = criarBotaoEstilizado("Falhar ❌", new Color(220, 53, 69));

        // Ajuste fino na cor do texto do botão amarelo para leitura
        btnPular.setForeground(Color.BLACK);

        painelBotoes.add(btnConcluir);
        painelBotoes.add(btnPular);
        painelBotoes.add(btnFalhar);
        painelTopo.add(painelBotoes, BorderLayout.EAST);

        // Ações
        btnConcluir.addActionListener(e -> { gerenciador.getAgenda().concluirTarefa(); atualizarInterface(); });
        btnPular.addActionListener(e -> { gerenciador.getAgenda().pularTarefa(); atualizarInterface(); });
        btnFalhar.addActionListener(e -> { gerenciador.getAgenda().falharTarefa(); atualizarInterface(); });

        return painelTopo;
    }

    // 2. PAINEL CENTRAL: Dividido entre Cadastro (Mediano) e Lista de Tarefas
    private JPanel criarPainelCentral() {
        JPanel painelCentro = new JPanel(new BorderLayout(15, 0));
        painelCentro.setBackground(COR_FUNDO);

        // --- ESQUERDA: Form de Cadastro Customizado para ficar MEDIANO ---
        JPanel painelFormWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelFormWrapper.setBackground(COR_FUNDO);
        
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(COR_CARD);
        painelForm.setBorder(criarBordaCustomizada(" Nova Tarefa "));
        // Define uma largura mediana fixa para o formulário não esticar na tela cheia
        painelForm.setPreferredSize(new Dimension(380, 400)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fonteLabels = new Font("Segoe UI", Font.BOLD, 13);

        // Linha 0: Nome
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblNome = new JLabel("Nome:"); lblNome.setFont(fonteLabels);
        painelForm.add(lblNome, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNomeTarefa = new JTextField(); EstilarCampoTexto(txtNomeTarefa);
        painelForm.add(txtNomeTarefa, gbc);

        // Linha 1: Descrição
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblDesc = new JLabel("Descrição:"); lblDesc.setFont(fonteLabels);
        painelForm.add(lblDesc, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtDescricaoTarefa = new JTextField(); EstilarCampoTexto(txtDescricaoTarefa);
        painelForm.add(txtDescricaoTarefa, gbc);

        // Linha 2: Horário
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblHora = new JLabel("Horário:"); lblHora.setFont(fonteLabels);
        painelForm.add(lblHora, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtHorarioTarefa = new JTextField("12:00"); EstilarCampoTexto(txtHorarioTarefa);
        painelForm.add(txtHorarioTarefa, gbc);

        // Linha 3: Cíclico
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblCiclo = new JLabel("Cíclica?"); lblCiclo.setFont(fonteLabels);
        painelForm.add(lblCiclo, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        chkCiclico = new JCheckBox(); chkCiclico.setBackground(COR_CARD);
        painelForm.add(chkCiclico, gbc);

        // Linha 4: Botão Adicionar (Ocupando as duas colunas)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 12, 10, 12);
        JButton btnAdicionar = criarBotaoEstilizado("Adicionar na Lista Dupla", COR_AZUL_PRINCIPAL);
        painelForm.add(btnAdicionar, gbc);

        painelFormWrapper.add(painelForm);

        // --- DIREITA: Lista de Tarefas (Ocupa o resto do espaço dinamicamente) ---
        JPanel painelLista = new JPanel(new BorderLayout());
        painelLista.setBackground(COR_CARD);
        painelLista.setBorder(criarBordaCustomizada(" Lista Ordenada de Hoje "));

        txtListaTarefas = new JTextArea();
        txtListaTarefas.setEditable(false);
        txtListaTarefas.setFont(new Font("Consolas", Font.PLAIN, 14)); // Letra mono-espaçada clássica para listas
        txtListaTarefas.setForeground(COR_TEXTO_PADRAO);
        txtListaTarefas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollLista = new JScrollPane(txtListaTarefas);
        scrollLista.setBorder(BorderFactory.createEmptyBorder());
        painelLista.add(scrollLista, BorderLayout.CENTER);

        painelCentro.add(painelFormWrapper, BorderLayout.WEST);
        painelCentro.add(painelLista, BorderLayout.CENTER);

        // Ação de Cadastrar
        btnAdicionar.addActionListener(e -> {
            try {
                String nome = txtNomeTarefa.getText();
                String desc = txtDescricaoTarefa.getText();
                LocalTime horario = LocalTime.parse(txtHorarioTarefa.getText(), timeFormatter);
                boolean ciclico = chkCiclico.isSelected();

                Tarefa nova = new Tarefa(nome, desc, horario, ciclico);
                gerenciador.getAgenda().adicionarTarefa(nova);

                txtNomeTarefa.setText("");
                txtDescricaoTarefa.setText("");
                chkCiclico.setSelected(false);

                atualizarInterface();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Formato de horário inválido! Use o padrão HH:mm (Ex: 14:30).", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painelCentro;
    }

    // 3. PAINEL INFERIOR: Histórico e Fechamento
    private JPanel criarPainelInferior() {
        JPanel painelBaixo = new JPanel(new BorderLayout(0, 10));
        painelBaixo.setBackground(COR_CARD);
        painelBaixo.setBorder(criarBordaCustomizada(" Histórico de Produtividade "));

        JButton btnFinalizarDia = criarBotaoEstilizado("🏁 Finalizar Ciclo do Dia e Migrar Cíclicas", COR_VERDE_SUCESSO);
        btnFinalizarDia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelBaixo.add(btnFinalizarDia, BorderLayout.NORTH);

        txtHistorico = new JTextArea(6, 40);
        txtHistorico.setEditable(false);
        txtHistorico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtHistorico.setForeground(new Color(100, 110, 120));
        txtHistorico.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JScrollPane scrollHist = new JScrollPane(txtHistorico);
        scrollHist.setBorder(BorderFactory.createLineBorder(COR_BORDAS));
        painelBaixo.add(scrollHist, BorderLayout.CENTER);

        btnFinalizarDia.addActionListener(e -> {
            gerenciador.finalizarDia();
            JOptionPane.showMessageDialog(this, "Relatório arquivado! Nova agenda gerada com sucesso.");
            atualizarInterface();
        });

        return painelBaixo;
    }

    // --- MÉTODOS AUXILIARES DE ESTILIZAÇÃO ---

    private JButton criarBotaoEstilizado(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void EstilarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDAS, 1, true),
            BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
    }

    private TitledBorder criarBordaCustomizada(String titulo) {
        TitledBorder borda = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COR_BORDAS, 1, true), titulo
        );
        borda.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        borda.setTitleColor(COR_AZUL_PRINCIPAL);
        return borda;
    }

    private void atualizarInterface() {
        Agenda agendaAtual = gerenciador.getAgenda();

        // 1. Atualiza a label do topo de forma ultra segura
        String textoBanner = "🎉 Parabéns! Nenhuma tarefa pendente no momento.";
        try {
            if (gerenciador.getAgenda() != null) {
                String nomeTarefa = gerenciador.getAgenda().getTarefaAtual();
                if (nomeTarefa != null && !nomeTarefa.trim().isEmpty()) {
                    textoBanner = "📌 Próxima Atividade: " + nomeTarefa;
                }
            }
        } catch (Exception e) {
            // Caso ocorra qualquer erro de ponteiro na leitura, mantém o padrão seguro
            textoBanner = "🎉 Parabéns! Nenhuma tarefa pendente no momento.";
        }
        lblTarefaAtual.setText(textoBanner);

        // 2. Atualiza a lista usando o método que você adicionou
        txtListaTarefas.setText(agendaAtual.exportarListaEmTexto());

        // 3. Atualiza o Histórico
        StringBuilder sbHist = new StringBuilder();
        Relatorio[] relatorios = gerenciador.getHistorico().getRelatorios();
        if (relatorios.length == 0) {
            sbHist.append("O histórico está limpo. Complete um dia para gerar dados.");
        } else {
            for (Relatorio r : relatorios) {
                if (r.getTitulo() != null) {
                    sbHist.append("📊 ").append(r.getTitulo())
                          .append("  |  ✅ Concluídas: ").append(r.getConcluidas())
                          .append("  |  ↩️ Puladas: ").append(r.getPuladas())
                          .append("  |  ❌ Falhadas: ").append(r.getFalhas())
                          .append("\n_________________________________________________________________________________\n");
                }
            }
        }
        txtHistorico.setText(sbHist.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AgendaUI().setVisible(true);
        });
    }
}