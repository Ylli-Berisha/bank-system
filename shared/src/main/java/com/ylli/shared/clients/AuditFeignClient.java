package com.ylli.shared.clients;

import com.ylli.shared.dtos.AuditDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "audit-service", path = "/api/audit")
public interface AuditFeignClient {

    @GetMapping("/get/{id}")
    ResponseEntity<AuditDto> getAudit(@PathVariable("id") Long id);

    @PostMapping("/create")
    ResponseEntity<AuditDto> createAudit(@RequestBody AuditDto audit);

    @PutMapping("/update/{id}")
    ResponseEntity<AuditDto> updateAudit(@PathVariable("id") Long id, @RequestBody AuditDto audit);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteAudit(@PathVariable("id") Long id);
}
