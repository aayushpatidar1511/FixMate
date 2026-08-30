package com.fixmate.controller;

import com.fixmate.dto.response.ApiResponse;
import com.fixmate.model.ServiceEntity;
import com.fixmate.service.CategoryCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final CategoryCatalogService catalogService;

    public ServiceController(CategoryCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceEntity>>> getActiveServices() {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getActiveServices()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceEntity>> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getServiceById(id)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ServiceEntity>>> getServicesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getServicesByCategory(categoryId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ServiceEntity>>> searchServices(@RequestParam(name = "q") String query) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.searchServices(query)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> createService(@RequestBody ServiceEntity service) {
        Long id = catalogService.createService(service);
        return ResponseEntity.ok(ApiResponse.ok("Service created successfully", id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateService(@PathVariable Long id, @RequestBody ServiceEntity service) {
        service.setServiceId(id);
        catalogService.updateService(service);
        return ResponseEntity.ok(ApiResponse.ok("Service updated successfully", null));
    }
}
