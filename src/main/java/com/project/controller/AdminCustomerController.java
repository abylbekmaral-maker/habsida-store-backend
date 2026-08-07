package com.project.controller;

import com.project.dto.CustomerRequestDto;
import com.project.dto.CustomerResponseDto;
import com.project.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Customers",
        description = "Customer management"
)
@RestController
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Create customer",
            description = "Creates a customer. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "409", description = "Customer already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDto createCustomer(@Valid @RequestBody CustomerRequestDto requestDto) {
        return customerService.createCustomer(requestDto);
    }

    @Operation(
            summary = "Get customers",
            description = "Gets all customers. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customers found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<CustomerResponseDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(
            summary = "Get customer",
            description = "Gets a customer. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public CustomerResponseDto getCustomerById(@PathVariable UUID id) {
        return customerService.getCustomerById(id);
    }

    @Operation(
            summary = "Update customer",
            description = "Updates a customer. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Customer already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public CustomerResponseDto updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRequestDto requestDto) {
        return customerService.updateCustomer(id, requestDto);
    }

    @Operation(
            summary = "Delete customer",
            description = "Deletes a customer. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
    }
}
