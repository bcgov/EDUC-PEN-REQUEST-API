package ca.bc.gov.educ.api.penrequest.service.v1;

import ca.bc.gov.educ.api.penrequest.constants.PenRequestStatusCode;
import ca.bc.gov.educ.api.penrequest.constants.StatsType;
import ca.bc.gov.educ.api.penrequest.repository.PenRequestRepository;
import ca.bc.gov.educ.api.penrequest.struct.v1.PenRequestStats;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PenRequestStatsService {
  private final PenRequestRepository penRequestRepository;

  public PenRequestStatsService(PenRequestRepository penRequestRepository) {
    this.penRequestRepository = penRequestRepository;
  }

  @Cacheable(value = "gmpStats")
  public PenRequestStats getStats(final StatsType statsType) {
    Pair<Long, Double> currentMonthResultAndPercentile;
    val baseDateTime = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    val baseWeekStart = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
    val baseWeekEnd = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(23).withMinute(59).withSecond(59).withNano(0);
    switch (statsType) {
      case COMPLETIONS_LAST_WEEK:
        return this.getPenRequestsCompletedLastWeek();
      case AVERAGE_COMPLETION_TIME:
        return this.getAverageGMPCompletionTime();
      case COMPLETIONS_LAST_12_MONTH:
        return this.getPenRequestsCompletedLastYear();
      case PERCENT_GMP_REJECTED_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentGMPBasedOnStatus(PenRequestStatusCode.REJECTED.toString());
        return PenRequestStats.builder().gmpRejectedInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentRejectedGmpToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case PERCENT_GMP_ABANDONED_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentGMPBasedOnStatus(PenRequestStatusCode.ABANDONED.toString());
        return PenRequestStats.builder().gmpAbandonedInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentAbandonedGmpToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case PERCENT_GMP_COMPLETED_WITH_DOCUMENTS_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentGMPWithDocsBasedOnStatus(PenRequestStatusCode.MANUAL.toString(), PenRequestStatusCode.AUTO.toString());
        return PenRequestStats.builder().gmpCompletedWithDocsInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentGmpCompletedWithDocumentsToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case PERCENT_GMP_COMPLETION_TO_LAST_MONTH:
        currentMonthResultAndPercentile = this.getMonthlyPercentGMPBasedOnStatus(PenRequestStatusCode.MANUAL.toString(), PenRequestStatusCode.AUTO.toString());
        return PenRequestStats.builder().gmpCompletedInCurrentMonth(currentMonthResultAndPercentile.getLeft()).percentCompletedGmpToLastMonth(currentMonthResultAndPercentile.getRight()).build();
      case ALL_STATUSES_LAST_12_MONTH:
        return PenRequestStats.builder().allStatsLastTwelveMonth(this.getAllStatusesBetweenDates(baseDateTime.minusMonths(12), baseDateTime.minusDays(1))).build();
      case ALL_STATUSES_LAST_6_MONTH:
        return PenRequestStats.builder().allStatsLastSixMonth(this.getAllStatusesBetweenDates(baseDateTime.minusMonths(6), baseDateTime.minusDays(1))).build();
      case ALL_STATUSES_LAST_1_MONTH:
        return PenRequestStats.builder().allStatsLastOneMonth(this.getAllStatusesBetweenDates(baseDateTime.minusMonths(1), baseDateTime.minusDays(1))).build();
      case ALL_STATUSES_LAST_1_WEEK:
        return PenRequestStats.builder().allStatsLastOneWeek(this.getAllStatusesBetweenDates(baseWeekStart.minusWeeks(1), baseWeekEnd.minusWeeks(1).plusDays(6))).build();
      default:
        break;
    }
    return new PenRequestStats();
  }

  private Pair<Long, Double> getMonthlyPercentGMPBasedOnStatus(final String... statusCode) {
    val dayOfMonth = LocalDateTime.now().getDayOfMonth();

    val startDatePreviousMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1).minusMonths(1);
    val endDatePreviousMonthLength = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).minusMonths(1).getMonth().length(LocalDate.now().minusMonths(1).isLeapYear());
    val endDatePreviousMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(Math.min(dayOfMonth, endDatePreviousMonthLength)).minusMonths(1);
    val startDateCurrentMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);
    val endDateCurrentMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(dayOfMonth);

    val previousMonthResult = this.penRequestRepository.countByPenRequestStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDatePreviousMonth, endDatePreviousMonth);

    val currentMonthResult = this.penRequestRepository.countByPenRequestStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDateCurrentMonth, endDateCurrentMonth);
    return Pair.of(currentMonthResult, findPercentage(previousMonthResult, currentMonthResult));
  }

  private Pair<Long, Double> getMonthlyPercentGMPWithDocsBasedOnStatus(final String... statusCode) {
    val dayOfMonth = LocalDateTime.now().getDayOfMonth();

    val startDatePreviousMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1).minusMonths(1);
    val endDatePreviousMonthLength = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).minusMonths(1).getMonth().length(LocalDate.now().minusMonths(1).isLeapYear());
    val endDatePreviousMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(Math.min(dayOfMonth, endDatePreviousMonthLength)).minusMonths(1);
    val startDateCurrentMonth = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).withDayOfMonth(1);
    val endDateCurrentMonth = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0).withDayOfMonth(dayOfMonth);
    val previousMonthResult = this.penRequestRepository.findNumberOfPenRequestsWithDocumentsStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDatePreviousMonth, endDatePreviousMonth);


    val currentMonthResult = this.penRequestRepository.findNumberOfPenRequestsWithDocumentsStatusCodeInAndStatusUpdateDateBetween(List.of(statusCode), startDateCurrentMonth, endDateCurrentMonth);

    return Pair.of(currentMonthResult, findPercentage(previousMonthResult, currentMonthResult));
  }

  private double findPercentage(long previousMonthResult, long currentMonthResult) {
    final double percentVal;
    if (previousMonthResult == 0 && currentMonthResult != 0) {
      percentVal = currentMonthResult;
    } else if (currentMonthResult == 0 && previousMonthResult != 0) {
      percentVal = -previousMonthResult;
    } else if (currentMonthResult == 0) {
      percentVal = 0.0;
    } else {
      double increase = (double) (currentMonthResult - previousMonthResult) / previousMonthResult;
      percentVal = increase * 100;
    }
    return percentVal;
  }

  private Map<String, Long> getAllStatusesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
    Map<String, Long> allStatusMap = new LinkedHashMap<>();
    for (val status : PenRequestStatusCode.values()) {
      val results = this.penRequestRepository.countByPenRequestStatusCodeInAndStatusUpdateDateBetween(List.of(status.toString()), startDate, endDate);
      allStatusMap.put(status.toString(), results);
    }
    return allStatusMap;
  }

  private PenRequestStats getPenRequestsCompletedLastYear() {
    LocalDate currentDate = LocalDate.now();
    LocalDate fromDate = currentDate.minusMonths(13);
    LocalDate toDate = currentDate.minusMonths(1);
    val gmpStats = this.penRequestRepository.findStatusAndStatusUpdateDatesBetweenForStatuses(fromDate, toDate, Arrays.asList("MANUAL", "AUTO"));
    Map<String, Integer> penReqCompletionsInLast12Months = new HashMap<>();
    for (val gmpStat : gmpStats) {
      val month = gmpStat.getStatusUpdateDate().getMonth().toString();
      if (penReqCompletionsInLast12Months.containsKey(month)) {
        val currentCount = penReqCompletionsInLast12Months.get(month);
        penReqCompletionsInLast12Months.put(month, currentCount + 1);
      } else {
        penReqCompletionsInLast12Months.put(month, 1);
      }
    }
    Arrays.stream(Month.values()).forEach(el -> {
      if (!penReqCompletionsInLast12Months.containsKey(el.toString())) {
        penReqCompletionsInLast12Months.put(el.toString(), 0);
      }
    });
    val sortedKeys = new ArrayList<>(penReqCompletionsInLast12Months.keySet()).stream().sorted(this::monthComparator).collect(Collectors.toList());
    Map<String, Integer> sortedMap = createSortedMap(penReqCompletionsInLast12Months, sortedKeys);
    return PenRequestStats.builder().completionsInLastTwelveMonth(sortedMap).build();
  }

  private Map<String, Integer> createSortedMap(Map<String, Integer> unsortedMap, List<String> sortedKeys) {
    Map<String, Integer> sortedMap = new LinkedHashMap<>();
    for (val key : sortedKeys) {
      sortedMap.put(key, unsortedMap.get(key));
    }
    return sortedMap;
  }

  private int monthComparator(String month1, String month2) {
    return Month.valueOf(month1).getValue() - Month.valueOf(month2).getValue();
  }

  private int dayComparator(String day1, String day2) {
    return DayOfWeek.valueOf(day1).getValue() - DayOfWeek.valueOf(day2).getValue();
  }

  private PenRequestStats getAverageGMPCompletionTime() {
    val gmpStat = this.penRequestRepository.findCompletionProcessAverageTime();
    return PenRequestStats.builder().averageTimeToCompleteRequest(gmpStat.getAverageCompletionTime()).build();
  }

  private PenRequestStats getPenRequestsCompletedLastWeek() {
    LocalDate currentDate = LocalDate.now();
    LocalDate fromDate = currentDate.minusDays(8);
    LocalDate toDate = currentDate.minusDays(1);
    val gmpStats = this.penRequestRepository.findStatusAndStatusUpdateDatesBetweenForStatuses(fromDate, toDate, Arrays.asList("MANUAL", "AUTO"));
    Map<String, Integer> penReqCompletionsInLastWeek = new HashMap<>();
    for (val gmpStat : gmpStats) {
      val day = gmpStat.getStatusUpdateDate().getDayOfWeek().toString();
      if (penReqCompletionsInLastWeek.containsKey(day)) {
        val currentCount = penReqCompletionsInLastWeek.get(day);
        penReqCompletionsInLastWeek.put(day, currentCount + 1);
      } else {
        penReqCompletionsInLastWeek.put(day, 1);
      }
    }
    Arrays.stream(DayOfWeek.values()).forEach(el -> {
      if (!penReqCompletionsInLastWeek.containsKey(el.toString())) {
        penReqCompletionsInLastWeek.put(el.toString(), 0);
      }
    });
    val sortedKeys = new ArrayList<>(penReqCompletionsInLastWeek.keySet()).stream().sorted(this::dayComparator).collect(Collectors.toList());
    Map<String, Integer> sortedMap = createSortedMap(penReqCompletionsInLastWeek, sortedKeys);
    return PenRequestStats.builder().completionsInLastWeek(sortedMap).build();
  }

  @Scheduled(cron = "0 0 0 * * *") // midnight
  @CacheEvict(value = "gmpStats", allEntries = true)
  public void clearCache() {
    // Empty method, spring boot does the magic.
  }

}
