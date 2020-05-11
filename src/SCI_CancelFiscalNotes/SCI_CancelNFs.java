package SCI_CancelFiscalNotes;

import Entity.Executavel;
import Executor.Execution;
import SCI_CancelFiscalNotes.Model.Model;
import SCI_CancelFiscalNotes.View.View;
import java.util.ArrayList;
import java.util.List;

public class SCI_CancelNFs {


    /*FAVOR NÃO LIGAR PARA A ORGANIZAÇÃO E PADRÕES DE CLASSE POIS É UM PROGRAMA SIMPLES*/
    public static void main(String[] args) {
        View view = new View();
        
        List<Executavel> getInputsExecutables =  new ArrayList<>();
        getInputsExecutables.add(view.new getInputEnterprise());
        getInputsExecutables.add(view.new getInputNotesFile());
        getInputsExecutables.add(view.new getInputMovimentType());
        
        Execution getInputsExecution = new Execution("Pegando inputs do usuário");
        getInputsExecution.setMostrarMensagens(false);
        
        getInputsExecution.setExecutaveis(getInputsExecutables);
        getInputsExecution.rodarExecutaveis();
        getInputsExecution.finalizar();
        
        if(!getInputsExecution.hasErrorBreak()){
            Model model = new Model(view.getCodeEnterprise(), view.getNotesFile(), view.getMovimentType());
            
            List<Executavel> modelList  = new ArrayList<>();
            modelList.add(model.new setListaDeNfs());
            modelList.add(model.new separarNfs());
            modelList.add(model.new fazerUpdateNoBanco());
            modelList.add(model.new criarListaCanceladasENaoExistentes());
            
            Execution modelExecution = new Execution("Cancelando NFs");
            modelExecution.rodarExecutaveis();
            modelExecution.finalizar();
        }
        
        System.exit(0);
    }
}
