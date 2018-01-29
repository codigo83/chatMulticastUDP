package projecto_multicast;


import chat_udp_2.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;

import java.net.SocketAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ServidorChat extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    
    
    static int conexiones = 0;
 
    static int maximo_conexiones = 10; 

    static JTextField mensaje = new JTextField("");
    static JTextField mensaje2 = new JTextField("");
    private JScrollPane scrollpanel;
    static JTextArea textarea;
    JButton salir = new JButton("Salir");
    private static byte[] buffer= null;
    
    
    private static DatagramSocket socketServidor= null;
    private static MulticastSocket socketCliente= null;
    private static DatagramPacket paquete= null;
    
    
    private static final String GRUPO = "224.0.0.25";
    private static final int PUERTO_SERVIDOR = 60005;
    public static final int PUERTO_GRUPO = 60006;
    
    public static InetAddress direccionGrupo;
    private static ArrayList<ClienteMultiUDP> clientes= new ArrayList<>();

    //constructor  -----------------------------------------------------------------------
    public ServidorChat() {
        
        
        //construyo ventana servidor
        super("Servidor del chat");
        
        
        
        setLayout(null);

        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        mensaje.setEditable(false);

        mensaje2.setBounds(10, 348, 400, 30);
        add(mensaje2);
        mensaje.setEditable(false);

        textarea = new JTextArea();
        scrollpanel = new JScrollPane(textarea);
        scrollpanel.setBounds(10, 50, 400, 300);
        add(scrollpanel);

        salir.setBounds(420, 10, 100, 30);
        add(salir);

        textarea.setEditable(false);
        salir.addActionListener(this);

        //Se anula el cierre de la ventana para que la finalizaci�n del servidor se haga desde el bot�n Salir
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }//fin constructor ------------------------------------------------------------------


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == salir) 
        {   
            if(socketServidor != null){
                socketServidor.close();  
            }
            if(socketCliente != null){
                socketCliente.close();  
            }
                
           
            System.exit(0);
        }
    }


    public static void main(String args[]) throws IOException {
        
        System.out.println("Servidor iniciado.");

        ServidorChat pantalla = new ServidorChat();
        pantalla.setBounds(0, 0, 540, 400);
        pantalla.setVisible(true);
        mensaje.setText("Numero de conexiones actuales: " + conexiones);
        
        socketServidor= new DatagramSocket(PUERTO_SERVIDOR);
        direccionGrupo= InetAddress.getByName(GRUPO);
        
        
        String mensaje;
       
        
        while (true) {
           
            buffer = new byte[1024];
            paquete = new DatagramPacket(buffer, buffer.length);
            System.out.println("kk");
            socketServidor.receive(paquete);
            
            System.out.println("servidor recibe: " + new String( paquete.getData() ));
            mensaje= new String(paquete.getData() );
            textarea.append( mensaje + "\n" );
            
           
            
            System.out.println(mensaje);
            paquete= new DatagramPacket(mensaje.getBytes(), mensaje.length(), direccionGrupo, PUERTO_GRUPO);
            socketServidor.send(paquete);
            System.out.println("enviado a clientes: " + new String( paquete.getData() ));
            
               
           }
        


    }//fin main

}
