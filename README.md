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

# 🔑 Guide d'utilisation de l'application CipherPass

## 👤 **Présentation de CipherPass**
CipherPass est une application de gestion de mots de passe sécurisée qui permet de stocker et de chiffrer vos identifiants de connexion. Les mots de passe sont chiffrés avec AES-256 et protégés par un mot de passe maître.

---

## 📊 **Structure des fichiers**

```
/keystore/
├── masterpwd.txt    (Hash du mot de passe maître)
├── keeper.key       (Fichier clé pour la Composite Key)
├── salt.dat         (Salt pour la dérivation de clé)
├── passwd.txt       (Mot de passe chiffré temporaire)
└── database.db      (Base de données chiffrée des mots de passe)
```

---

## 🔑 **Première utilisation**

1. **Démarrer l'application** :
   ```bash
   mvn exec:java
   ```
2. **Définir le mot de passe maître** :
   - Saisissez un mot de passe maître qui servira à protéger tous vos mots de passe.
   
3. **Sauvegarder un mot de passe** :
   - L'application vous demande ensuite d'entrer un mot de passe à sauvegarder.
   - Ce mot de passe sera chiffré et stocké en toute sécurité.

---

## 🛠️ **Utilisation régulière**

1. **Connexion** :
   - Entrez votre mot de passe maître pour accéder à vos données.

2. **Menu interactif** :
   - **1** : Ajouter un nouveau mot de passe.
   - **2** : Lister tous les mots de passe sauvegardés.
   - **3** : Quitter l'application.

---

## 🔐 **Fonctionnalités**

### ➕ **Ajouter un mot de passe**
- Saisissez les informations suivantes :
  - **Site web**
  - **Nom d'utilisateur**
  - **Mot de passe**
- Le mot de passe sera chiffré et stocké dans la base de données.

### 📃 **Lister les mots de passe**
- Tous les mots de passe enregistrés seront affichés déchiffrés sous cette forme :
  
  ```
  🌐 Site : github.com | 👤 Utilisateur : clement | 🔑 Mot de passe : MotDePasse123
  🌐 Site : gmail.com  | 👤 Utilisateur : clem@gmail.com | 🔑 Mot de passe : MonSuperMdp
  ```

### ⏹️ **Quitter l'application**
- Entrez **3** pour fermer l'application.

---

## ⚠️ **Recommandations de sécurité**

1. **Mot de passe maître** : Utilisez un mot de passe complexe et unique.
2. **Sauvegarde des données** : Faites régulièrement des sauvegardes de votre dossier `keystore`.
3. **Fichier `.gitignore`** : Ajoutez `/keystore/` pour éviter de versionner vos données sensibles.
4. **Protection des fichiers** : Protégez le dossier `keystore` avec des permissions restrictives :
   ```bash
   chmod 700 keystore/
   ```
---
Merci d'avoir utilisé le Gestionnaire de Mots de Passe en Java ! 🚀
