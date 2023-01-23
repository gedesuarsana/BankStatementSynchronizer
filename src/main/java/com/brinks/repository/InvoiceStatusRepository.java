package com.brinks.repository;

import com.brinks.models.InvoiceStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceStatusRepository extends CrudRepository<InvoiceStatus,Integer> {

 List<InvoiceStatus> findByStatus(String status);

 @Query(value="select * from invoice_status where invoice_name :invoiceName",nativeQuery=true)
 List<InvoiceStatus> findByInvoiceName(@Param("invoiceName")String invoiceName);

}
