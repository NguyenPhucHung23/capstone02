package cap2.service;

import cap2.dto.request.BehaviorEventRequest;
import cap2.repository.BehaviorEventRepository;
import cap2.schema.UserBehaviorEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BehaviorService {

    @Autowired
    private BehaviorEventRepository behaviorEventRepository;

    public void saveEvent(BehaviorEventRequest request, String userId) {
        UserBehaviorEvent event = new UserBehaviorEvent();
        event.setUserId(userId);
        event.setProductId(request.getProductId());
        event.setDesignRequestId(request.getDesignRequestId());
        event.setEventType(request.getEventType());
        event.setRating(request.getRating());
        event.setRankingScore(request.getRankingScore());

        behaviorEventRepository.save(event);
    }
}
