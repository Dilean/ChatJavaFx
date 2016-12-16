/*
 * Disponível em: http://www.mballem.com/post/chat-jms-com-activemq/
 */
package main;

import chain.Processador;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import model.Pedido;

public class Servidor implements MessageListener {

    private String estadoAtual;
    private Pedido pedido;
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;

    /* Construtor usado para inicializar o cliente JMS do Servidor */
    public Servidor(String topicFactory, String topicName) throws Exception {
        // Obtém os dados da conexão JNDI através do arquivo jndi.properties
        InitialContext ctx = new InitialContext();
        // O cliente utiliza o TopicConnectionFactory para criar um objeto do tipo TopicConnection com o provedor JMS
        TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup(topicFactory);
        // Utiliza o TopicConnectionFactory para criar a conexão com o provedor JMS
        connection = conFactory.createTopicConnection();
        // Utiliza o TopicConnection para criar a sessão para o produtor
        // Atributo false -> uso ou não de transações(tratar uma série de envios/recebimentos como unidade atômica e
        // controlá-la via commit e rollback)
        // Atributo AUTO_ACKNOWLEDGE -> Exige confirmação automática após recebimento correto
        pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        // Utiliza o TopicConnection para criar a sessão para o consumidor        
        TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        // Pesquisa o destino do tópico via JNDI
        Topic chatTopic = (Topic) ctx.lookup(topicName);
        // Cria o tópico JMS do produtor das mensagens através da sessão e o nome do tópico
        publisher = pubSession.createPublisher(chatTopic);
        // Cria(Assina) o tópico JMS do consumidor das mensagens através da sessão e o nome do tópico
        TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);
        // Escuta o tópico para receber as mensagens através do método onMessage()
        subscriber.setMessageListener(this);
        // Inicializa as variaveis do Servidor
        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        // Inicia a conexão JMS, permite que mensagens sejam entregues
        connection.start();
    }

    /* Recebe as mensagens do tópico assinado */
    @Override
    public void onMessage(Message message) {
        try {
            // As mensagens criadas como TextMessage devem ser recebidas como TextMessage
            TextMessage textMessage = (TextMessage) message;
            String linha = textMessage.getText();
            String[] texto = linha.split(":");
            if (texto[0].equals("0")) {
                String linhaAux = texto[1];
                String[] textoAux = linhaAux.split(";");
                switch (textoAux[0]) {
                    case "0":
                        writeMessage(estadoAtual);
                        break;
                    case "1":
                        pedido = new Pedido();
                        estadoAtual = pedido.getEstado();
                        atualizarPedido();
                        break;
                }
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Cria a mensagem de texto e a publica no tópico. Evento referente ao produtor
    public void writeMessage(String text) throws JMSException {
        // Recebe um objeto da sessao para criar uma mensagem do tipo TextMessage
        TextMessage message = pubSession.createTextMessage();
        // Seta no objeto a mensagem que será enviada
        message.setText(1 + ":" + text);
        // Publica a mensagem no tópico
        publisher.publish(message);
    }

    // Fecha a conexão JMS
    public void close() throws JMSException {
        connection.close();
    }

    public void atualizarPedido() throws InterruptedException {
        new Thread() {
            @Override
            public void run() {
                while ((pedido.verificarEnviado() != true) && (pedido.verificarPagamentoPedidoReprovado() != true)) {
                    try {
                        Thread.sleep(pedido.gerarTempoAleatorio());
                        Processador.getInstancia().processarPedido(pedido);
                        estadoAtual = pedido.getEstado();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    // Roda o Servidor
    public static void main(String[] args) {
        try {
            // Faz uma chamada ao construtor da classe para iniciar o chat
            Servidor cliente = new Servidor("TopicCF", "topicChat");
            // Depois da conexão criada, faz um loop para enviar mensagens
            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
