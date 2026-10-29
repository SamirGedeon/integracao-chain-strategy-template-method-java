import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Integra Strategy, Template Method e Chain of Responsibility em uma central de pedidos. */
public class Main {
    enum TipoPedido { ELETRONICO, VESTUARIO, OUTRO }

    static final class Pedido {
        final int protocolo; final TipoPedido tipo; final double pesoKg;
        private String resultado;
        Pedido(int protocolo, TipoPedido tipo, double pesoKg) {
            if (pesoKg <= 0) throw new IllegalArgumentException("Peso deve ser positivo.");
            this.protocolo=protocolo; this.tipo=Objects.requireNonNull(tipo); this.pesoKg=pesoKg;
        }
        void concluir(String r) { resultado=r; }
        public String getResultado() { return resultado; }
    }

    interface EstrategiaFrete { double calcular(Pedido pedido); String nome(); }
    static final class FreteEconomico implements EstrategiaFrete {
        public double calcular(Pedido p) { return 8 + p.pesoKg * 2; }
        public String nome() { return "Econômico"; }
    }
    static final class FreteExpresso implements EstrategiaFrete {
        public double calcular(Pedido p) { return 18 + p.pesoKg * 4; }
        public String nome() { return "Expresso"; }
    }
    static final class FreteRetirada implements EstrategiaFrete {
        public double calcular(Pedido p) { return 0; }
        public String nome() { return "Retirada"; }
    }

    abstract static class ProcessadorPedido {
        private ProcessadorPedido proximo;
        public ProcessadorPedido definirProximo(ProcessadorPedido p) { proximo=p; return p; }
        public final boolean processar(Pedido p, EstrategiaFrete frete) {
            if (aceita(p)) { p.concluir(executarFluxo(p,frete)); return true; }
            return proximo != null && proximo.processar(p,frete);
        }
        protected abstract boolean aceita(Pedido p);
        protected final String executarFluxo(Pedido p, EstrategiaFrete frete) {
            List<String> etapas=new ArrayList<>();
            etapas.add(validar(p)); etapas.add(separar(p)); etapas.add(enviar(p,frete));
            return String.join(" | ",etapas);
        }
        protected String validar(Pedido p) { return "Protocolo " + p.protocolo + " validado"; }
        protected abstract String separar(Pedido p);
        protected String enviar(Pedido p, EstrategiaFrete frete) {
            return "Frete " + frete.nome() + " R$ " + String.format(java.util.Locale.US,"%.2f",frete.calcular(p));
        }
    }
    static final class ProcessadorEletronicos extends ProcessadorPedido {
        protected boolean aceita(Pedido p) { return p.tipo==TipoPedido.ELETRONICO; }
        protected String separar(Pedido p) { return "Eletrônico embalado com proteção"; }
    }
    static final class ProcessadorVestuario extends ProcessadorPedido {
        protected boolean aceita(Pedido p) { return p.tipo==TipoPedido.VESTUARIO; }
        protected String separar(Pedido p) { return "Peça de vestuário embalada"; }
    }
    static final class ProcessadorGeral extends ProcessadorPedido {
        protected boolean aceita(Pedido p) { return p.tipo==TipoPedido.OUTRO; }
        protected String separar(Pedido p) { return "Item geral separado"; }
    }
    static ProcessadorPedido criarFluxo() {
        ProcessadorPedido c=new ProcessadorEletronicos();
        c.definirProximo(new ProcessadorVestuario()).definirProximo(new ProcessadorGeral());
        return c;
    }
    public static void main(String[] args) {
        EstrategiaFrete frete=new FreteEconomico();
        Pedido pedido=new Pedido(1001,TipoPedido.ELETRONICO,2.5);
        criarFluxo().processar(pedido,frete);
        System.out.println(pedido.getResultado());
    }
}
