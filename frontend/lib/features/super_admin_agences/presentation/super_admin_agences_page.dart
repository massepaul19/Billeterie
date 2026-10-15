import 'package:flutter/material.dart';

/// Agences (super admin) - propriétaire : Front C (Abdias) - Sprint 1
/// À coder : Liste des agences (activer/désactiver), formulaire avec logo, création du compte admin de l'agence.
/// Voir TODO.md de ce dossier et docs/API.md.
class SuperAdminAgencesPage extends StatelessWidget {
  const SuperAdminAgencesPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Agences (super admin)")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste des agences (activer/désactiver), formulaire avec logo, création du compte admin de l'agence.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}