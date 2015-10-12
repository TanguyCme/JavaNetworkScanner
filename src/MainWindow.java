import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.awt.color.*;

import javax.imageio.*;


public class MainWindow extends JFrame {

	JList<String> m_list;
	JButton m_scanButton;
	static Vector<String> v = new Vector<String>();
	JLabel m_portLabel = new JLabel("port :");
	JTextField m_portfield = new JTextField("22222");
	JPanel fl = new JPanel(new GridLayout(2,0));
	
	String ipString;
	static int port = 22222;
	
	private static BufferedImage createRGBImage(byte[] bytes, int width, int height) {
	    DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
	    ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    return new BufferedImage(cm, Raster.createInterleavedRaster(buffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null), false, null);
	  }
	
	public static InetAddress getWLANipAddress(String protocolVersion) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            if (netint.isUp() && !netint.isLoopback() && !netint.isVirtual()) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (protocolVersion.equals("IPv4")) {
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress;
                        }
                    } else {
                        if (inetAddress instanceof Inet6Address) {
                            return inetAddress;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void printReachableHosts(InetAddress inetAddress) throws SocketException {
        String ipAddress = inetAddress.toString();
        ipAddress = ipAddress.substring(1, ipAddress.lastIndexOf('.')) + ".";
        for (int i = 2; i < 253; i++) {
            String otherAddress = ipAddress + String.valueOf(i);
               
            try {
                if (InetAddress.getByName(otherAddress.toString()).isReachable(50)) {
                    System.out.println(otherAddress);
                    v.addElement(otherAddress);
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(otherAddress, port), 500);
                    
                    /*DataInputStream dIn = new DataInputStream(socket.getInputStream());
                    byte[] recievedMsg;

                    int length = dIn.readInt();
                    try {
                      if (length >0) {
                        recievedMsg = new byte[length];
                        dIn.readFully(recievedMsg,0,recievedMsg.length);

                        //Part wich write the byte array in a file
                        // FileOutputStream fos = new FileOutputStream(new File("toto.txt"));
                        // fos.write(recievedMsg);
                        // fos.close();

                        //Part wich convert and display the image
                        BufferedImage recievedImg = createRGBImage(recievedMsg,320,240);
                        try {
                          ImageIO.write(recievedImg, "BMP", new File("toto.png"));
                          JFrame frame = new JFrame();
                          frame.getContentPane().setLayout(new FlowLayout());
                          frame.getContentPane().add(new JLabel(new ImageIcon(recievedImg)));
                          frame.pack();
                          frame.setVisible(true);
                        }
                        finally {
                          //close the inputStream
                          dIn.close();
                        }

                      }
                    }catch(IOException e){
                      e.printStackTrace();
                    }*/
                    
                    
                    socket.close();
                }
            } catch (UnknownHostException e) {
               //e.printStackTrace();
            } catch (IOException e) {
               // e.printStackTrace();
            }
            
        }
    }
	
	public MainWindow() throws HeadlessException {
		super();
		
		
		m_scanButton = new JButton("Lancer le scan");
		m_scanButton.addActionListener(new ActionListener() {
		
			
			@Override
			public void actionPerformed(ActionEvent e) {
				InetAddress address;
				/*String text = new String(m_portfield.toString());
				port = Integer.parseInt(text);*/
				try {
					address = getWLANipAddress("IPv4");
					if (address != null) {
		                printReachableHosts(address);
		                m_list.setListData(v);
		                fl.revalidate();
		                fl.repaint();
		                
		            }
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
				
			}
		});
		
		m_list = new JList<String>(v);
		fl.add("List", m_list);
		fl.add("button",m_scanButton);
		getContentPane().add(fl);
		
		pack();
	}
	
	
}
