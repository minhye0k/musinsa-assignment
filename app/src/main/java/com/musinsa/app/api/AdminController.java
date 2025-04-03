package com.musinsa.app.api;

import com.musinsa.app.api.facade.AdminFacade;
import com.musinsa.app.api.request.ManageProductRequest;
import com.musinsa.app.api.response.ManageProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {
    private final AdminFacade adminFacade;

    @PostMapping
    public ResponseEntity<ManageProductResponse> manageProduct(@RequestBody ManageProductRequest manageProductRequest) {
        manageProductRequest.validate();
        ManageProductResponse manageProductResponse = adminFacade.manageProduct(manageProductRequest);
        return ResponseEntity.ok(manageProductResponse);
    }


}
