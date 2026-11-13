package cm.billetterie.event;

/** Publié quand un billet vient d'être créé suite à un paiement réussi. */
public record BilletPayeEvent(Long billetId) {
}
