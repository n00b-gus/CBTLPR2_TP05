import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class TP05 extends JFrame {

    public TP05() {
        setTitle("TP05");
        setSize(520, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PainelThreads painel = new PainelThreads();
        add(painel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TP05 janela = new TP05();
            janela.setVisible(true);
        });
    }
}

class PainelThreads extends JPanel {

    private static final String VAL_INICIAL = "1000";
    private static final String PRIORIDADE_1_PADRAO = "1";
    private static final String PRIORIDADE_2_PADRAO = "10";
    private static final int PRIORIDADE_MIN = Thread.MIN_PRIORITY;
    private static final int PRIORIDADE_MAX = Thread.MAX_PRIORITY;

    private static final int ITERACOES_CARGA_CPU = 250_000;

    private final JTextField txtMaximo;
    private final JTextField txtPrioridade1;
    private final JTextField txtPrioridade2;

    private final JButton btnIniciar;
    private final JButton btnLimpar;

    private final DefaultListModel<String> modeloLista1;
    private final DefaultListModel<String> modeloLista2;
    private final JList<String> lista1;
    private final JList<String> lista2;

    private final JLabel lblFim1;
    private final JLabel lblFim2;

    private boolean thread1Fim;
    private boolean thread2Fim;

    public PainelThreads() {
        setLayout(new BorderLayout());

        txtMaximo = new JTextField(VAL_INICIAL, 10);
        txtPrioridade1 = new JTextField(PRIORIDADE_1_PADRAO, 10);
        txtPrioridade2 = new JTextField(PRIORIDADE_2_PADRAO, 10);

        btnIniciar = new JButton("Iniciar");
        btnLimpar = new JButton("Limpar");

        modeloLista1 = new DefaultListModel<>();
        modeloLista2 = new DefaultListModel<>();

        lista1 = new JList<>(modeloLista1);
        lista2 = new JList<>(modeloLista2);

        lblFim1 = new JLabel("Final", SwingConstants.CENTER);
        lblFim2 = new JLabel("Final", SwingConstants.CENTER);

        lblFim1.setVisible(false);
        lblFim2.setVisible(false);

        JPanel topo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        topo.add(new JLabel("Máximo:"), gbc);

        gbc.gridx = 1;
        topo.add(txtMaximo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topo.add(new JLabel("Prioridade 1:"), gbc);

        gbc.gridx = 1;
        topo.add(txtPrioridade1, gbc);

        gbc.gridx = 2;
        topo.add(new JLabel("Número entre 1 e 10"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        topo.add(new JLabel("Prioridade 2:"), gbc);

        gbc.gridx = 1;
        topo.add(txtPrioridade2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        topo.add(btnIniciar, gbc);

        add(topo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 25, 0));
        centro.add(criarColunaLista(lista1, lblFim1));
        centro.add(criarColunaLista(lista2, lblFim2));
        add(centro, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.add(btnLimpar);
        add(rodape, BorderLayout.SOUTH);

        btnIniciar.addActionListener(e -> iniciarContagem());
        btnLimpar.addActionListener(e -> limparTela());
    }

    private JPanel criarColunaLista(JList<String> lista, JLabel lblFinal) {
        JPanel coluna = new JPanel(new BorderLayout(5, 5));
        coluna.add(new JScrollPane(lista), BorderLayout.CENTER);
        coluna.add(lblFinal, BorderLayout.SOUTH);
        return coluna;
    }

    private void iniciarContagem() {
        try {
            int max = Integer.parseInt(txtMaximo.getText().trim());
            int p1 = Integer.parseInt(txtPrioridade1.getText().trim());
            int p2 = Integer.parseInt(txtPrioridade2.getText().trim());

            if (max <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "O valor máximo deve ser maior que zero.",
                    "Valor Inválido",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (p1 < PRIORIDADE_MIN || p1 > PRIORIDADE_MAX || p2 < PRIORIDADE_MIN || p2 > PRIORIDADE_MAX) {
                JOptionPane.showMessageDialog(
                    this,
                    "As prioridades devem estar entre 1 e 10.",
                    "Prioridade Inválida",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            modeloLista1.clear();
            modeloLista2.clear();
            lblFim1.setVisible(false);
            lblFim2.setVisible(false);
            thread1Fim = false;
            thread2Fim = false;
            btnIniciar.setEnabled(false);

            TarefaContagem tarefa1 = new TarefaContagem(1, max, modeloLista1, lista1, lblFim1);
            TarefaContagem tarefa2 = new TarefaContagem(2, max, modeloLista2, lista2, lblFim2);

            Thread t1 = new Thread(tarefa1, "Thread-1");
            Thread t2 = new Thread(tarefa2, "Thread-2");

            t1.setPriority(p1);
            t2.setPriority(p2);

            t1.start();
            t2.start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor, insira apenas números inteiros válidos.",
                "Erro de Formato",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registrarConclusao(int idThread) {
        if (idThread == 1) {
            thread1Fim = true;
        } else {
            thread2Fim = true;
        }

        if (thread1Fim && thread2Fim) {
            btnIniciar.setEnabled(true);
        }
    }

    private void limparTela() {
        modeloLista1.clear();
        modeloLista2.clear();
        lblFim1.setVisible(false);
        lblFim2.setVisible(false);
        btnIniciar.setEnabled(true);
    }

    private class TarefaContagem implements Runnable {
        private final int idThread;
        private final int maximo;
        private final DefaultListModel<String> modeloAlvo;
        private final JList<String> listaAlvo;
        private final JLabel lblFinalAlvo;

        public TarefaContagem(
            int idThread,
            int maximo,
            DefaultListModel<String> modeloAlvo,
            JList<String> listaAlvo,
            JLabel lblFinalAlvo
        ) {
            this.idThread = idThread;
            this.maximo = maximo;
            this.modeloAlvo = modeloAlvo;
            this.listaAlvo = listaAlvo;
            this.lblFinalAlvo = lblFinalAlvo;
        }

        @Override
        public void run() {
            for (int i = 0; i < maximo; i++) {
                iteracoes();

                final String valor = String.valueOf(i);
                SwingUtilities.invokeLater(() -> {
                    modeloAlvo.addElement(valor);
                    int ultimo = modeloAlvo.getSize() - 1;
                    if (ultimo >= 0) {
                        listaAlvo.ensureIndexIsVisible(ultimo);
                    }
                });
            }

            SwingUtilities.invokeLater(() -> {
                lblFinalAlvo.setVisible(true);
                registrarConclusao(idThread);
            });
        }

        private double iteracoes() {
            double acumulador = 0.0;
            for (int k = 1; k <= ITERACOES_CARGA_CPU; k++) {
                acumulador += Math.sqrt(k) * Math.sin(k);
            }
            return acumulador;
        }
    }
}

//Gustavo Cardoso Muniz e Izadora Santos Silva