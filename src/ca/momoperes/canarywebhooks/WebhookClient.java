package ca.momoperes.canarywebhooks;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONObject;

public class WebhookClient implements Runnable {

    private final URI target;
    private WebhookIdentifier identifier;

    protected WebhookClient(URI target, WebhookIdentifier identifier) {
        this.target = target;
        this.identifier = identifier;
    }

    public Response sendPayload(Payload payload) throws IOException {
    	
    	ArrayList<Response> list = new ArrayList<Response>();
    	Response response = null;
    	Thread thread = new Thread(new Runnable() {
    		
    	    @Override
    	    public void run() {
    	    	PayloadObject object = payload.toObject();
    	        //System.out.println(object.toJSONString());
    	         try {
					final Response responsed = executePost(object);
					list.add(responsed);
				} catch (IOException e) {
					e.printStackTrace();
				}        
    	    }
    	            
    	});
    	        
    	thread.start();
    	
    	if (list.size() > 0) {
    		if (list.get(0) != null) {
    			response = list.get(0);
    			list.clear();
    		}
    	}
    	
    	return response;
    }

    public Response executePost(String body, ContentType contentType) throws IOException {
        return Request.Post(target).bodyString(body, contentType).execute();
    }

    public Response executePost(JSONObject object) throws IOException {
        return executePost(object.toJSONString(), ContentType.APPLICATION_JSON);
    }

    public URI getTarget() {
        return target;
    }

    public WebhookIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(WebhookIdentifier identifier) {
        this.identifier = identifier;
    }

	@Override
	public void run() {
				
	}
}
