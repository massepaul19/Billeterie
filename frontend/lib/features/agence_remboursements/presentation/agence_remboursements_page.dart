import 'package:flutter/material.dart';

/// Remboursements (agence) - propriétaire : Front C (Abdias) - Sprint 2
/// À coder : Liste des demandes de remboursement avec boutons Traité et Refusé.
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceRemboursementsPage extends StatelessWidget {
  const AgenceRemboursementsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Remboursements (agence)")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste des demandes de remboursement avec boutons Traité et Refusé.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}