# 📱 SGI3D App

Application mobile Android développée dans le cadre du projet **SGI3D (Système de Gestion et de Surveillance d'Imprimantes 3D)**.

Cette application permet aux étudiants, opérateurs et administrateurs de gérer les demandes d'impression 3D, de suivre leur évolution et de recevoir des notifications en temps réel.

---

## 🎯 Objectifs

L'application a pour objectif de :

* Faciliter l'envoi de fichiers destinés à l'impression 3D ;
* Centraliser les demandes d'impression ;
* Permettre le suivi de l'état des demandes ;
* Améliorer la communication entre les utilisateurs et les opérateurs ;
* Intégrer le système SGI3D dans une solution mobile moderne.

---

## 🚀 Fonctionnalités

### Authentification

* Connexion sécurisée des utilisateurs
* Gestion des sessions

### Gestion des demandes d'impression

* Création d'une demande d'impression
* Ajout d'un commentaire
* Sélection de fichiers STL ou GCODE
* Envoi des fichiers vers le serveur

### Prévisualisation 3D

* Affichage des modèles STL avant envoi
* Validation visuelle du modèle

### Suivi des demandes

* Consultation des demandes créées
* Affichage du statut de chaque demande
* Mise à jour automatique des informations

### Notifications

* Confirmation d'envoi de demande
* Notifications de changement de statut
* Alertes destinées aux administrateurs et opérateurs

### Gestion des rôles

* Étudiant
* Opérateur
* Administrateur

---

## 🛠️ Technologies utilisées

### Mobile

* Kotlin
* Jetpack Compose
* Navigation Compose
* ViewModel
* StateFlow

### Communication

* Retrofit
* API REST

### Backend

* PHP
* MySQL

### Architecture

* MVVM (Model - View - ViewModel)

---

## 📂 Architecture du projet

```text
Application Android
       │
       ▼
    API REST
       │
       ▼
Serveur NAS SGI3D
       │
       ▼
 Base de données MySQL
```

---
## ⚠️ Limites actuelles du projet

L'application SGI3D repose actuellement sur l'utilisation d'OctoPrint comme système de gestion des imprimantes 3D. Ainsi, seules les imprimantes configurées avec OctoPrint et disposant d'une clé API valide peuvent être intégrées à l'application.

L'ajout d'une nouvelle imprimante nécessite actuellement une intervention au niveau de la base de données. Pour qu'une imprimante soit reconnue par l'application, il est nécessaire d'enregistrer les informations suivantes :

* Nom de l'imprimante ;
* Adresse IP ou URL d'accès ;
* Clé API OctoPrint ;
* Informations de connexion nécessaires à la communication avec l'API.

Une fois ces informations enregistrées, l'imprimante devient visible dans l'application mobile et ses informations peuvent être consultées.

À ce jour, l'application n'est pas totalement finalisée. Durant le développement du prototype, seule une imprimante a pu être entièrement intégrée et testée.

Les fonctionnalités suivantes sont actuellement opérationnelles uniquement pour cette première imprimante :

* Envoi d'un fichier d'impression ;
* Lancement d'une impression ;
* Mise en pause d'une impression ;
* Reprise d'une impression ;
* Arrêt d'une impression.

Les autres imprimantes enregistrées dans le système peuvent être affichées dans l'interface grâce au composant "Printer Card", permettant de consulter leurs informations et leur état, mais elles ne peuvent pas encore être pilotées directement depuis l'application.

## 🔮 Évolutions possibles

Plusieurs améliorations peuvent être envisagées afin de faire évoluer le projet :

### Gestion complète de plusieurs imprimantes

Permettre le pilotage de toutes les imprimantes enregistrées dans le système et non plus uniquement de la première imprimante configurée.

### Ajout dynamique des imprimantes

Développer une interface d'administration permettant d'ajouter ou supprimer des imprimantes directement depuis l'application ou le site web, sans modification manuelle de la base de données.

### Compatibilité avec d'autres interfaces d'impression 3D

Actuellement, l'application est exclusivement compatible avec OctoPrint. Une évolution intéressante serait d'ajouter la prise en charge d'autres solutions de supervision d'impression 3D telles que :

* Fluidd ;
* Mainsail ;
* Klipper ;
* Prusa Connect ;
* Bambu Studio / Bambu Cloud ;
* Creality Print.

Cette évolution permettrait de rendre l'application plus universelle et compatible avec un plus grand nombre d'imprimantes 3D.

### Amélioration des notifications

Mettre en place un système de notifications push en temps réel afin d'informer instantanément les utilisateurs lors :

* du démarrage d'une impression ;
* de la fin d'une impression ;
* d'une erreur ;
* d'une alerte de sécurité.

### Gestion avancée des impressions

Ajouter des fonctionnalités complémentaires telles que :

* historique complet des impressions ;
* statistiques d'utilisation ;
* estimation du coût d'impression ;
* estimation de la consommation de filament ;
* suivi détaillé de l'avancement des impressions.

---

## ⚙️ Installation

1. Cloner le dépôt :

```bash
git clone https://github.com/votre-repository/sgi3d-app.git
```

2. Ouvrir le projet avec Android Studio.

3. Configurer l'URL de l'API dans le fichier de configuration.

4. Compiler et exécuter l'application.

---

## 👨‍💻 Auteur

**Jolan Schambourg**

Projet réalisé dans le cadre du BTS CIEL – Option Informatique et Réseaux (IR) – Session 2026.

---

## 📄 Licence

Projet pédagogique réalisé dans le cadre de la formation BTS CIEL.
