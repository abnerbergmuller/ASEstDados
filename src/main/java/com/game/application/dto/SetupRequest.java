package com.game.application.dto;

import com.game.domain.model.Personagem;
import java.util.List;

public class SetupRequest {
    private List<String> nomes;
    private List<Personagem> personagens;

    public List<String> getNomes() {
        return nomes;
    }

    public void setNomes(List<String> nomes) {
        this.nomes = nomes;
    }

    public List<Personagem> getPersonagens() {
        return personagens;
    }

    public void setPersonagens(List<Personagem> personagens) {
        this.personagens = personagens;
    }
}
