package com.agung.restful.service;

import com.agung.restful.entity.Address;
import com.agung.restful.entity.Contact;
import com.agung.restful.entity.User;
import com.agung.restful.model.AddressResponse;
import com.agung.restful.model.ContactResponse;
import com.agung.restful.model.CreateAddressRequest;
import com.agung.restful.model.UpdateAddressRequest;
import com.agung.restful.repository.AddressRepository;
import com.agung.restful.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public AddressResponse create(User user,CreateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact Not Found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressid){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact Not Found"));

        Address addressResponse = addressRepository.findFirstByContactAndId(contact,addressid)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Address Not Found"));

        return toAddressResponse(addressResponse);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact Not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Address Not Found"));

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);
        return toAddressResponse(address);
    }

    @Transactional
    public void remove(User user,String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact Not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Address Not Found"));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user,String contactId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact Not Found"));

        List<Address> addresses = addressRepository.findAllByContact(contact);
        return addresses.stream().map(this::toAddressResponse).toList();
    }
}
