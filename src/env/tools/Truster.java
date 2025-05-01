package tools;

import cartago.*;
import java.util.*;

public class Truster extends Artifact {

    @OPERATION
    public void getHighestAverageRatingAgent_IT(Object[] ITList, OpFeedbackParam<Object> mostTrustworthyAgent) {
        var ratings = calculateAverageRatings(ITList);
        mostTrustworthyAgent.set(findHighestRatedAgent(ratings));
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