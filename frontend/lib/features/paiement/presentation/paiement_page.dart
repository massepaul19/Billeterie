import 'package:flutter/material.dart';

/// Paiement - propriétaire : Front D (Bouba) - Sprint 2
/// À coder : Choix MTN ou Orange, numéro, paiement simulé, puis succès ou échec.
/// Voir TODO.md de ce dossier et docs/API.md.
class PaiementPage extends StatelessWidget {
  const PaiementPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Paiement")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Choix MTN ou Orange, numéro, paiement simulé, puis succès ou échec.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}