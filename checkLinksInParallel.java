import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class CheckAppLinks {
	
    static ArrayList<String> appLinksGood = new ArrayList<String>();
    static ArrayList<String> appLinksBad = new ArrayList<String>();
    
	
	 // считываем содержимое файла в String с помощью BufferedReader
    private static String readUsingBufferedReader(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }
        
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }
    
    private static void checkUrl(String url1, String threadName) {
    	System.out.println(url1 + " " + threadName);
    	
    	URL url;
		try {
			url = new URL(url1);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    	con.setRequestMethod("GET");
	    	con.setConnectTimeout(5000);
	    	con.setReadTimeout(5000);
	    	int status = con.getResponseCode();
	    	System.out.println(status);
	    	if (status == 404) {
	    		//System.out.println(url1 + " is bad");
	    		appLinksBad.add(url1);
	    		
	    	} else if (status == 200) {
	    		//System.out.println(url1 + " is good");
	    		appLinksGood.add(url1);
	    	}
	    	
	    	
//	    	BufferedReader in = new BufferedReader(
//	    			  new InputStreamReader(con.getInputStream()));
//	    			String inputLine;
//	    			StringBuffer content = new StringBuffer();
//	    			while ((inputLine = in.readLine()) != null) {
//	    			    content.append(inputLine);
//	    		}
//	    	System.out.println(content);
//	    	in.close();
	    	con.disconnect();
	    			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    } 
    
    private static void writeFile(ArrayList<String> arrayToOutput, String toFileLink) throws IOException {
    	File fout = new File(toFileLink);
    	java.io.FileOutputStream fos = new java.io.FileOutputStream(fout);
     
    	java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(fos));
   

    		
    	arrayToOutput.forEach((s) -> {
    			try {
    				bw.write(s);
            		bw.newLine();
    			}catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}			
        	});
  
     
    	bw.close();
    }
    
    
    public static void main(String[] args) {
    	String appLinksFileLink = "D:/appLinkList.txt";
 		
        File file = new File(appLinksFileLink);
        
       // String[] appLinksList;
        ArrayList<String> appLinksListArray = new ArrayList<String>();
   
        
        //создаем объект FileReader для объекта File
        FileReader fr;
        
		try {
			fr = new FileReader(file);
			 //создаем BufferedReader с существующего FileReader для построчного считывания
	        BufferedReader reader = new BufferedReader(fr);
	        String line = reader.readLine();
	        // считываем список линков из файла в массив для последующей обработки
            while (line != null) {
            	//System.out.println(line);          
            	appLinksListArray.add(line);
        
            	 line = reader.readLine();
            }
            
         //   System.out.println(appLinksListArray.get(0));     
	        
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// параллельная обработка 
		appLinksListArray.parallelStream().forEach(String ->
			checkUrl(String,Thread.currentThread().getName())
	    );
		 
		 System.out.println("end of parallel tasks"); 
		 // запись array в файлы
		 try {
			writeFile(appLinksGood,"D:/appLinkListGood.txt");
			writeFile(appLinksBad,"D:/appLinkListBad.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
    }

}
