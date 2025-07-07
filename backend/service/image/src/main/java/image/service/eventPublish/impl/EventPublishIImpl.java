package image.service.eventPublish.impl;

import dto.image.response.ImageResponse;
import eurm.ResourceCategory;
import event.events.ImageUpdateEvent.BandRoomImageUpdateEvent;
import event.events.ImageUpdateEvent.StudioImageUpdateEvent;
import event.events.ImageUpdateEvent.UserProfileImageUpdateEvent;
import image.service.eventPublish.EventPublish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import outbox.publisher.OutboxEventPublisher;

import java.util.HashMap;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventPublishIImpl implements EventPublish {
    private final OutboxEventPublisher  outboxEventPublisher;


    @Override
    public void ImageUpdateSelector(List<ImageResponse> req) {
        ResourceCategory category = req.getFirst().getCategory();

        if(category.equals(ResourceCategory.PROFILE))
        {
            ProfileImageUpdate(req);
            return ;
        }

        if(category.equals(ResourceCategory.BAND_ROOM))
        {
            BandRoomImageUpdate(req);
            return;
        }

        if(category.equals(ResourceCategory.STUDIO))
        {
            StudioImageUpdate(req);
        }


    }


    private void ProfileImageUpdate(List<ImageResponse> req) {
        outboxEventPublisher.publish(
                UserProfileImageUpdateEvent.builder()
                        .userId(req.getFirst().getReferenceId())
                        .profileImageUrl(req.getFirst().getUrl())
                        .build()
        );


    }
    private void BandRoomImageUpdate(List<ImageResponse> req)
    {
        HashMap<Long,String> map = new HashMap<>();
        for(ImageResponse image : req)
        {
            map.put(image.getId(), image.getUrl());
        }
        outboxEventPublisher.publish(
                BandRoomImageUpdateEvent.builder()
                        .referenceId(req.getFirst().getReferenceId())
                        .images(map)
                        .build()
        );
    }
    private void StudioImageUpdate(List<ImageResponse> req)
    {
        HashMap<Long,String> map = new HashMap<>();
        for(ImageResponse image : req)
        {
            map.put(image.getId(), image.getUrl());
        }
        outboxEventPublisher.publish(
                StudioImageUpdateEvent.builder()
                        .referenceId(req.getFirst().getReferenceId())
                        .images(map)
                        .build()
        );

    }
}
