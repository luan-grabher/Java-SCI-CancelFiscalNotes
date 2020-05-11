package SCI_CancelFiscalNotes.Model;

import Entity.Executavel;
import Executor.Execution;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import main.Arquivo;
import sql.Banco;
import sql.SQL;

public class Model {

    private final String localBancoCfg = "sci.cfg";
    private final int codCancelamento = 2;

    private final int codigoEmpresa;
    private final File arquivoNotas;
    private final String tipoMovimento;
    private String listaDeNfs;
    private String listaDeNfsDivididasIn;

    public Model(int codigoEmpresa, File arquivoNotas, String tipoMovimento) {
        this.codigoEmpresa = codigoEmpresa;
        this.arquivoNotas = arquivoNotas;
        this.tipoMovimento = tipoMovimento;
    }

    public class criarListaCanceladasENaoExistentes extends Executavel {

        public criarListaCanceladasENaoExistentes() {
            nome = "Criando lista de cancelas com sucesso e não existentes no sistema";
        }

        @Override
        public void run() {
            List<String[]> canceladasBanco = getListaNFsCancelasBanco();
            List<String[]> nfsOriginais = getListaJavaDasNfs();

            //Produra nfs nao existentes
            List<String[]> nfsNaoExistentesSistema;
            nfsNaoExistentesSistema = nfsOriginais.stream().filter(
                    nf -> canceladasBanco.stream().filter(c -> c[0].equals(nf[0])).count() == 0
            ).collect(Collectors.toList());

            //Cria texto arquivo
            StringBuilder textoArquivo = new StringBuilder();

            textoArquivo.append("NFS CANCELADAS COM SUCESSO:\n");
            for (String[] cancelada : canceladasBanco) {
                textoArquivo.append(cancelada[0]);
                textoArquivo.append("\r\n");
            }

            textoArquivo.append("\n");
            textoArquivo.append("NFS QUE NÃO EXISTEM NO SISTEMA:\n");
            for (String[] nf : nfsNaoExistentesSistema) {
                textoArquivo.append(nf[0]);
                textoArquivo.append("\r\n");
            }

            String localSalvar = arquivoNotas.getParentFile().getAbsolutePath() + "/Nfs cancelas e não existentes no Único.txt";
            if (!Arquivo.salvar(localSalvar, textoArquivo.toString())) {
                throw new Error("Não foi possivel salvar o arquivo: " + localSalvar);
            }
        }
    }

    private List<String[]> getListaNFsCancelasBanco() {
        List<String[]> lista = new ArrayList<>();
        try {
            //Procura quais estão cancelados no sistema
            String sqlProcura = "select BDNUMDOCINI from VEF_EMP_TMOV" + tipoMovimento + " m where m.BDCODEMP = " + codigoEmpresa + " AND BDCODSITNF = " + codCancelamento + " AND   " + listaDeNfsDivididasIn;

            Banco banco = new Banco(localBancoCfg);
            lista = banco.select(sqlProcura);
        } catch (Exception e) {
        }
        return lista;
    }

    private List<String[]> getListaJavaDasNfs() {
        List<String[]> lista = new ArrayList<>();
        try {
            String[] listaNfsSplit = listaDeNfs.split(",");
            for (String string : listaNfsSplit) {
                lista.add(new String[]{string});
            }
        } catch (Exception e) {
        }
        return lista;
    }

    public class fazerUpdateNoBanco extends Executavel {

        public fazerUpdateNoBanco() {
            nome = "Fazendo alteração das NFs no banco";
        }

        @Override
        public void run() {
            //Montar sql
            String sql = "UPDATE VEF_EMP_TMOV" + tipoMovimento + " m "
                    + " SET m.BDCODSITNF = " + codCancelamento + ", m.BDDATACANC = m.BDDATAEMISSAO "
                    + " where m.BDCODEMP = " + codigoEmpresa + " AND "
                    + listaDeNfsDivididasIn;

            Banco banco = new Banco(localBancoCfg);
            if (banco.testConnection()) {
                if (!banco.query(sql)) {
                    throw new Error("Ocorreu um erro ao executar comando SQL no banco.");
                }
            } else {
                throw new Error("Não foi possivel conectar ao banco de dados!");
            }
        }
    }

    public class separarNfs extends Executavel {

        public separarNfs() {
            nome = "Separando a lista de NFs";
        }

        @Override
        public void run() {
            listaDeNfsDivididasIn = SQL.divideIn(listaDeNfs, "BDNUMDOCINI");
        }
    }

    public class setListaDeNfs extends Executavel {

        public setListaDeNfs() {
            nome = "Definindo lista de Nfs";
        }

        @Override
        public void run() {
            try {
                //Pega texto arquivo
                String textoArquivo = Arquivo.ler(arquivoNotas.getAbsolutePath());

                //Substitui quebra de linha para poder filtrar a string depois
                listaDeNfs = textoArquivo.replaceAll("\r\n", ",");

                //Filtra apenas números e virgulas
                listaDeNfs = listaDeNfs.replaceAll("[^0-9,]", "");
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Ocorreu um erro ao criar lista de NFs usando o arquivo fornecido: " + e);
            }
        }
    }

}
