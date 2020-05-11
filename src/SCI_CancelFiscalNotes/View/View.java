package SCI_CancelFiscalNotes.View;

import Entity.Executavel;
import java.io.File;
import javax.swing.JOptionPane;

public class View {

    private int codeEnterprise;
    private File notesFile;
    private String movimentType;
    private static final String[] movimentTypes = new String[]{"Entrada", "Saida", "Serviço"};

    public class getInputEnterprise extends Executavel {

        public getInputEnterprise() {
            nome = "Pegando código da empresa";
        }

        @Override
        public void run() {
            String message = "Insira o código da empresa no Unico:";

            String enterpriseString = JOptionPane.showInputDialog(message);
            try {
                codeEnterprise = Integer.valueOf(enterpriseString);
                if (codeEnterprise < 1) {
                    throw new Error("O código da empresa não pode ser menor do que 1;");
                }
            } catch (Exception e) {
                throw new Error("Código de empresa inválido! Deve ser um número!");
            }
        }
    }

    public class getInputNotesFile extends Executavel {

        public getInputNotesFile() {
            nome = "Pegando arquivo de NFs";
        }

        @Override
        public void run() {
            Executor.View.View.render("Escolha a seguir o arquivo CSV com apenas o número das NFs. Deve haver apenas uma coluna:", "question");
            notesFile = Selector.Arquivo.selecionar("C:/", "CSV com NFs", "csv");
            if (!Selector.Arquivo.verifica(notesFile.getAbsolutePath(), "csv")) {
                throw new Error("Arquivo de NFs inválido!");
            }
        }

    }

    public class getInputMovimentType extends Executavel {

        public getInputMovimentType() {
            nome = "Pegando tipo de movimento";
        }

        @Override
        public void run() {
            int option = Executor.View.View.chooseOption("Tipo de Movimento", "Escolha o tipo de movimento:", movimentTypes);
            try {
                movimentType = movimentTypes[option].toUpperCase().substring(0, 3);
            } catch (Exception e) {
                movimentType = "ENT";
            }
        }

    }

    public int getCodeEnterprise() {
        return codeEnterprise;
    }

    public File getNotesFile() {
        return notesFile;
    }

    public String getMovimentType() {
        return movimentType;
    }
    
    
}
