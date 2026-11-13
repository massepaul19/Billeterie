package cm.billetterie.controller;

import cm.billetterie.dto.AnnulationReponse;
import cm.billetterie.dto.BilletReponse;
import cm.billetterie.service.BilletPdfService;
import cm.billetterie.service.BilletService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private final BilletService service;
    private final BilletPdfService pdf;

    public BilletController(BilletService service, BilletPdfService pdf) {
        this.service = service;
        this.pdf = pdf;
    }

    @GetMapping("/{code}")
    public BilletReponse obtenir(@PathVariable String code) {
        return service.obtenir(code);
    }

    /** Billet PDF avec QR code (pas de "produces" ici : les erreurs restent renvoyées en JSON). */
    @GetMapping("/{code}/pdf")
    public ResponseEntity<byte[]> telecharger(@PathVariable String code) {
        byte[] contenu = pdf.genererPourCode(code);
        String nom = "billet-" + code.replaceAll("[^A-Za-z0-9-]", "") + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nom + "\"")
                .body(contenu);
    }

    @PostMapping("/{code}/annulation")
    public AnnulationReponse annuler(@PathVariable String code) {
        return service.annuler(code);
    }
}
