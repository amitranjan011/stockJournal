package com.amit.journal.domain.repo;

import com.amit.journal.model.TransactionSummary;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsSummaryDAO {

    TransactionSummary findBySymbolAnsOpen(String symbol);

    List<TransactionSummary> getSummaryRecords(String symbol, LocalDate startDate, LocalDate endDate);
}