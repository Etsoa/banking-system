package com.example.serveurpret.services;

import com.example.serveurpret.models.Pret;
import com.example.serveurpret.repository.PretRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class PretService {

    @EJB
    private PretRepository pretRepository;

    public List<Pret> getAllPrets() {
        return pretRepository.findAll();
    }

    public List<Pret> getPretsByClientId(String clientId) {
        return pretRepository.findByClientId(clientId);
    }

    public Pret getPretById(Integer id) {
        return pretRepository.findById(id);
    }

    public void createPret(Pret pret) {
        pretRepository.save(pret);
    }

    public Pret updatePret(Pret pret) {
        return pretRepository.update(pret);
    }

    public void deletePret(Pret pret) {
        pretRepository.delete(pret);
    }
}
