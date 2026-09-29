package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.dto.*;
import com.manacommunity.homeservices.service.PetRegistryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetRegistryService petRegistryService;

    /**
     * GET /pets
     * Community pet directory
     */
    @GetMapping
    public ResponseEntity<List<PetDto>> getPets(
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String search) {
        log.info("Fetching pets list (communityId: {}, species: {}, search: {})", communityId, species, search);
        List<PetDto> pets = petRegistryService.getPets(communityId, species, search);
        return ResponseEntity.ok(pets);
    }

    /**
     * GET /pets/{id}
     * Get pet details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PetDto> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petRegistryService.getPetById(id));
    }

    /**
     * POST /pets
     * Register a pet
     */
    @PostMapping
    public ResponseEntity<PetDto> registerPet(@Valid @RequestBody PetRegistrationRequest request) {
        log.info("Incoming pet registration request for '{}'", request.getName());
        PetDto created = petRegistryService.registerPet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /pets/my
     * Get user's registered pets
     */
    @GetMapping("/my")
    public ResponseEntity<List<PetDto>> getMyPets(@RequestParam(defaultValue = "1") Long ownerId) {
        return ResponseEntity.ok(petRegistryService.getMyPets(ownerId));
    }

    /**
     * GET /pets/services
     * Pet service providers (vets, walkers, groomers, trainers, sitters)
     */
    @GetMapping("/services")
    public ResponseEntity<List<PetServiceProviderDto>> getPetServices(
            @RequestParam(required = false) String type) {
        log.info("Fetching pet services (type: {})", type);
        List<PetServiceProviderDto> services = petRegistryService.getPetServices(type);
        return ResponseEntity.ok(services);
    }

    /**
     * GET /pets/services/{id}
     * Get specific pet service provider
     */
    @GetMapping("/services/{id}")
    public ResponseEntity<PetServiceProviderDto> getPetServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(petRegistryService.getPetServiceById(id));
    }

    /**
     * POST /pets/services/{id}/book
     * Book a pet service
     */
    @PostMapping("/services/{id}/book")
    public ResponseEntity<PetBookingDto> bookPetService(
            @PathVariable Long id,
            @Valid @RequestBody PetBookingRequest request) {
        log.info("Booking pet service with provider ID: {} for pet ID: {}", id, request.getPetId());
        PetBookingDto booking = petRegistryService.bookPetService(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    /**
     * GET /pets/bookings/my
     * Get resident's pet bookings
     */
    @GetMapping("/bookings/my")
    public ResponseEntity<List<PetBookingDto>> getMyBookings(@RequestParam(defaultValue = "1") Long userId) {
        return ResponseEntity.ok(petRegistryService.getMyBookings(userId));
    }
}
