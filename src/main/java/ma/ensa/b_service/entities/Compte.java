package ma.ensa.b_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;


@Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Compte{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private double solde;

        @Temporal(TemporalType.DATE)
        private LocalDate dateCreation;

        @Enumerated(EnumType.STRING)
        private TypeCompte type;



        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public double getSolde() {
            return solde;
        }

        public void setSolde(double solde) {
            this.solde = solde;
        }

        public LocalDate getDateCreation() {
            return dateCreation;
        }

        public void setDateCreation(LocalDate dateCreation) {
            this.dateCreation = dateCreation;
        }

        public TypeCompte getTyoe() {
            return type;
        }

        public void setTyoe(TypeCompte tyoe) {
            this.type = tyoe;
        }
    }

