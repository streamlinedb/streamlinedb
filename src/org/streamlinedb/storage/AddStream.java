package org.streamlinedb.storage;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
 
public class AddStream
{
    public static int add(String filename,HashMap hmap1) {
    	int total;
   		try (Writer writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(filename, "utf-8"))) {
			    for(String rstr:hmap1.keySet())
					writer.write(String.format("%s;%s\n",rstr,hmap1.get(rstr)));
   		}catch(Exception e) {
    		e.printStackTrace();
    		return -1;
    	}
    	return total;
    }
}
