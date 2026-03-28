package com.restaurante;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Usuario USUARIO_PADRAO = new Usuario("admin123", "admin123", "Atendente");
    private static int proximoPedidoId = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean controle = true;

        exibirCabecalho();

        while (controle) {
            int escolha = getEscolha(sc);

            switch (escolha) {
                case 1:
                    loginUsuario(sc);
                    break;
                case 2:
                    controle = false;
                    System.out.println("Programa encerrado. Volte sempre ao Sabor do Cerrado Cuiabano!");
                    break;
                default:
                    System.out.println("Opcao invalida. Escolha uma alternativa do menu.");
                    break;
            }
        }

        sc.close();
    }

    private static void exibirCabecalho() {
        System.out.println("==============================================");
        System.out.println("      SABOR DO CERRADO CUIABANO");
        System.out.println("      Restaurante regional de Cuiaba");
        System.out.println("==============================================");
    }

    private static int getEscolha(Scanner sc) {
        System.out.println();
        System.out.println("1 - Login");
        System.out.println("2 - Encerrar programa");
        return lerInteiro(sc, "Escolha uma opcao: ");
    }

    private static void loginUsuario(Scanner sc) {
        System.out.println();
        System.out.print("Digite o login do usuario: ");
        String usuario = sc.nextLine().trim();
        System.out.print("Digite a senha do usuario: ");
        String senha = sc.nextLine().trim();

        if (!USUARIO_PADRAO.login.equals(usuario) || !USUARIO_PADRAO.senha.equals(senha)) {
            System.out.println("Login ou senha incorretos.");
            return;
        }

        System.out.println();
        System.out.println("Login efetuado com sucesso. Bem-vindo, " + USUARIO_PADRAO.nomeExibicao + "!");

        boolean controleSubMenu = true;
        List<Pedido> listaPedidos = new ArrayList<>();

        while (controleSubMenu) {
            int opcaoSubMenu = getOpcaoSubMenu(sc);

            switch (opcaoSubMenu) {
                case 1:
                    realizarPedido(sc, listaPedidos);
                    break;
                case 2:
                    verStatusPedido(listaPedidos);
                    break;
                case 3:
                    editarPedido(sc, listaPedidos);
                    break;
                case 4:
                    excluirPedidos(sc, listaPedidos);
                    break;
                case 5:
                    verPedidos(listaPedidos);
                    break;
                case 6:
                    fecharConta(listaPedidos);
                    break;
                case 7:
                    controleSubMenu = false;
                    System.out.println("Logout realizado com sucesso.");
                    break;
                default:
                    System.out.println("Opcao invalida. Escolha uma alternativa do menu.");
                    break;
            }
        }
    }

    private static void realizarPedido(Scanner sc, List<Pedido> listaPedidos) {
        System.out.println();
        System.out.println("Cardapio cuiabano da casa:");
        ItemCardapio[] cardapio = ItemCardapio.values();
        for (ItemCardapio item : cardapio) {
            System.out.println(item.codigo + " - " + item.nome + " - R$ " + formatarValor(item.preco));
        }

        int codigoPrato = lerInteiro(sc, "Escolha um prato pelo codigo: ");
        ItemCardapio itemSelecionado = ItemCardapio.porCodigo(codigoPrato);
        if (itemSelecionado == null) {
            System.out.println("Prato nao encontrado no cardapio.");
            return;
        }

        System.out.print("Deseja adicionar observacao ao pedido? ");
        String observacao = sc.nextLine().trim();
        if (observacao.isBlank()) {
            observacao = "Sem observacoes";
        }

        Pedido pedido = new Pedido(proximoPedidoId++, itemSelecionado, observacao);
        listaPedidos.add(pedido);

        System.out.println("Pedido #" + pedido.id + " cadastrado com sucesso: "
                + pedido.item.nome + " - R$ " + formatarValor(pedido.getValor()));
    }

    private static void verStatusPedido(List<Pedido> listaPedidos) {
        System.out.println();
        if (listaPedidos.isEmpty()) {
            System.out.println("Nenhum pedido foi realizado ate o momento.");
            return;
        }

        Pedido pedidoAtualizado = atualizarFilaCozinha(listaPedidos);
        if (pedidoAtualizado != null) {
            System.out.println("Atualizacao da cozinha: pedido #" + pedidoAtualizado.id
                    + " agora esta em " + pedidoAtualizado.status.descricao + ".");
        } else {
            System.out.println("Todos os pedidos ja foram entregues.");
        }

        System.out.println("Status atual dos pedidos:");
        for (Pedido pedido : listaPedidos) {
            System.out.println(descreverPedido(pedido));
        }
    }

    private static Pedido atualizarFilaCozinha(List<Pedido> listaPedidos) {
        for (Pedido pedido : listaPedidos) {
            if (pedido.status != StatusPedido.ENTREGUE) {
                pedido.avancarStatus();
                return pedido;
            }
        }
        return null;
    }

    private static void editarPedido(Scanner sc, List<Pedido> listaPedidos) {
        System.out.println();
        if (listaPedidos.isEmpty()) {
            System.out.println("Nao ha pedidos para editar.");
            return;
        }

        verPedidos(listaPedidos);
        int idPedido = lerInteiro(sc, "Informe o numero do pedido que deseja editar: ");
        Pedido pedido = buscarPedidoPorId(listaPedidos, idPedido);
        if (pedido == null) {
            System.out.println("Pedido nao encontrado.");
            return;
        }

        if (pedido.status == StatusPedido.ENTREGUE) {
            System.out.println("O pedido ja foi entregue e nao pode mais ser alterado.");
            return;
        }

        System.out.println("Escolha o novo prato para o pedido #" + pedido.id + ":");
        for (ItemCardapio item : ItemCardapio.values()) {
            System.out.println(item.codigo + " - " + item.nome + " - R$ " + formatarValor(item.preco));
        }

        int codigoPrato = lerInteiro(sc, "Novo codigo do prato: ");
        ItemCardapio novoItem = ItemCardapio.porCodigo(codigoPrato);
        if (novoItem == null) {
            System.out.println("Prato nao encontrado no cardapio.");
            return;
        }

        System.out.print("Nova observacao do pedido: ");
        String observacao = sc.nextLine().trim();
        if (observacao.isBlank()) {
            observacao = "Sem observacoes";
        }

        pedido.item = novoItem;
        pedido.observacao = observacao;
        pedido.status = StatusPedido.RECEBIDO;

        System.out.println("Pedido atualizado com sucesso. A cozinha recebeu a nova versao do pedido.");
    }

    private static void excluirPedidos(Scanner sc, List<Pedido> listaPedidos) {
        System.out.println();
        if (listaPedidos.isEmpty()) {
            System.out.println("Nao ha pedidos para excluir.");
            return;
        }

        verPedidos(listaPedidos);
        int idPedido = lerInteiro(sc, "Informe o numero do pedido que deseja excluir: ");
        Pedido pedido = buscarPedidoPorId(listaPedidos, idPedido);
        if (pedido == null) {
            System.out.println("Pedido nao encontrado.");
            return;
        }

        if (pedido.status == StatusPedido.ENTREGUE) {
            System.out.println("O pedido ja foi entregue e nao pode ser excluido.");
            return;
        }

        listaPedidos.remove(pedido);
        System.out.println("Pedido #" + idPedido + " excluido com sucesso.");
    }

    private static void verPedidos(List<Pedido> listaPedidos) {
        System.out.println();
        if (listaPedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }

        System.out.println("Pedidos registrados:");
        for (Pedido pedido : listaPedidos) {
            System.out.println(descreverPedido(pedido));
        }
        System.out.println("Total parcial da mesa: R$ " + formatarValor(calcularTotal(listaPedidos)));
    }

    private static void fecharConta(List<Pedido> listaPedidos) {
        System.out.println();
        if (listaPedidos.isEmpty()) {
            System.out.println("Nao ha pedidos para fechar a conta.");
            return;
        }

        BigDecimal total = calcularTotal(listaPedidos);
        System.out.println("Resumo da mesa:");
        for (Pedido pedido : listaPedidos) {
            System.out.println(descreverPedido(pedido));
        }
        System.out.println("Valor total da conta: R$ " + formatarValor(total));
    }

    private static BigDecimal calcularTotal(List<Pedido> listaPedidos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Pedido pedido : listaPedidos) {
            total = total.add(pedido.getValor());
        }
        return total;
    }

    private static String descreverPedido(Pedido pedido) {
        return "#" + pedido.id
                + " | " + pedido.item.nome
                + " | " + pedido.status.descricao
                + " | Obs.: " + pedido.observacao
                + " | R$ " + formatarValor(pedido.getValor());
    }

    private static Pedido buscarPedidoPorId(List<Pedido> listaPedidos, int idPedido) {
        for (Pedido pedido : listaPedidos) {
            if (pedido.id == idPedido) {
                return pedido;
            }
        }
        return null;
    }

    private static int getOpcaoSubMenu(Scanner sc) {
        System.out.println();
        System.out.println("1 - Fazer pedido");
        System.out.println("2 - Ver status do pedido");
        System.out.println("3 - Editar pedido");
        System.out.println("4 - Excluir pedido");
        System.out.println("5 - Ver pedidos");
        System.out.println("6 - Fechar conta");
        System.out.println("7 - Logout");
        return lerInteiro(sc, "Escolha uma opcao: ");
    }

    private static int lerInteiro(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = sc.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas numeros inteiros.");
            }
        }
    }

    private static String formatarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }
}
