/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projecto_multicast;

/**
 *
 * @author Carlos
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Carlos
 */
public class ClienteChatUDP extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
   
    String nombre;
    static JTextField mensaje = new JTextField();
    private JScrollPane scrollpanel;
    static JTextArea textarea1;
    JButton btnEnviar = new JButton("Enviar");
    JButton btnDesconectar = new JButton("Salir");
    
    private static final String GRUPO = "224.0.0.25";
    private static final int PUERTO_SERVIDOR = 60005;
    private static final int PUERTO_GRUPO = 60006;
    private static final String SERVIDOR = "localhost";
    private static boolean repetir= true;
    
    public static InetAddress direccionGrupo= null;
    public static InetAddress direccionServidor= null;
    
    private static MulticastSocket multiSocket= null;
    public static DatagramSocket socket= null;

   
    private static byte[] buffer = new byte[1024];
    
    public ClienteChatUDP(String nombre){
        super("Conexión del cliente chat tcp");
        
        this.nombre= nombre;
        System.out.println("El nombre es: " + this.nombre);
        setLayout(null);
        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        textarea1 = new JTextArea();
        scrollpanel = new JScrollPane(textarea1);
        scrollpanel.setBounds(10, 50, 400, 300);
        add(scrollpanel);
        btnEnviar.setBounds(420, 10, 100, 30);
        add(btnEnviar);
        btnDesconectar.setBounds(420, 50, 100, 30);
        add(btnDesconectar);
        textarea1.setEditable(false);
        btnEnviar.addActionListener(this);
        btnDesconectar.addActionListener(this);
        //Se anula el cierre de la ventana para obligarle a dar al botón de salir
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        System.out.println("fin constructor");
        
        
        
    }
    
    public String getNombre(){
        return this.nombre;
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == btnDesconectar){
           
            
            String texto= ">>>>>>>>>> " + nombre + " ABANDONA EL CHAT!!!";
            DatagramPacket paquete= new DatagramPacket( texto.getBytes(), texto.length(), direccionServidor, PUERTO_SERVIDOR );
            try {
                socket.send(paquete);
                socket.close();
                repetir= false;
            } catch (Exception e) {e.printStackTrace();}
            
            System.exit(0);
        }
        if(ae.getSource() == btnEnviar){
            System.out.println("Enviamos los mensajes al grupo multicast");
            String texto= nombre + "--> " + mensaje.getText().toString();
            
            DatagramPacket paquete= new DatagramPacket(texto.getBytes(), texto.length(), direccionServidor, PUERTO_SERVIDOR );
            try {
                
                socket.send(paquete);
                
            } catch (IOException e) {e.printStackTrace();}
            mensaje.setText("");
        }
    }

    
    public static void main(String[] args) throws Exception{
        
        //pido nombre usuario
        String nickname = JOptionPane.showInputDialog("Introduce tu nick");
        DatagramPacket paquete= null;
      
        
        
        if (!nickname.trim().equals("")) //si ha introducido el nickname 
        {
           
            ClienteChatUDP cliente= new ClienteChatUDP(nickname);
            
            System.out.println("Te llamas: " + cliente.getNombre());
      
            
            cliente.setBounds(0, 0, 540, 400);
            cliente.setVisible(true);
            
            
            //Creamos en grupo y nos unimos
        try {
          
            socket= new DatagramSocket();
            multiSocket= new MulticastSocket(PUERTO_GRUPO);
            direccionGrupo= InetAddress.getByName(GRUPO);
            direccionServidor= InetAddress.getByName(SERVIDOR);
            multiSocket.joinGroup(direccionGrupo);
            
            paquete= new DatagramPacket(nickname.getBytes(), nickname.length(), direccionServidor, PUERTO_SERVIDOR );
            socket.send(paquete);
            System.out.println("primer envio");
            
            while(repetir){
                
                buffer = new byte[1024];
                paquete= new DatagramPacket(buffer, buffer.length);
                multiSocket.receive(paquete);
                
                System.out.println("cliente recibe paquete por multisocket: " + new String( paquete.getData() ));
                
                String texto= new String( paquete.getData() );
                textarea1.append(texto + "\n");
            
            }
            
        } catch (IOException e) {e.printStackTrace();}
            
            
         
            
        } else {
            System.out.println("El nombre está vacio");
        }
    }


        
    
}
