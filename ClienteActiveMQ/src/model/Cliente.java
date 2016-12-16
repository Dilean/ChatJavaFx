/*
 * Disponível em: http://www.mballem.com/post/chat-jms-com-activemq/
 */
package model;

import java.util.Scanner;
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
import presenter.ViewPedidoPresenter;
import view.ViewPedido;

public class Cliente implements MessageListener {

    private static Cliente instancia = null;
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private ViewPedidoPresenter presenter;

    /* Construtor usado para inicializar o cliente JMS do Cliente */
    private Cliente(String topicFactory, String topicName, ViewPedidoPresenter presenter) throws Exception {
        this.presenter = presenter;
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
        // Inicializa as variaveis do Cliente
        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        // Inicia a conexão JMS, permite que mensagens sejam entregues
        connection.start();
    }

    public static Cliente getInstancia(ViewPedidoPresenter p) throws Exception {
        if (instancia == null) {
            instancia = new Cliente("TopicCF", "topicChat", p);
        }
        return instancia;
    }

    /* Recebe as mensagens do tópico assinado */
    @Override
    public void onMessage(Message message) {
        try {
            // As mensagens criadas como TextMessage devem ser recebidas como TextMessage
            TextMessage textMessage = (TextMessage) message;
            String linha = textMessage.getText();
            String[] texto = linha.split(":");
            if (texto[0].equals("1")) {
                presenter.atualizarPedido(texto[1]);
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    // Cria a mensagem de texto e a publica no tópico. Evento referente ao produtor
    public void writeMessage(String text) throws JMSException {
        // Recebe um objeto da sessao para criar uma mensagem do tipo TextMessage
        TextMessage message = pubSession.createTextMessage();
        // Seta no objeto a mensagem que será enviada
        message.setText("0:" + text);
        // Publica a mensagem no tópico
        publisher.publish(message);
    }

    // Fecha a conexão JMS
    public void close() throws JMSException {
        connection.close();
    }

    // Roda o Cliente
    public void aguardaEscrita() throws Exception {
        try {
            //Habilita o envio de mensagens por linha de comando
            Scanner commandLine = new Scanner(System.in);
            while (true) {
                //captura a mensagem digitada no console
                String s = commandLine.nextLine();
                //para sair digite exit
                if (presenter.pedidoEnviado()) {
                    //fecha a conexão com o provedor
                    this.close();
                    //sai do sistema
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
