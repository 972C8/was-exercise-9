package tools;

import cartago.*;
import java.util.*;

public class Truster extends Artifact {

    //Task 1
    @OPERATION
    public void getHighestAverageRatingAgent_IT(Object[] ITList, OpFeedbackParam<Object> mostTrustworthyAgent) {
        var ratings = calculateAverageRatings(ITList);
        mostTrustworthyAgent.set(findHighestRatedAgent(ratings));
    }

    //Task 3
    @OPERATION
    public void getHighestAverageRatingAgent_IT_CR(Object[] ITList, Object[] CRList,
            OpFeedbackParam<String> mostTrustworthyAgent) {
        var itRatings = calculateAverageRatings(ITList);
        var crRatings = calculateAverageRatings(CRList);

        var combinedRatings = new HashMap<String, Double>();
        itRatings.forEach((agent, itRating) -> {
            double crRating = crRatings.getOrDefault(agent, 0.0);
            combinedRatings.put(agent, 0.5 * itRating + 0.5 * crRating);
        });

        mostTrustworthyAgent.set(findHighestRatedAgent(combinedRatings));
    }

    //Task 4
    @OPERATION
    public void getHighestAverageRatingAgent_IT_CR_WR(Object[] ITList, Object[] CRList, Object[] WRList,
            OpFeedbackParam<String> mostTrustworthyAgent) {
        var itRatings = calculateAverageRatings(ITList);
        var crRatings = calculateAverageRatings(CRList);
        var wrRatings = calculateAverageRatings(WRList);

        var combinedRatings = new HashMap<String, Double>();
        itRatings.forEach((agent, itRating) -> {
            double crRating = crRatings.getOrDefault(agent, 0.0);
            double wrRating = wrRatings.getOrDefault(agent, 0.0);
            combinedRatings.put(agent, (itRating + crRating + wrRating) / 3.0);
        });

        mostTrustworthyAgent.set(findHighestRatedAgent(combinedRatings));
    }

    @OPERATION
    public void getTempReadingByAgent(String agentName, Object[] tempReadings, OpFeedbackParam<Double> tempReading) {
        for (Object entry : tempReadings) {
            Object[] reading = (Object[]) entry;
            String agent = (String) reading[1];
            if (agent.equals(agentName)) {
                tempReading.set(((Number) reading[0]).doubleValue());
                return;
            }
        }
    }

    private Map<String, Double> calculateAverageRatings(Object[] ratingsList) {
        var ratingsMap = new HashMap<String, List<Double>>();

        for (Object entry : ratingsList) {
            Object[] rating = (Object[]) entry;
            String targetAgent = (String) rating[1];
            double ratingValue = ((Number) rating[3]).doubleValue();

            ratingsMap.computeIfAbsent(targetAgent, k -> new ArrayList<>())
                    .add(ratingValue);
        }

        var averages = new HashMap<String, Double>();
        ratingsMap.forEach((agent, ratings) -> {
            double avg = ratings.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            averages.put(agent, avg);
        });

        return averages;
    }

    private String findHighestRatedAgent(Map<String, Double> ratings) {
        return ratings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }
}