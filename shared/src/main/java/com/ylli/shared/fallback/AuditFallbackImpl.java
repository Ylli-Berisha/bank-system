package com.ylli.shared.fallback;

import com.ylli.shared.clients.AuditFeignClient;
import com.ylli.shared.dtos.AuditDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditFallbackImpl implements FallbackFactory<AuditFeignClient> {
    @Override
    public AuditFeignClient create(Throwable cause) {
        return new AuditFeignClient() {

            @Override
            public ResponseEntity<AuditDto> getAudit(Long id) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AuditDto> createAudit(AuditDto audit) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<AuditDto> updateAudit(Long id, AuditDto audit) {
                return ResponseEntity.status(503).build();
            }

            @Override
            public ResponseEntity<Void> deleteAudit(Long id) {
                return ResponseEntity.status(503).build();
            }
        };
    }
}
