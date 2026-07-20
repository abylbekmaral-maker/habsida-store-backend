package com.project.service;

import com.project.dto.AddressRequestDto;
import com.project.dto.AddressResponseDto;
import com.project.dto.CustomerRequestDto;
import com.project.dto.CustomerResponseDto;
import com.project.entity.Customer;
import com.project.entity.CustomerAddress;
import com.project.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setPhone(dto.phone());
        if (dto.status() != null) {
            customer.setStatus(dto.status());
        }
        if (dto.addresses() != null) {
            for (AddressRequestDto addressDto : dto.addresses()) {
                CustomerAddress address = new CustomerAddress();
                address.setAddressLine(addressDto.addressLine());
                address.setDefault(addressDto.isDefault());
                customer.addAddress(address);
            }
        }
        return toResponseDto(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(UUID customerId) {
        return toResponseDto(getValidCustomer(customerId));
    }

    @Transactional
    public CustomerResponseDto updateCustomer(UUID customerId, CustomerRequestDto dto) {
        Customer customer = getValidCustomer(customerId);

        customer.setName(dto.name());
        customer.setPhone(dto.phone());
        if(dto.status() != null) {
            customer.setStatus(dto.status());
        }
        customer.getAddresses().clear();
        if (dto.addresses() != null) {
            for (AddressRequestDto addressDto : dto.addresses()) {
                CustomerAddress address = new CustomerAddress();
                address.setAddressLine(addressDto.addressLine());
                address.setDefault(addressDto.isDefault());
                customer.addAddress(address);
            }
        }
        return toResponseDto(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(UUID customerId) {
        Customer customer = getValidCustomer(customerId);
        customerRepository.delete(customer);
    }

    private Customer getValidCustomer(UUID customerId) {
        return  customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }
    private CustomerResponseDto toResponseDto(Customer customer) {
        List<AddressResponseDto> addressDtos = customer.getAddresses().stream()
                .map(address -> new AddressResponseDto(address.getId(), address.getAddressLine(), address.isDefault()))
                .toList();

        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getStatus(),
                addressDtos
        );
    }
}
