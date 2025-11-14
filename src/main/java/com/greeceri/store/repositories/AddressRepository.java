package com.greeceri.store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.Address;
import com.greeceri.store.models.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findByUser(User user);
    Optional<Address> findByUserAndIsMainAddress(User user, boolean isMainAddress);

}
