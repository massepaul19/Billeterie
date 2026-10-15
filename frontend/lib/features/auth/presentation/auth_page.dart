import 'package:flutter/material.dart';

/// Connexion - propriétaire : Front C (Abdias) - Sprint 1
/// À coder : Écran de connexion (téléphone + mot de passe) : garde le jeton et redirige selon le rôle. À livrer pour J4.
/// Voir TODO.md de ce dossier et docs/API.md.
class AuthPage extends StatelessWidget {
  const AuthPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Connexion")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Écran de connexion (téléphone + mot de passe) : garde le jeton et redirige selon le rôle. À livrer pour J4.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}