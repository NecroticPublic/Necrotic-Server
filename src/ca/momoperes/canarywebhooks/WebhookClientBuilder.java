package ca.momoperes.canarywebhooks;

import java.net.URI;
import java.net.URISyntaxException;

public class WebhookClientBuilder {

    private static final String CANARY_URL = "https://canary.discordapp.com/api/webhooks/";

    private URI uri;
    private WebhookIdentifier identifier;

    public WebhookClient build() {
        if (this.uri == null && identifier == null) {
            return null;
        }
        if (this.uri != null) {
            return new WebhookClient(this.uri, identifier);
        }
        try {
            this.uri = new URI(CANARY_URL + identifier.getId() + "/" + identifier.getToken());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new WebhookClient(uri, identifier);
    }

    public WebhookClientBuilder withURI(URI url) {
        this.uri = url;
        return this;
    }

    public WebhookClientBuilder withIdentifier(WebhookIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }
}
