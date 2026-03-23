package cap2.dto.request;

import cap2.schema.UserBehaviorEvent;

public class BehaviorEventRequest {
    private String productId;
    private String designRequestId;
    private UserBehaviorEvent.EventType eventType;
    private Integer rating;
    private Double rankingScore;

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDesignRequestId() {
        return designRequestId;
    }

    public void setDesignRequestId(String designRequestId) {
        this.designRequestId = designRequestId;
    }

    public UserBehaviorEvent.EventType getEventType() {
        return eventType;
    }

    public void setEventType(UserBehaviorEvent.EventType eventType) {
        this.eventType = eventType;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Double getRankingScore() {
        return rankingScore;
    }

    public void setRankingScore(Double rankingScore) {
        this.rankingScore = rankingScore;
    }
}
