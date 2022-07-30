package org.streamlinedb.storage;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
 
public class WriteTable
{
    public static int write(String filename,String[] table) {
    	int total;
    	try {
    		RandomAccessFile raf=new RandomAccessFile(filename, "rw");
    		FileChannel rchan = raf.getChannel();
    		total = Arrays.stream(table).mapToInt(String::length).sum();		
    		total+=table.length*4; 
    		MappedByteBuffer out = rchan.map(FileChannel.MapMode.READ_WRITE, 0, total);  
    		for (int i = 0; i < table.length; i++) {
    			out.putInt(table[i].length());
    			out.put(table[i].getBytes());
    		}  
    		raf.close();
   		}catch(Exception e) {
    		e.printStackTrace();
    		return -1;
    	}
    	return total;
    }
}
