package userProfile.service;

import eurm.City;
import eurm.Genre;
import eurm.Instrument;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import primaryIdProvider.Snowflake;
import userProfile.entity.UserGerne;
import userProfile.entity.UserInstrument;
import userProfile.entity.UserProfile;
import userProfile.repository.UserProfileRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;



@Service
@RequiredArgsConstructor
public class ConcurrentDummyUserProfile {


    private final UserProfileRepository userProfileRepository;
    private final Snowflake snowflake;


    @Transactional
    public void createThousandUsersConcurrently() throws InterruptedException {
            int totalUsers = 1000;
            int poolSize    = 50;

            ExecutorService executor = Executors.newFixedThreadPool(poolSize);
            CountDownLatch latch     = new CountDownLatch(totalUsers);

            IntStream.range(0, totalUsers).forEach(i -> {
                executor.submit(() -> {
                    try {
                        UserProfile user = createUserProfile(i);
                        userProfileRepository.save(user);
                    } finally {
                        latch.countDown();
                    }
                });
            });

            latch.await(1, TimeUnit.MINUTES);
            executor.shutdown();

            long savedCount = userProfileRepository.count();
            System.out.println(">>> 총 생성된 유저 수: " + savedCount);
        }

        private UserProfile createUserProfile(int i) {
            // 1) 기본 필드
            UserProfile user = UserProfile.builder()
                    .userId(snowflake.nextId())
                    .nickname("user" + i)
                    .email("user" + i + "@example.com")
                    .profileImageUrl("http://example.com/avatars/" + i + ".png")
                    .phoneNumber("010" + i + "678")
                    .gender(i % 2 == 0 ? "M" : "F")
                    .introduction("Hello, I'm user" + i)
                    .city(City.values()[i % City.values().length])
                    .deletedAt(null)
                    .build();

            // 2) 연관된 UserInstrument 한 개
            Instrument inst = Instrument.values()[i % Instrument.values().length];
            UserInstrument ui = UserInstrument.builder()
                    .userProfile(user)
                    .instrument(inst)
                    .build();
            user.getUserInstruments().add(ui);

            // 3) 연관된 UserGerne 한 개
            Genre gr = Genre.values()[i % Genre.values().length];
            UserGerne ug = UserGerne.builder()
                    .userProfile(user)
                    .genre(gr)
                    .build();
            user.getUserGerne().add(ug);

            // createdAt/updatedAt 는 @PrePersist/@PreUpdate 로 자동 설정됩니다
            return user;
        }
    }

