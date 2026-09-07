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

    // Tant que le domaine n'est pas vérifié, on garde onboarding@resend.dev
    // (fonctionne uniquement vers l'email de ton compte Resend)
    private static final String FROM = "AgroPlateforme <onboarding@resend.dev>";
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate = new RestTemplate();

    public void envoyer(String destinataire, String sujet, String texte) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // On transforme le texte brut en HTML simple (saut de ligne -> <br>)
        String html = texte.replace("\n", "<br>");

        Map<String, Object> body = Map.of(
            "from", FROM,
            "to", new String[]{ destinataire },
            "subject", sujet,
            "html", html
        );

        HttpEntity<Map<String, Object>> requete = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(RESEND_URL, requete, String.class);
    }
}