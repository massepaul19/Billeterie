import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/ui/crud.dart';
import '../../../core/utils/format.dart';
import '../data/agence_remboursements_api.dart';

class AgenceRemboursementsPage extends StatelessWidget {
  const AgenceRemboursementsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final api = AgenceRemboursementsApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Remboursements',
      charger: api.lister,
      vide: 'Aucune demande de remboursement.',
      item: (c, r, rech) {
        final demande = r['statut'] == 'DEMANDE';
        return Card(
          child: ListTile(
            leading: const Icon(Icons.payments_outlined),
            title: Text('${r['clientNom'] ?? ''} · ${r['clientTelephone'] ?? ''}'),
            subtitle: Text('${formatFcfa((r['montant'] as num).toInt())} · ${r['motif'] ?? ''}\n${r['statut']}'),
            isThreeLine: true,
            trailing: demande
                ? Row(mainAxisSize: MainAxisSize.min, children: [
                    IconButton(
                        tooltip: 'Marquer comme traité',
                        icon: const Icon(Icons.check, color: Colors.green),
                        onPressed: () => executer(c, () => api.changerStatut(r['id'] as int, 'TRAITE'),
                            apres: rech, succes: 'Remboursement traité.')),
                    IconButton(
                        tooltip: 'Refuser',
                        icon: const Icon(Icons.close, color: Colors.red),
                        onPressed: () async {
                          if (await confirmer(c, 'Refuser ce remboursement ?') && c.mounted) {
                            await executer(c, () => api.changerStatut(r['id'] as int, 'REFUSE'),
                                apres: rech, succes: 'Remboursement refusé.');
                          }
                        }),
                  ])
                : null,
          ),
        );
      },
    );
  }
}
