package com.example.centralizer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.centralizer.models.Client;
import com.example.centralizer.repository.ClientRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Optional<Client> getClientById(Integer id) {
        return clientRepository.findById(id);
    }
    
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }
    
    public void deleteClient(Integer id) {
        clientRepository.deleteById(id);
    }
    
    public List<Client> searchClientsByNom(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom);
    }
    
    public List<Client> searchClientsByEmail(String email) {
        return clientRepository.findByEmailContainingIgnoreCase(email);
    }
}