package com.example.comptecourant.ejb;

import com.example.comptecourant.models.CompteCourant;
import com.example.comptecourant.exceptions.CompteCourantException;
import jakarta.ejb.Remote;
import java.util.List;
import java.math.BigDecimal;

@Remote
public interface CompteCourantServiceRemote {
    List<CompteCourant> getAllComptes() throws CompteCourantException;
    CompteCourant getCompteById(Integer id) throws CompteCourantException;
    CompteCourant createCompte(CompteCourant compte) throws CompteCourantException;
    CompteCourant updateCompte(CompteCourant compte) throws CompteCourantException;
    void deleteCompte(Integer id) throws CompteCourantException;
    CompteCourant updateSolde(Integer idCompte, BigDecimal nouveauSolde) throws CompteCourantException;
}
