package com.brinks.repository;

import com.brinks.models.InvoiceStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface InvoiceStatusRepository extends CrudRepository<InvoiceStatus,Integer> {

 @Query(value="select * from invoice_status where status =:status order by index_in_statement asc",nativeQuery=true)
 List<InvoiceStatus> findByStatusOrderByIndexInStatementDesc(@Param("status") String status);

 @Query(value="select * from invoice_status where invoice_name =:invoiceName and status =:status",nativeQuery=true)
 List<InvoiceStatus> findByStatusAndInvoiceName(@Param("status") String status,@Param("invoiceName")String invoiceName);


 @Query(value="select * from invoice_status where bank_statement_id =:bankStatementId and index_in_statement =:index",nativeQuery=true)
 InvoiceStatus findByBankStatementIdAndIndexInStatement(@Param("bankStatementId") BigInteger bankStatementId, @Param("index")Integer index);


 @Query(value="select * from invoice_status where invoice_name =:invoiceName",nativeQuery=true)
 List<InvoiceStatus> findByInvoiceName(@Param("invoiceName")String invoiceName);

}
