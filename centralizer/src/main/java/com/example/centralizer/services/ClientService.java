package com.example.centralizer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.centralizer.models.Client;
import com.example.centralizer.models.HistoriqueStatutClient;
import com.example.centralizer.repository.ClientRepository;
import com.example.centralizer.repository.HistoriqueStatutClientRepository;
import com.example.centralizer.exceptions.ClientNotFoundException;
import java.util.List;

@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private HistoriqueStatutClientRepository historiqueStatutClientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Client getClientById(Integer id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new ClientNotFoundException("Client non trouvé : " + id));
    }
    
    public Client saveClient(Client client) {
        // Vérifier si c'est un nouveau client (ID null ou 0)
        boolean isNewClient = (client.getId() == null || client.getId() == 0);
        
        // Sauvegarder le client
        Client savedClient = clientRepository.save(client);
        
        // Si c'est un nouveau client, ajouter un historique "actif"
        if (isNewClient) {
            HistoriqueStatutClient historique = new HistoriqueStatutClient(savedClient.getId(), "actif");
            historiqueStatutClientRepository.save(historique);
        }
        
        return savedClient;
    }
    
    public void deleteClient(Integer id) {
        // Vérifier que le client existe avant de le marquer comme inactif
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ClientNotFoundException("Client introuvable avec l'ID: " + id));
        
        // Créer un historique de statut "inactif"
        HistoriqueStatutClient historique = new HistoriqueStatutClient(id, "inactif");
        historiqueStatutClientRepository.save(historique);
    }
    
    public List<Client> searchClientsByNom(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom);
    }
    
    public List<Client> searchClientsByEmail(String email) {
        return clientRepository.findByEmailContainingIgnoreCase(email);
    }
    
    public String getStatutClient(Integer idClient) {
        List<HistoriqueStatutClient> historiques = historiqueStatutClientRepository.findByIdClientOrderByDateChangementDesc(idClient);
        if (!historiques.isEmpty()) {
            return historiques.get(0).getStatut();
        }
        return "actif"; // Statut par défaut si aucun historique
    }
    
    public void changerStatutClient(Integer idClient, String nouveauStatut) {
        // Créer un nouvel historique de statut
        HistoriqueStatutClient historique = new HistoriqueStatutClient(idClient, nouveauStatut);
        historiqueStatutClientRepository.save(historique);
    }
}