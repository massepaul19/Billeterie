package cm.billetterie.service;

import cm.billetterie.entity.Billet;
import cm.billetterie.entity.enums.StatutBillet;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.BilletRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BilletPdfService {

    private static final DateTimeFormatter FORMAT_COURT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BilletRepository billets;
    private final BilletDonneesService donnees;

    public BilletPdfService(BilletRepository billets, BilletDonneesService donnees) {
        this.billets = billets;
        this.donnees = donnees;
    }

    /** PDF du billet à partir de son code (refuse un billet annulé). */
    public byte[] genererPourCode(String code) {
        Billet billet = billets.findByCodeUnique(code == null ? "" : code.trim().toUpperCase())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Billet introuvable."));
        if (billet.getStatut() == StatutBillet.ANNULE) {
            throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "BILLET_INVALIDE", "Ce billet a été annulé.");
        }
        return generer(donnees.charger(billet));
    }

    public byte[] generer(BilletDonnees d) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Font titre = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(20, 70, 140));
            Font slogan = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.DARK_GRAY);
            Font enTete = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
            Font etiquette = new Font(Font.HELVETICA, 8, Font.BOLD, Color.GRAY);
            Font valeur = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
            Font petit = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY);

            Document doc = new Document(PageSize.A5, 30, 30, 30, 30);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph nomAgence = new Paragraph(d.agence(), titre);
            nomAgence.setAlignment(Element.ALIGN_CENTER);
            doc.add(nomAgence);
            if (d.slogan() != null && !d.slogan().isBlank()) {
                Paragraph s = new Paragraph(d.slogan(), slogan);
                s.setAlignment(Element.ALIGN_CENTER);
                doc.add(s);
            }
            Paragraph t = new Paragraph("BILLET DE VOYAGE", enTete);
            t.setAlignment(Element.ALIGN_CENTER);
            t.setSpacingBefore(10);
            t.setSpacingAfter(14);
            doc.add(t);

            PdfPTable table = new PdfPTable(new float[] {3f, 2f});
            table.setWidthPercentage(100);

            PdfPCell gauche = new PdfPCell();
            gauche.setBorder(PdfPCell.NO_BORDER);
            gauche.addElement(ligne("PASSAGER", d.nomComplet(), etiquette, valeur));
            gauche.addElement(ligne("DÉPART", d.lieuDepart(), etiquette, valeur));
            gauche.addElement(ligne("ARRIVÉE", d.lieuArrivee(), etiquette, valeur));
            gauche.addElement(ligne("DATE ET HEURE", d.departFormate(), etiquette, valeur));
            gauche.addElement(ligne("VÉHICULE", d.equipements(), etiquette, valeur));
            gauche.addElement(ligne("PRIX", d.montantFormate(), etiquette, valeur));
            table.addCell(gauche);

            PdfPCell droite = new PdfPCell(qr(d.code()), true);
            droite.setBorder(PdfPCell.NO_BORDER);
            droite.setHorizontalAlignment(Element.ALIGN_CENTER);
            droite.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(droite);
            doc.add(table);

            Paragraph code = new Paragraph("Code du billet : " + d.code(), enTete);
            code.setAlignment(Element.ALIGN_CENTER);
            code.setSpacingBefore(12);
            doc.add(code);

            String validite = d.valideJusquA() == null ? ""
                    : " Valable jusqu'au " + FORMAT_COURT.format(d.valideJusquA()) + ".";
            Paragraph pied = new Paragraph(
                    "Présentez ce billet (QR code) à l'embarquement, avec une pièce d'identité." + validite, petit);
            pied.setAlignment(Element.ALIGN_CENTER);
            pied.setSpacingBefore(10);
            doc.add(pied);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "PDF_ERREUR",
                    "Impossible de générer le billet PDF.");
        }
    }

    private static Image qr(String contenu) throws Exception {
        BitMatrix matrice = new QRCodeWriter()
                .encode(contenu, BarcodeFormat.QR_CODE, 300, 300, Map.of(EncodeHintType.MARGIN, 1));
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrice, "PNG", png);
        return Image.getInstance(png.toByteArray());
    }

    private static Paragraph ligne(String etiquette, String valeur, Font fe, Font fv) {
        Paragraph p = new Paragraph();
        p.setSpacingAfter(7);
        p.add(new Chunk(etiquette + "\n", fe));
        p.add(new Chunk(valeur == null ? "" : valeur, fv));
        return p;
    }
}
