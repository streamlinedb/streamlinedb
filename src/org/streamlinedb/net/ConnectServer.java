package org.streamlinedb.net;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

public class ConnectServer {
	
	
	    private final HashMap<SocketChannel, Queue<ByteBuffer>> incoming = new HashMap<>();
	 
	    private ServerSocketChannel socket;
	    private Selector selector;
        int port=7080;  	 
	    public void start() throws IOException {
	        selector = Selector.open();
	        
	        if(System.getProperty("StreamLineDB.port")!=null)
	        	port = Integer.valueOf(System.getProperty("StreamLineDB.port"));                 
	        socket = ServerSocketChannel.open();
	        socket.bind(new InetSocketAddress(port));
	        socket.configureBlocking(false);
	        socket.register(selector, SelectionKey.OP_ACCEPT);
	 
	        while (true) {
	            selector.select();
	            Set<SelectionKey> keys = selector.selectedKeys();
	 
	            for (Iterator<SelectionKey> i = keys.iterator(); i.hasNext(); ) {
	 
	                SelectionKey key = i.next();
	                i.remove();
	 
	                if (key.isValid() && key.isAcceptable()) {
	                    handleAccept();
	                } else if (key.isValid() && key.isReadable()) {
	                    handleRead(key);
	                } else if (key.isValid() && key.isWritable()) {
	                    handleWrite(key);
	                }
	            }
	        }
	    }
	 
	    private void handleAccept() throws IOException {
	        SocketChannel socketa = socket.accept();
	 
	        if (socketa != null) {
	        	socketa.configureBlocking(false);
	        	socketa.register(selector, SelectionKey.OP_READ);
	 
	            incoming.put(socketa, new LinkedList<ByteBuffer>());
	        }
	    }
	 
	    private void handleRead(SelectionKey key) throws IOException {
	        SocketChannel socket = (SocketChannel) key.channel();
	    	ByteBuffer buffer = ByteBuffer.allocate(1024);
	        
	        int read = socket.read(buffer);
	        if (read == -1) {
	        	incoming.remove(socket);
	        	socket.close();
	        } else if (read > 0) {
	        	buffer.flip();
	            for (int i = 0; i < buffer.limit(); i++) {
	            	buffer.put(i, (byte) Character.toUpperCase((char) buffer.get(i)));
	            }
	            incoming.get(socket).add(buffer);
	            key.interestOps(SelectionKey.OP_WRITE);
	        }
	    }
	 
	    private void handleWrite(SelectionKey key) throws IOException {
	        Queue<ByteBuffer> queue = incoming.get(socket);
	        SocketChannel socketa = (SocketChannel) key.channel();
	 
	        while (!queue.isEmpty()) {
	            ByteBuffer buffer = queue.peek();
	            int write = socketa.write(buffer);
	            if (write == -1) {
	            	incoming.remove(socketa);
	            	socketa.close();
	                return;
	            } else if (buffer.hasRemaining()) {
	            	socketa.close();
	                return;
	            }
	 
	            queue.remove();
	        }
	        key.interestOps(SelectionKey.OP_READ);
	    }
	
}
