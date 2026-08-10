package com.agriculture.backend.controller;

import com.agriculture.backend.service.RapportNationalService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rapports/national")
public class RapportNationalController {

    private final RapportNationalService service;

    public RapportNationalController(RapportNationalService service) {
        this.service = service;
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('ADMIN_NATIONAL')")
    public ResponseEntity<byte[]> telechargerPdf() {
        byte[] pdf = service.genererRapportPdf();
        String nomFichier = "rapport-agricole-national-" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomFichier + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}