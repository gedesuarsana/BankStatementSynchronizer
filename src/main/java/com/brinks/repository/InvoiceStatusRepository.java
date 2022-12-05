package com.brinks.repository;

import com.brinks.models.InvoiceStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceStatusRepository extends CrudRepository<InvoiceStatus,Integer> {

}
