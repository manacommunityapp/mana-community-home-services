package com.manacommunity.homeservices.service.impl;

import com.manacommunity.homeservices.dto.*;
import com.manacommunity.homeservices.exception.HomeServiceNotFoundException;
import com.manacommunity.homeservices.model.entity.PetBookingEntity;
import com.manacommunity.homeservices.model.entity.PetEntity;
import com.manacommunity.homeservices.model.entity.PetServiceProviderEntity;
import com.manacommunity.homeservices.repository.PetBookingRepository;
import com.manacommunity.homeservices.repository.PetRepository;
import com.manacommunity.homeservices.repository.PetServiceProviderRepository;
import com.manacommunity.homeservices.service.PetRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetRegistryServiceImpl implements PetRegistryService {

    private final PetRepository petRepository;
    private final PetServiceProviderRepository providerRepository;
    private final PetBookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PetDto> getPets(Long communityId, String species, String search) {
        List<PetEntity> pets = petRepository.searchPets(
                (species != null && !species.isBlank()) ? species : null,
                (search != null && !search.isBlank()) ? search : null
        );
        return pets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PetDto getPetById(Long id) {
        PetEntity pet = petRepository.findById(id)
                .orElseThrow(() -> new HomeServiceNotFoundException("Pet not found with ID: " + id));
        return mapToDto(pet);
    }

    @Override
    @Transactional
    public PetDto registerPet(PetRegistrationRequest req) {
        log.info("Registering new pet '{}' for owner '{}'", req.getName(), req.getOwnerName());
        PetEntity pet = PetEntity.builder()
                .name(req.getName())
                .species(req.getSpecies())
                .breed(req.getBreed())
                .ageYears(req.getAgeYears())
                .gender(req.getGender())
                .color(req.getColor())
                .photoUrl(req.getPhotoUrl())
                .ownerId(req.getOwnerId() != null ? req.getOwnerId() : 1L)
                .ownerName(req.getOwnerName() != null ? req.getOwnerName() : "Community Resident")
                .ownerPhone(req.getOwnerPhone())
                .communityId(req.getCommunityId() != null ? req.getCommunityId() : 1L)
                .tower(req.getTower() != null ? req.getTower() : "Tower A")
                .flatNumber(req.getFlatNumber() != null ? req.getFlatNumber() : "101")
                .isVaccinated(req.getIsVaccinated() != null ? req.getIsVaccinated() : true)
                .lastVaccinationDate(req.getLastVaccinationDate())
                .vaccinationCertificateUrl(req.getVaccinationCertificateUrl())
                .microchipNumber(req.getMicrochipNumber())
                .isFriendlyWithKids(req.getIsFriendlyWithKids() != null ? req.getIsFriendlyWithKids() : true)
                .isFriendlyWithPets(req.getIsFriendlyWithPets() != null ? req.getIsFriendlyWithPets() : true)
                .specialNotes(req.getSpecialNotes())
                .vetName(req.getVetName())
                .vetContact(req.getVetContact())
                .status("ACTIVE")
                .build();

        PetEntity saved = petRepository.save(pet);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetDto> getMyPets(Long ownerId) {
        return petRepository.findByOwnerIdOrderByIdDesc(ownerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetServiceProviderDto> getPetServices(String serviceType) {
        List<PetServiceProviderEntity> list;
        if (serviceType != null && !serviceType.isBlank()) {
            list = providerRepository.findByServiceTypeIgnoreCase(serviceType);
        } else {
            list = providerRepository.findAll();
        }
        return list.stream().map(this::mapToProviderDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PetServiceProviderDto getPetServiceById(Long id) {
        PetServiceProviderEntity p = providerRepository.findById(id)
                .orElseThrow(() -> new HomeServiceNotFoundException("Pet service provider not found: " + id));
        return mapToProviderDto(p);
    }

    @Override
    @Transactional
    public PetBookingDto bookPetService(Long providerId, PetBookingRequest req) {
        PetServiceProviderEntity provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new HomeServiceNotFoundException("Pet service provider not found: " + providerId));

        PetEntity pet = petRepository.findById(req.getPetId())
                .orElseThrow(() -> new HomeServiceNotFoundException("Pet not found with ID: " + req.getPetId()));

        PetBookingEntity booking = PetBookingEntity.builder()
                .providerId(provider.getId())
                .providerName(provider.getName())
                .serviceType(provider.getServiceType())
                .petId(pet.getId())
                .petName(pet.getName())
                .userId(req.getUserId() != null ? req.getUserId() : pet.getOwnerId())
                .userName(req.getUserName() != null ? req.getUserName() : pet.getOwnerName())
                .userPhone(req.getUserPhone() != null ? req.getUserPhone() : pet.getOwnerPhone())
                .tower(req.getTower() != null ? req.getTower() : pet.getTower())
                .flatNumber(req.getFlatNumber() != null ? req.getFlatNumber() : pet.getFlatNumber())
                .bookingDate(req.getBookingDate())
                .timeSlot(req.getTimeSlot())
                .notes(req.getNotes())
                .status("CONFIRMED")
                .amount(provider.getBasePrice() != null ? provider.getBasePrice() : 500.0)
                .build();

        PetBookingEntity saved = bookingRepository.save(booking);
        return mapToBookingDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetBookingDto> getMyBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId).stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private PetDto mapToDto(PetEntity p) {
        return PetDto.builder()
                .id(p.getId())
                .name(p.getName())
                .species(p.getSpecies())
                .breed(p.getBreed())
                .ageYears(p.getAgeYears())
                .gender(p.getGender())
                .color(p.getColor())
                .photoUrl(p.getPhotoUrl())
                .ownerId(p.getOwnerId())
                .ownerName(p.getOwnerName())
                .ownerPhone(p.getOwnerPhone())
                .communityId(p.getCommunityId())
                .tower(p.getTower())
                .flatNumber(p.getFlatNumber())
                .isVaccinated(p.getIsVaccinated())
                .lastVaccinationDate(p.getLastVaccinationDate())
                .vaccinationCertificateUrl(p.getVaccinationCertificateUrl())
                .microchipNumber(p.getMicrochipNumber())
                .isFriendlyWithKids(p.getIsFriendlyWithKids())
                .isFriendlyWithPets(p.getIsFriendlyWithPets())
                .specialNotes(p.getSpecialNotes())
                .vetName(p.getVetName())
                .vetContact(p.getVetContact())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private PetServiceProviderDto mapToProviderDto(PetServiceProviderEntity p) {
        return PetServiceProviderDto.builder()
                .id(p.getId())
                .name(p.getName())
                .serviceType(p.getServiceType())
                .description(p.getDescription())
                .contactNumber(p.getContactNumber())
                .email(p.getEmail())
                .clinicOrAddress(p.getClinicOrAddress())
                .priceRange(p.getPriceRange())
                .basePrice(p.getBasePrice())
                .rating(p.getRating())
                .reviewCount(p.getReviewCount())
                .photoUrl(p.getPhotoUrl())
                .isVerified(p.getIsVerified())
                .isAvailable(p.getIsAvailable())
                .communityId(p.getCommunityId())
                .availableHours(p.getAvailableHours())
                .build();
    }

    private PetBookingDto mapToBookingDto(PetBookingEntity b) {
        return PetBookingDto.builder()
                .id(b.getId())
                .providerId(b.getProviderId())
                .providerName(b.getProviderName())
                .serviceType(b.getServiceType())
                .petId(b.getPetId())
                .petName(b.getPetName())
                .userId(b.getUserId())
                .userName(b.getUserName())
                .userPhone(b.getUserPhone())
                .tower(b.getTower())
                .flatNumber(b.getFlatNumber())
                .bookingDate(b.getBookingDate())
                .timeSlot(b.getTimeSlot())
                .notes(b.getNotes())
                .status(b.getStatus())
                .amount(b.getAmount())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
