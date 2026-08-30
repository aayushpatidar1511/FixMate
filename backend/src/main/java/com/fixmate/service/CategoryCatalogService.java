package com.fixmate.service;

import com.fixmate.exception.ResourceNotFoundException;
import com.fixmate.model.Category;
import com.fixmate.model.ServiceEntity;
import com.fixmate.repository.CategoryRepository;
import com.fixmate.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryCatalogService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;

    public CategoryCatalogService(CategoryRepository categoryRepository, ServiceRepository serviceRepository) {
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findAllActive();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    public Long createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void updateCategory(Category category) {
        categoryRepository.update(category);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.delete(categoryId);
    }

    public List<ServiceEntity> getActiveServices() {
        return serviceRepository.findAllActive();
    }

    public List<ServiceEntity> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryId(categoryId);
    }

    public ServiceEntity getServiceById(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
    }

    public Long createService(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    public void updateService(ServiceEntity service) {
        serviceRepository.update(service);
    }

    public List<ServiceEntity> searchServices(String query) {
        return serviceRepository.searchByName(query);
    }
}
