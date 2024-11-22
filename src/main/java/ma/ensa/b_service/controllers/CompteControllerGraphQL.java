package ma.ensa.b_service.controllers;


import lombok.AllArgsConstructor;
import ma.ensa.b_service.entities.Compte;

import ma.ensa.b_service.entities.Transaction;
import ma.ensa.b_service.entities.TransactionInput;
import ma.ensa.b_service.entities.TransactionType;
import ma.ensa.b_service.repostories.CompteRepository;

import ma.ensa.b_service.repostories.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;


    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return compteRepository.findById(id).orElseThrow(
                () -> new RuntimeException(String.format("Compte with id %s not found", id))
        );
    }

    @MutationMapping
    public Compte saveCompte(@Argument Compte compte) {
        return compteRepository.save(compte);
    }


    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;
        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }
    @QueryMapping
    public List<Transaction> allTransactions() {
        return transactionRepository.findAll();
    }

    @QueryMapping
    public List<Transaction> compteTransactions(@Argument Long compteId) {
        return transactionRepository.findByCompteId(compteId);
    }

    @QueryMapping
    public Map<String, Object> transactionStats() {
        long totalCount = transactionRepository.countTotalTransactions();
        double totalDeposits = transactionRepository.sumTotalDeposits() != null
                ? transactionRepository.sumTotalDeposits()
                : 0.0;
        double totalWithdrawals = transactionRepository.sumTotalWithdrawals() != null
                ? transactionRepository.sumTotalWithdrawals()
                : 0.0;

        return Map.of(
                "totalCount", totalCount,
                "totalDeposits", totalDeposits,
                "totalWithdrawals", totalWithdrawals
        );
    }



    @MutationMapping
    public Transaction addTransaction(@Argument("transaction") TransactionInput input) {
        if (input == null) {
            throw new RuntimeException("TransactionInput cannot be null");
        }

        Compte compte = compteRepository.findById(input.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        if (input.getType().equalsIgnoreCase("RETRAIT") && compte.getSolde() < input.getMontant()) {
            throw new RuntimeException("Insufficient balance");
        }
        TransactionType transactionType = TransactionType.valueOf(input.getType().toUpperCase());
        double newSolde = transactionType == TransactionType.DEPOT
                ? compte.getSolde() + input.getMontant()
                : compte.getSolde() - input.getMontant();
        compte.setSolde(newSolde);
        compteRepository.save(compte);

        Transaction transaction = new Transaction();
        transaction.setMontant(input.getMontant());
        transaction.setDate(LocalDate.parse(input.getDate()));
        transaction.setType(transactionType);
        transaction.setCompte(compte);

        return transactionRepository.save(transaction);
    }



}
