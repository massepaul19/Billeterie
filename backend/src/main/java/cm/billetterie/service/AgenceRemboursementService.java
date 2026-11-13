package cm.billetterie.service;

import cm.billetterie.dto.RemboursementAgenceReponse;
import cm.billetterie.entity.*;
import cm.billetterie.entity.enums.StatutRemboursement;
import cm.billetterie.exception.ApiException;
import cm.billetterie.repository.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgenceRemboursementService {

    private final RemboursementRepository remboursements;
    private final ReservationAgenceRepository reservations;
    private final VoyageAgenceRepository voyages;
    private final ClientRepository clients;

    public AgenceRemboursementService(RemboursementRepository remboursements,
                                      ReservationAgenceRepository reservations,
                                      VoyageAgenceRepository voyages, ClientRepository clients) {
        this.remboursements = remboursements;
        this.reservations = reservations;
        this.voyages = voyages;
        this.clients = clients;
    }

    public List<RemboursementAgenceReponse> lister(Long agenceId) {
        Map<Long, Reservation> parId = reservations.findParAgence(agenceId).stream()
                .collect(Collectors.toMap(Reservation::getId, Function.identity()));
        if (parId.isEmpty()) return List.of();
        return remboursements.findByReservationIdInOrderByCreeLeDesc(parId.keySet()).stream()
                .map(rb -> vers(rb, parId.get(rb.getReservationId()))).toList();
    }

    @Transactional
    public RemboursementAgenceReponse changerStatut(Long agenceId, Long id, StatutRemboursement nouveau) {
        Remboursement rb = remboursements.findById(id).orElseThrow(this::introuvable);
        Reservation res = reservations.findById(rb.getReservationId()).orElseThrow(this::introuvable);
        voyages.findByIdAndAgenceId(res.getVoyageId(), agenceId).orElseThrow(this::introuvable);
        if (nouveau == StatutRemboursement.DEMANDE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Statut attendu : TRAITE ou REFUSE.");
        }
        if (rb.getStatut() != StatutRemboursement.DEMANDE) {
            throw new ApiException(HttpStatus.CONFLICT, "CONFLIT", "Cette demande a déjà été traitée.");
        }
        rb.setStatut(nouveau);
        return vers(remboursements.save(rb), res);
    }

    private RemboursementAgenceReponse vers(Remboursement rb, Reservation res) {
        Client c = (res == null || res.getClientId() == null) ? null
                : clients.findById(res.getClientId()).orElse(null);
        return new RemboursementAgenceReponse(rb.getId(), rb.getReservationId(), rb.getMontant(), rb.getMotif(),
                rb.getStatut(), rb.getCreeLe(), c == null ? null : c.getNom(), c == null ? null : c.getPrenom());
    }

    private ApiException introuvable() {
        return new ApiException(HttpStatus.NOT_FOUND, "INTROUVABLE", "Remboursement introuvable.");
    }
}
