package com.example.points.controller;

import org.springframework.web.bind.annotation.*;
import com.example.points.model.Transaction;
import java.util.*;

@RestController
public class PointsController {

    // Store all transactions and current balances by payer
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, Integer> payerBalances = new HashMap<>();

    // Add points endpoint
    @PostMapping("/add")
    public void addPoints(@RequestBody Transaction transaction) {
        // Update payer balance
        payerBalances.put(transaction.getPayer(), 
            payerBalances.getOrDefault(transaction.getPayer(), 0) + transaction.getPoints());
        
        // Store transaction
        transactions.add(transaction);
    }

    // Spend points endpoint
    @PostMapping("/spend")
    public List<Map<String, Object>> spendPoints(@RequestBody Map<String, Integer> spendRequest) {
        int pointsToSpend = spendRequest.get("points");

        // Check if user has enough points
        int totalPoints = payerBalances.values().stream().mapToInt(Integer::intValue).sum();
        if (pointsToSpend > totalPoints) {
            throw new RuntimeException("Not enough points.");
        }

        // List to hold the response of points spent by payer
        List<Map<String, Object>> pointsSpent = new ArrayList<>();

        // Sort transactions by timestamp to spend the oldest points first
        transactions.sort(Comparator.comparing(Transaction::getTimestamp));

        for (Transaction transaction : transactions) {
            if (pointsToSpend <= 0) break; // Stop if we've spent the required points
            
            // Determine how many points to spend from this transaction
            int availablePoints = Math.min(transaction.getPoints(), pointsToSpend);
            if (availablePoints <= 0) continue; // Skip if this transaction has no points

            // Reduce points to spend and update the payer's balance
            pointsToSpend -= availablePoints;
            payerBalances.put(transaction.getPayer(), payerBalances.get(transaction.getPayer()) - availablePoints);

            // Add the spent amount for this payer to the response
            Map<String, Object> spent = new HashMap<>();
            spent.put("payer", transaction.getPayer());
            spent.put("points", -availablePoints);
            pointsSpent.add(spent);

            // Update the transaction to reflect the remaining points
            transaction.setPoints(transaction.getPoints() - availablePoints);
        }

        return pointsSpent;
    }

    // Get balance endpoint
    @GetMapping("/balance")
    public Map<String, Integer> getBalance() {
        return payerBalances;
    }
}
