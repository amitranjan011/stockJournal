package com.amit.journal.service;

import com.amit.journal.constants.Constants;
import com.amit.journal.model.StockKPI;
import com.amit.journal.model.TransactionKPI;
import com.amit.journal.model.TransactionSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionKPIServiceImpl implements TransactionKPIService {

    private static final Logger LOG = LogManager.getLogger(TransactionKPIServiceImpl.class);
    @Override
    public TransactionKPI generateKPI(List<TransactionSummary> tramSummaryList) {
        /*Supplier<Stream<TransactionSummary>> summaryStream = tramSummaryList::stream;

        Comparator<TransactionSummary> comparator = Comparator.comparing(TransactionSummary::getPctReturn);
        TransactionSummary bestClosed = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED).max(comparator).orElse(null);
        TransactionSummary worstClosed = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED).min(comparator).orElse(null);

        List<TransactionSummary> winningClosed = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED && summary.getPctReturn() > 0).collect(Collectors.toList());
        List<TransactionSummary> losingClosed = summaryStream.get().filter(summary -> summary.getPctReturn() < 0).collect(Collectors.toList());



        double avgGainPctClosed = winningClosed.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgLossPctClosed = losingClosed.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgHoldDaysClosed = summaryStream.get().mapToInt(TransactionSummary::getDaysHeld).summaryStatistics().getAverage();
        double avgOverallClosedReturnPct = summaryStream.get().filter(summary -> summary.getPctReturn() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED)
                .mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();


        TransactionSummary bestOpen = summaryStream.get().filter(summary -> summary.getUnrealizedProfitPct() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED).max(comparator).orElse(null);
        TransactionSummary worstOpen = summaryStream.get().filter(summary -> summary.getUnrealizedProfitPct() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED).min(comparator).orElse(null);
        List<TransactionSummary> winningOpenList = summaryStream.get()
                .filter(summary -> summary.getUnrealizedProfitPct() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED && summary.getUnrealizedProfitPct() > 0)
                .collect(Collectors.toList());
        List<TransactionSummary> losingOPenList = summaryStream.get().filter(summary -> summary.getUnrealizedProfitPct() < 0).collect(Collectors.toList());

        double avgGainPctOpen = winningClosed.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgLossPctOpen = losingClosed.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgOverallOpenReturnPct = summaryStream.get().filter(summary -> summary.getUnrealizedProfitPct() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED)
                .mapToDouble(TransactionSummary::getUnrealizedProfitPct).summaryStatistics().getAverage();

        TransactionKPI transactionKPI = new TransactionKPI();
        transactionKPI.setAvgGainPctClosed(avgGainPctClosed);
        transactionKPI.setAvgLossPctClosed(avgLossPctClosed);
        transactionKPI.setBestClosedStock(bestClosed);
        transactionKPI.setWorstClosedStock(worstClosed);
        transactionKPI.setAvgHoldDaysClosed(avgHoldDaysClosed);
        transactionKPI.setAvgOverallGainPctClosed(avgOverallClosedReturnPct);

        transactionKPI.setBestOpenStock(bestOpen);
        transactionKPI.setWorstOpenStock(worstOpen);
        transactionKPI.setAvgGainPctOpen(avgGainPctOpen);
        transactionKPI.setAvgLossPctOpen(avgLossPctOpen);
        transactionKPI.setAvgOverallGainPctOpen(avgOverallOpenReturnPct);*/

        Supplier<Stream<TransactionSummary>> summaryStream = tramSummaryList::stream;
        TransactionKPI transactionKPI = new TransactionKPI();
        transactionKPI.setStockClosed(getClosedPositionKPI(summaryStream));
        transactionKPI.setStockOpen(getOpenPositionKPI(summaryStream));
        return transactionKPI;
    }

    private StockKPI getClosedPositionKPI(Supplier<Stream<TransactionSummary>> summaryStream) {
        Comparator<TransactionSummary> comparator = Comparator.comparing(TransactionSummary::getPctReturn);
        Predicate<TransactionSummary> retLessThanUpperLimit = summary -> summary.getPctReturn() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED;
        Predicate<TransactionSummary> retPctGreaterZero = summary -> summary.getPctReturn() > 0;
        Predicate<TransactionSummary> retPctLessZero = summary -> summary.getPctReturn() < 0;
        TransactionSummary bestClosed = summaryStream.get().filter(retLessThanUpperLimit).max(comparator).orElse(null);
        TransactionSummary worstClosed = summaryStream.get().filter(retLessThanUpperLimit).min(comparator).orElse(null);

        List<TransactionSummary> winningClosed = summaryStream.get().filter(retLessThanUpperLimit.and(retPctGreaterZero)).collect(Collectors.toList());
        List<TransactionSummary> losingClosed = summaryStream.get().filter(retPctLessZero).collect(Collectors.toList());

        double avgGainPctClosed = winningClosed.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgLossPctClosed = losingClosed.stream().mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();
        double avgHoldDaysClosed = summaryStream.get().mapToInt(TransactionSummary::getDaysHeld).summaryStatistics().getAverage();
        double avgOverallClosedReturnPct = summaryStream.get().filter(retLessThanUpperLimit)
                .mapToDouble(TransactionSummary::getPctReturn).summaryStatistics().getAverage();

        StockKPI closedStockKPI = new StockKPI();
        closedStockKPI.setBestStock(bestClosed);
        closedStockKPI.setWorstStock(worstClosed);
        closedStockKPI.setAvgGainingPct(avgGainPctClosed);
        closedStockKPI.setAvgLossingPct(avgLossPctClosed);
        closedStockKPI.setAvgOverallGainPct(avgOverallClosedReturnPct);
        closedStockKPI.setAvgHoldDaysStock(avgHoldDaysClosed);
        return closedStockKPI;

    }

    private StockKPI getOpenPositionKPI(Supplier<Stream<TransactionSummary>> summaryStream) {
        Comparator<TransactionSummary> comparator = Comparator.comparing(TransactionSummary::getUnrealizedProfitPct);
        Predicate<TransactionSummary> retLessThanUpperLimit = summary -> summary.getUnrealizedProfitPct() < Constants.RETURN_PERCENT_UNSOLD_UNREALISED;
        Predicate<TransactionSummary> retPctGreaterZero = summary -> summary.getUnrealizedProfitPct() > 0;
        Predicate<TransactionSummary> retPctLessZero = summary -> summary.getUnrealizedProfitPct() < 0;
        TransactionSummary bestOpen = summaryStream.get().filter(retLessThanUpperLimit).max(comparator).orElse(null);
        TransactionSummary worstOpen = summaryStream.get().filter(retLessThanUpperLimit).min(comparator).orElse(null);

        List<TransactionSummary> winningOpen = summaryStream.get().filter(retLessThanUpperLimit.and(retPctGreaterZero)).collect(Collectors.toList());
        List<TransactionSummary> losingOpen = summaryStream.get().filter(retPctLessZero).collect(Collectors.toList());

        double avgGainPctOpen = winningOpen.stream().mapToDouble(TransactionSummary::getUnrealizedProfitPct).summaryStatistics().getAverage();
        double avgLossPctOpen = losingOpen.stream().mapToDouble(TransactionSummary::getUnrealizedProfitPct).summaryStatistics().getAverage();
        double avgHoldDaysOpen = summaryStream.get().mapToInt(TransactionSummary::getDaysHeld).summaryStatistics().getAverage();
        double avgOverallOpenReturnPct = summaryStream.get().filter(retLessThanUpperLimit)
                .mapToDouble(TransactionSummary::getUnrealizedProfitPct).summaryStatistics().getAverage();


        StockKPI openStockKPI = new StockKPI();
        openStockKPI.setBestStock(bestOpen);
        openStockKPI.setWorstStock(worstOpen);
        openStockKPI.setAvgGainingPct(avgGainPctOpen);
        openStockKPI.setAvgLossingPct(avgLossPctOpen);
        openStockKPI.setAvgOverallGainPct(avgOverallOpenReturnPct);
        openStockKPI.setAvgHoldDaysStock(avgHoldDaysOpen);
        return openStockKPI;
    }
}
