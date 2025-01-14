# 📂 CipherPass - Password Manager

![CipherPass](https://img.shields.io/badge/version-1.0-blue.svg) ![Java](https://img.shields.io/badge/Java-17+-green.svg)

## 🚀 Présentation

**CipherPass** est un gestionnaire de mots de passe sécurisé qui permet de stocker, gérer et chiffrer les mots de passe de manière confidentielle.

---

## 🗂️ Structure du Projet

```bash
CipherPass/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cipher/
│   │   │       ├── controllers/
│   │   │       └── Outils/
│   │   └── resources/
├── keystore/
├── pom.xml
└── README.md
```

---

## 🔑 Fonctionnalités

- 🔒 **Authentification Sécurisée** avec mot de passe maître (hashé SHA-256)
- 🛡️ **Chiffrement AES-256** des mots de passe avec clé dérivée via PBKDF2
- 📝 **Gestion des mots de passe** (Ajout, Suppression, Visualisation)
- 🔄 **Générateur de mots de passe** personnalisé

---

## ⚙️ Installation

### Prérequis
- **Java JDK 17+**
- **Maven**

### Instructions
```bash
# 1. Cloner le dépôt
git clone https://github.com/utilisateur/CipherPass.git

# 2. Installer les dépendances
cd CipherPass
mvn clean install

# 3. Lancer l'application
mvn javafx:run
```

---

## 🧪 Tests

### Lancer les tests
```bash
mvn test
```

### Couverture des tests
- ✅ **Unitaires** : chiffrement/déchiffrement, gestion de base de données
- ✅ **Intégration** : flux complet (Authentification → Ajout → Suppression)

---

## 🔐 Sécurité

- **Chiffrement AES-256** avec **PBKDF2** (100 000 itérations)
- **Hash SHA-256** pour le mot de passe maître

---

## 📅 Feuille de Route

- [ ] 🔍 Authentification biométrique
- [ ] 🔒 Chiffrement complet de la base de données
- [ ] ☁️ Sauvegardes cloud chiffrées

---

## 👤 Auteur

**Clément Da Cruz**  
_Étudiant en 3e année de Licence NEC à l'Université de Pau et des Pays de l'Adour_

---

## 📄 Licence

Ce projet est sous licence **Apache License 2.0**.

---

**🔐 Protégez vos données avec CipherPass !**

