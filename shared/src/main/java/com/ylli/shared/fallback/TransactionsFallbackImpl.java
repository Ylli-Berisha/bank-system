package com.ylli.shared.fallback;

import com.ylli.shared.clients.TransactionsFeignClient;
import com.ylli.shared.dtos.LoanDto;
import com.ylli.shared.dtos.TransactionDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionsFallbackImpl implements FallbackFactory<TransactionsFeignClient> {
    @Override
    public TransactionsFeignClient create(Throwable cause) {
        return new TransactionsFeignClient() {

            @Override
            public ResponseEntity<TransactionDto> getTransaction(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<TransactionDto> createTransaction(TransactionDto transaction) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<TransactionDto> updateTransaction(String id, TransactionDto transaction) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Void> deleteTransaction(String id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<LoanDto> getLoan(Long id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<LoanDto> createLoan(LoanDto loan) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<LoanDto> updateLoan(Long id, LoanDto loan) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Void> deleteLoan(Long id) {
                return ResponseEntity.status(503).build();
            }
        };
    }
}
