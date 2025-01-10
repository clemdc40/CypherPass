# CypherPass
Password manager
# Gestionnaire de Mots de Passe en Java

## Description
Ce projet est un gestionnaire de mots de passe sécurisé développé en Java. Il permet de stocker, gérer et générer des mots de passe tout en garantissant une sécurité optimale grâce à l'utilisation d'algorithmes de chiffrement robustes. 

## Fonctionnalités
- **Stockage sécurisé des mots de passe** : Utilisation d'une base de données SQLite avec chiffrement AES-256.
- **Génération de mots de passe forts** : Options personnalisables (longueur, caractères spéciaux, etc.).
- **Recherche et gestion** : Ajouter, modifier, supprimer et rechercher des mots de passe.
- **Interface utilisateur** : Une application conviviale grâce à JavaFX.
- **Authentification principale** : Protection par mot de passe principal sécurisé.

## Prérequis
Avant de lancer le projet, assurez-vous que les outils suivants sont installés sur votre machine :

- **Java** : Version 11 ou supérieure
- **SQLite** : Bibliothèque JDBC intégrée dans le projet
- **JavaFX** : Ajoutée comme dépendance externe

## Installation
1. Clonez ce dépôt sur votre machine locale :
   ```bash
   git clone https://github.com/votre-utilisateur/gestionnaire-mdp-java.git
   ```
2. Ouvrez le projet dans votre IDE préféré (IntelliJ IDEA, Eclipse, ou VS Code).
3. Ajoutez les bibliothèques nécessaires au projet :
   - JDBC pour SQLite
   - JavaFX
4. Configurez votre IDE pour inclure JavaFX en suivant [ce guide](https://openjfx.io/openjfx-docs/).

## Utilisation
1. Lancez le fichier principal : `Main.java`.
2. Configurez un mot de passe principal lors de la première utilisation.
3. Utilisez l'interface pour :
   - Ajouter un nouveau mot de passe.
   - Rechercher des mots de passe enregistrés.
   - Modifier ou supprimer des entrées existantes.
4. Profitez du générateur de mots de passe pour créer des mots de passe forts et sécurisés.

## Schéma de la Base de Données
```sql
CREATE TABLE passwords (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_name TEXT NOT NULL,
    username TEXT NOT NULL,
    encrypted_password TEXT NOT NULL,
    website_url TEXT,
);
```

## Sécurité
- **Chiffrement des données** : Toutes les données sont chiffrées à l'aide de l'algorithme AES-256 avec une clé dérivée via PBKDF2.
- **Hachage sécurisé** : Le mot de passe principal est haché avec SHA-256 pour éviter le stockage en clair.

## Contributions
Les contributions sont les bienvenues ! Si vous souhaitez ajouter des fonctionnalités ou améliorer le projet, veuillez suivre ces étapes :
1. Forkez ce dépôt.
2. Créez une branche pour vos modifications :
   ```bash
   git checkout -b ajout-fonctionnalite
   ```
3. Faites vos modifications et soumettez une Pull Request.

## Licence
Ce projet est sous licence [MIT](LICENSE). Vous êtes libre de l'utiliser, de le modifier et de le distribuer tant que la licence d'origine est incluse.

## Améliorations Futures
- Intégration avec un service cloud pour la synchronisation des mots de passe.
- Version mobile pour Android/iOS.
- Support pour le remplissage automatique des champs dans les navigateurs.
- Authentification biométrique.

---
Merci d'avoir utilisé le Gestionnaire de Mots de Passe en Java ! 🚀
