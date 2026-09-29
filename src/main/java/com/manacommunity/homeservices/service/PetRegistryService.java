package com.manacommunity.homeservices.service;

import com.manacommunity.homeservices.dto.*;

import java.util.List;

public interface PetRegistryService {
    List<PetDto> getPets(Long communityId, String species, String search);
    PetDto getPetById(Long id);
    PetDto registerPet(PetRegistrationRequest request);
    List<PetDto> getMyPets(Long ownerId);

    List<PetServiceProviderDto> getPetServices(String serviceType);
    PetServiceProviderDto getPetServiceById(Long id);
    PetBookingDto bookPetService(Long providerId, PetBookingRequest request);
    List<PetBookingDto> getMyBookings(Long userId);
}
