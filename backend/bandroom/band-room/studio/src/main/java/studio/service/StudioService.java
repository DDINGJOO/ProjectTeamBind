package studio.service;


import dto.image.response.SimpleImageResponse;
import dto.studio.request.StudioCreateRequest;
import dto.studio.request.StudioUpdateRequest;
import dto.studio.response.ProductResponse;
import eurm.Status;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import primaryIdProvider.Snowflake;
import studio.entity.Image;
import studio.entity.Studio;
import studio.repository.ImageRepository;
import studio.repository.StudioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudioService {

    private final StudioRepository studioRepository;
    private final ImageRepository imageRepository;
    private final Snowflake snowflake;

    @Transactional
    public void createStudio(StudioCreateRequest req)
    {
        Long pk =  snowflake.nextId();
        while(studioRepository.findById(pk).isEmpty())
        {
            pk = snowflake.nextId();
        }
        studioRepository.save(
                Studio.builder()
                        .id(snowflake.nextId())
                        .createdAt(LocalDateTime.now())
                        .description(req.description())
                        .name(req.name())
                        .bandRoomId(req.bandRoomId())
                        .status(Status.close)
                        .build()
        );
    }

    @Transactional
    public void changeStatus(Status newStatus, Long studioId)
    {
        //TODO
        Studio studio = studioRepository.findById(studioId).orElseThrow(EntityNotFoundException::new);
        studio.setStatus(newStatus);
        studioRepository.save(studio);

    }

    @Transactional
    public void updateStudio(StudioUpdateRequest req)
    {
        //TODO
        Studio studio =  studioRepository.findById(req.studioId()).orElseThrow(EntityNotFoundException::new);

        studio.setName(req.name());
        studio.setDescription(req.description());
        studio.setStatus(req.status());
        studioRepository.save(studio);

    }

    public List<ProductResponse> getStudios(Long bandRoomId)
    {
        List<Studio> studios = studioRepository.findAllByBandRoomId(bandRoomId);
        List<ProductResponse> responses = new ArrayList<>();
        studios.forEach(studio->{
            responses.add(
                    toResponse(studio)
            );

        });
        return responses;
    }

    public ProductResponse getStudio(Long studioId)
    {
        Studio studio = studioRepository.findById(studioId).orElseThrow(
                () -> new EntityNotFoundException("Studio with id " + studioId + " not found")
        );
        return toResponse(studio);
    }


    @Transactional
    public void deleteStudio(Long id)
    {
        studioRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(Long bandRoomId)
    {
        studioRepository.deleteAllByBandRoomId(bandRoomId);
    }


    private ProductResponse toResponse(Studio studio) {
        // studio.getImages() 를 ImageResponse 로 변환해 리스트로 수집
        var imageResponses = studio.getImages().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .studioId(studio.getId())
                .description(studio.getDescription())
                .bandRoomId(studio.getBandRoomId())
                .status(studio.getStatus())
                .name(studio.getName())
                .images(imageResponses)      // 여기에 매핑된 List<ImageResponse> 전달
                .build();
    }


    private SimpleImageResponse toResponse(Image image)
    {
        return SimpleImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }
}
