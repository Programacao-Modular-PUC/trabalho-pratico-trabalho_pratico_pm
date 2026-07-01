package com.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Getter
@Setter
@Entity
@Table(name = "residencias")
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String email;

   @OneToMany(mappedBy = "residencia",
           cascade = CascadeType.ALL,
           fetch = FetchType.LAZY)
@JsonManagedReference
private List<Quarto> quartos = new ArrayList<>();

    @JsonIgnore
@OneToMany(mappedBy = "residencia",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY)
private List<Aluguel> historicoAlugueis = new ArrayList<>();
}
