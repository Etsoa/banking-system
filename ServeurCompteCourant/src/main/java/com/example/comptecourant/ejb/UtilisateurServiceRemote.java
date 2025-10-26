package com.example.comptecourant.ejb;

import com.example.comptecourant.models.Utilisateur;
import com.example.comptecourant.exceptions.CompteCourantException;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface UtilisateurServiceRemote {
    boolean login(String nomUtilisateur, String motDePasse) throws CompteCourantException;
    void logout();
    boolean estConnecte();
    Utilisateur getUtilisateurConnecte() throws CompteCourantException;
    Integer getRoleUtilisateurConnecte() throws CompteCourantException;
    boolean aAutorisationPour(String nomTable, String nomAction);
    void exigerConnexion() throws CompteCourantException;
    void exigerAutorisation(String nomTable, String nomAction) throws CompteCourantException;
    
    // CRUD pour utilisateurs
    List<Utilisateur> getAllUtilisateurs() throws CompteCourantException;
    Utilisateur getUtilisateurById(Integer id) throws CompteCourantException;
    Utilisateur createUtilisateur(Utilisateur utilisateur) throws CompteCourantException;
    Utilisateur updateUtilisateur(Utilisateur utilisateur) throws CompteCourantException;
    void deleteUtilisateur(Integer id) throws CompteCourantException;
}
