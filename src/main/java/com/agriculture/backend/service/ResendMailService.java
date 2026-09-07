package com.agriculture.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ResendMailService {

    @Value("${resend.api.key}")
    private String apiKey;

    /*
     * Adresse autorisée par Resend en mode test.
     */
    @Value("${resend.test.recipient:anniceflorencia@gmail.com}")
    private String testRecipient;

    private static final String FROM =
            "AgroPlateforme <onboarding@resend.dev>";

    private static final String RESEND_URL =
            "https://api.resend.com/emails";

    private final RestTemplate restTemplate = new RestTemplate();

    public void envoyer(String destinataire, String sujet, String texte) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        /*
         * Resend est actuellement en mode test.
         *
         * Tous les emails sont donc redirigés vers
         * l'adresse autorisée par Resend.
         *
         * Le destinataire original reste indiqué dans
         * le contenu de l'email afin de savoir à quel
         * compte correspond le code OTP.
         */
        String html =
                "<p>" + texte.replace("\n", "<br>") + "</p>"
                + "<hr>"
                + "<p><strong>Destinataire prévu :</strong> "
                + destinataire
                + "</p>";

        Map<String, Object> body = Map.of(
                "from", FROM,
                "to", new String[]{ testRecipient },
                "subject", sujet,
                "html", html
        );

        HttpEntity<Map<String, Object>> requete =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                RESEND_URL,
                requete,
                String.class
        );
    }
}