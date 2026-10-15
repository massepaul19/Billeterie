import 'package:flutter/material.dart';

/// Vérification des billets - propriétaire : Front C (Abdias) - Sprint 2
/// À coder : Scan du QR (ou recherche nom/téléphone) et affichage immédiat du statut du billet.
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceVerificationPage extends StatelessWidget {
  const AgenceVerificationPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Vérification des billets")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Scan du QR (ou recherche nom/téléphone) et affichage immédiat du statut du billet.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}