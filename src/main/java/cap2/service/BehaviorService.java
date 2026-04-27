package cap2.service;

import cap2.dto.request.BehaviorEventRequest;
import cap2.repository.BehaviorEventRepository;
import cap2.schema.UserBehaviorEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BehaviorService {

    BehaviorEventRepository behaviorEventRepository;

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
    }

    public void saveEventSafely(String userId,
                                String productId,
                                UserBehaviorEvent.EventType eventType,
                                Integer rating,
                                Double rankingScore,
                                String designRequestId) {
        try {
            saveEvent(userId, productId, eventType, rating, rankingScore, designRequestId);
        } catch (Exception ex) {
            // Do not block core flows (cart/order/review) if behavior tracking fails.
            log.warn("Skip behavior tracking. userId={}, productId={}, eventType={}, reason={}",
                    userId, productId, eventType, ex.getMessage());
        }
    }
}
