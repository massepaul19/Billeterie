# TODO Sécurité (Back A, Sprint 1)

1. Ajouter dans pom.xml : spring-boot-starter-security + jjwt (api, impl, jackson).
2. Hachage des mots de passe : BCryptPasswordEncoder (jamais de mot de passe en clair).
3. POST /api/auth/connexion : téléphone + mot de passe -> JWT contenant `role` et `agenceId`.
4. Filtre JWT : /api/admin/** = SUPER_ADMIN ; /api/agence/** = ADMIN_AGENCE ; le reste est public.
5. RG14 : dans les services /api/agence/**, l'agenceId vient TOUJOURS du jeton, jamais du corps de la requête.
6. Seed : créer un super administrateur au démarrage s'il n'existe pas (voir DemoDataLoader).
7. Tests : 401 sans jeton, 403 avec le mauvais rôle, accès interdit aux données d'une autre agence.
