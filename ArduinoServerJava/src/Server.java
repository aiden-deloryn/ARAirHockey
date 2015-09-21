import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/", new HomePage());
    server.createContext("/sendData", new SendHandler());
    server.setExecutor(null);
    server.start();
    System.out.println("Server Running On localhost");
  }

  static class HomePage implements HttpHandler {
	  public void handle(HttpExchange t) throws IOException{
		    File file = new File("./public/html/index.html").getCanonicalFile();
		    if (!file.isFile()) {
		      String response = "404 (Not Found)\n";
		      t.sendResponseHeaders(404, response.length());
		      OutputStream os = t.getResponseBody();
		      os.write(response.getBytes());
		      os.close();
		    } else {
		      t.sendResponseHeaders(200, 0);
		      OutputStream os = t.getResponseBody();
		      FileInputStream fs = new FileInputStream(file);
		      final byte[] buffer = new byte[0x10000];
		      int count = 0;
		      while ((count = fs.read(buffer)) >= 0) {
		    	  os.write(buffer,0,count);
		      }
		      fs.close();
		      os.close();
		    }
	  }
  }

  static class SendHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        StringBuilder response = new StringBuilder();
        Map <String,String>parms = Server.queryToMap(t.getRequestURI().getQuery());
        response.append("<html><body>");
        response.append("ID: " + parms.get("id"));
        response.append(" X: " + parms.get("x"));
        response.append(" Y: " + parms.get("y"));
        response.append("</body></html>");
        System.out.println(response.toString());
        Server.writeResponse(t, response.toString());
    }
  }
  
  public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
	    httpExchange.sendResponseHeaders(200, response.length());
	    OutputStream os = httpExchange.getResponseBody();
	    os.write(response.getBytes());
	    os.close();	
  }
  
  public static Map<String, String> queryToMap(String query){
	    Map<String, String> result = new HashMap<String, String>();
	    for (String param : query.split("&")) {
	        String pair[] = param.split("=");
	        if (pair.length>1) {
	            result.put(pair[0], pair[1]);
	        }else{
	            result.put(pair[0], "");
	        }
	    }
	    return result;
  }
}