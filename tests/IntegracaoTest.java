public final class IntegracaoTest {
    private static int n;
    private static void check(boolean ok,String msg){if(!ok)throw new AssertionError(msg);n++;}
    public static void main(String[] args){
        Main.ProcessadorPedido fluxo=Main.criarFluxo();
        Main.Pedido eletronico=new Main.Pedido(1,Main.TipoPedido.ELETRONICO,2);
        check(fluxo.processar(eletronico,new Main.FreteEconomico()),"eletrônico encaminhado");
        check(eletronico.getResultado().contains("Eletrônico embalado"),"handler concreto correto");
        check(eletronico.getResultado().contains("R$ 12.00"),"estratégia econômica aplicada");
        Main.Pedido roupa=new Main.Pedido(2,Main.TipoPedido.VESTUARIO,1);
        check(fluxo.processar(roupa,new Main.FreteExpresso()),"vestuário encaminhado");
        check(roupa.getResultado().contains("Peça de vestuário"),"handler de vestuário selecionado");
        check(roupa.getResultado().contains("R$ 22.00"),"estratégia expressa usada");
        Main.Pedido outro=new Main.Pedido(3,Main.TipoPedido.OUTRO,1);
        check(fluxo.processar(outro,new Main.FreteRetirada()),"fallback atende outro pedido");
        check(outro.getResultado().contains("R$ 0.00"),"retirada sem custo");
        check(eletronico.getResultado().contains("validado") && eletronico.getResultado().contains("|"),"template mantém etapas em sequência");
        Main.ProcessadorPedido parcial=new Main.ProcessadorEletronicos();
        Main.Pedido semHandler=new Main.Pedido(4,Main.TipoPedido.OUTRO,1);
        check(!parcial.processar(semHandler,new Main.FreteEconomico()),"cadeia sem correspondência informa false");
        check(semHandler.getResultado()==null,"pedido não processado não é alterado");
        try { new Main.Pedido(5,Main.TipoPedido.OUTRO,0); throw new AssertionError("peso inválido aceito"); }
        catch(IllegalArgumentException esperado){n++;}
        System.out.println(n+" verificações aprovadas.");
    }
}
