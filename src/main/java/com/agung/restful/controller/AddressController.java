package com.agung.restful.controller;

import com.agung.restful.entity.User;
import com.agung.restful.model.response.AddressResponse;
import com.agung.restful.model.request.CreateAddressRequest;
import com.agung.restful.model.request.UpdateAddressRequest;
import com.agung.restful.model.response.WebResponse;
import com.agung.restful.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "/api/contacts/{idContact}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(User user,
                                               @RequestBody CreateAddressRequest request,
                                               @PathVariable("idContact") String contactId) {
        request.setContactId(contactId);

        AddressResponse addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .status(true)
                .build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(User user,
                                            @PathVariable("contactId") String contactId,
                                            @PathVariable("addressId") String addressId) {
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .status(true)
                .build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user,
                                               @RequestBody UpdateAddressRequest request,
                                               @PathVariable("contactId") String contactId,
                                               @PathVariable("addressId") String addressId) {
        request.setContactId(contactId);
        request.setAddressId(addressId);

        AddressResponse addressResponse = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .status(true)
                .build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@AuthenticationPrincipal User user,
                                      @PathVariable("contactId") String contactId,
                                      @PathVariable("addressId") String addressId) {

        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder()
                .data("OK")
                .status(true)
                .build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(@AuthenticationPrincipal User user,
                                            @PathVariable("contactId") String contactId) {
        List<AddressResponse> addressResponse = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder()
                .data(addressResponse)
                .status(true)
                .build();
    }

}
