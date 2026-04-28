package cap2.service;

import cap2.dto.request.BehaviorEventRequest;
import cap2.repository.BehaviorEventRepository;
import cap2.schema.UserBehaviorEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BehaviorService {

    BehaviorEventRepository behaviorEventRepository;
    MongoTemplate mongoTemplate;

    public void saveEvent(BehaviorEventRequest request, String userId) {
        saveEvent(
                userId,
                request.getProductId(),
                request.getEventType(),
                request.getRating(),
                request.getRankingScore(),
                request.getDesignRequestId()
        );
    }

    public void saveEvent(String userId,
                          String productId,
                          UserBehaviorEvent.EventType eventType,
                          Integer rating,
                          Double rankingScore,
                          String designRequestId) {
        UserBehaviorEvent event = new UserBehaviorEvent();
        event.setUserId(userId);
        event.setProductId(productId);
        event.setDesignRequestId(designRequestId);
        event.setEventType(eventType);
        event.setRating(rating);
        event.setRankingScore(rankingScore);

        behaviorEventRepository.save(event);

        if (eventType == UserBehaviorEvent.EventType.PURCHASE) {
            log.info("Behavior tracked PURCHASE. userId={}, productId={}", userId, productId);
        }
    }

    public void saveEventSafely(String userId,
                                String productId,
                                UserBehaviorEvent.EventType eventType,
                                Integer rating,
                                Double rankingScore,
                                String designRequestId) {
        try {
            saveEvent(userId, productId, eventType, rating, rankingScore, designRequestId);
        } catch (DuplicateKeyException ex) {
            // Some environments may have a unique index on (userId, productId).
            // Fallback to update existing event so PURCHASE is not dropped.
            log.warn("Duplicate behavior key detected, fallback to upsert. userId={}, productId={}, eventType={}",
                    userId, productId, eventType);
            upsertByUserAndProduct(userId, productId, eventType, rating, rankingScore, designRequestId);
        } catch (Exception ex) {
            // Do not block core flows (cart/order/review) if behavior tracking fails.
            log.warn("Skip behavior tracking. userId={}, productId={}, eventType={}, reason={}",
                    userId, productId, eventType, ex.getMessage());
        }
    }

    private void upsertByUserAndProduct(String userId,
                                        String productId,
                                        UserBehaviorEvent.EventType eventType,
                                        Integer rating,
                                        Double rankingScore,
                                        String designRequestId) {
        try {
            Query query = new Query(Criteria.where("userId").is(userId)
                    .and("productId").is(productId));

            Update update = new Update()
                    .set("eventType", eventType)
                    .set("rating", rating)
                    .set("rankingScore", rankingScore)
                    .set("designRequestId", designRequestId)
                    .set("createdAt", Instant.now())
                    .set("userId", userId)
                    .set("productId", productId);

            mongoTemplate.upsert(query, update, UserBehaviorEvent.class);

            if (eventType == UserBehaviorEvent.EventType.PURCHASE) {
                log.info("Behavior upserted PURCHASE. userId={}, productId={}", userId, productId);
            }
        } catch (Exception upsertEx) {
            log.warn("Skip behavior tracking after duplicate-key fallback. userId={}, productId={}, eventType={}, reason={}",
                    userId, productId, eventType, upsertEx.getMessage());
        }
    }
}
