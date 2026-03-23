// BE: cap2/schema/UserBehaviorEvent.java
package cap2.schema;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user_behavior_events")
public class UserBehaviorEvent {
    @Id
    private String id;

    private String userId;
    private String productId;
    private String designRequestId;  // gợi ý nào dẫn đến hành động này

    private EventType eventType;     // PRODUCT_VIEW, ADD_TO_CART, PURCHASE, RATING

    private Integer rating;          // 1-5, chỉ khi eventType = RATING
    private Double rankingScore;     // ranking_score lúc sản phẩm được gợi ý

    @CreatedDate
    private Instant createdAt;

    public enum EventType {
        PRODUCT_VIEW,    // implicit score = 1
        ADD_TO_CART,     // implicit score = 3
        PURCHASE,        // implicit score = 5
        RATING           // explicit score = rating value (1-5)
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
