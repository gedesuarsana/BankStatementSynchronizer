package com.brinks.repository;

import com.brinks.models.BankStatement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface BankStatementRepository extends CrudRepository<BankStatement, BigInteger> {

}
