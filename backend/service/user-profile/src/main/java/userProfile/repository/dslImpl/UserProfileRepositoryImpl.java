package userProfile.repository.dslImpl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dto.userprofile.condition.ProfileSearchCondition;
import dto.userprofile.response.UserProfileResponse;
import eurm.Genre;
import eurm.Instrument;
import eurm.Location;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import userProfile.entity.QUserGenre;
import userProfile.entity.QUserInterest;
import userProfile.entity.QUserProfile;
import userProfile.entity.UserProfile;
import userProfile.repository.UserProfileQueryRepository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public class UserProfileRepositoryImpl implements UserProfileQueryRepository {

    private final JPAQueryFactory queryFactory;

    public UserProfileRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public Page<UserProfile> searchProfilesDsl(
            String nickname,
            Location location,
            List<Instrument> interests,
            List<Genre> genres,
            Pageable pageable
    ) {
        QUserProfile profile = QUserProfile.userProfile;
        QUserInterest userInterest = QUserInterest.userInterest;
        QUserGenre userGenre = QUserGenre.userGenre;

        // where절 빌드
        var whereBuilder = new BooleanBuilder();

        if (nickname != null && !nickname.isBlank()) {
            whereBuilder.and(profile.nickname.containsIgnoreCase(nickname));
        }
        if (location != null) {
            whereBuilder.and(profile.location.eq(location));
        }
        if (interests != null && !interests.isEmpty()) {
            // 관심사에 해당하는 userId만
            whereBuilder.and(profile.userId.in(
                    JPAExpressions
                            .select(userInterest.userProfile.userId)
                            .from(userInterest)
                            .where(userInterest.interest.in(interests))
            ));
        }
        if (genres != null && !genres.isEmpty()) {
            // 장르에 해당하는 userId만
            whereBuilder.and(profile.userId.in(
                    JPAExpressions
                            .select(userGenre.userProfile.userId)
                            .from(userGenre)
                            .where(userGenre.genre.in(genres))
            ));
        }

        // 1차 쿼리: UserProfile 페이징 (관계 Fetch X)
        List<UserProfile> results = queryFactory
                .selectFrom(profile)
                .where(whereBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        Long total = queryFactory
                .select(profile.count())
                .from(profile)
                .where(whereBuilder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }



    public Page<UserProfile> search(
            ProfileSearchCondition searchCondition,
            Pageable pageable
    ) {
        String nickname = searchCondition.getNickname();
        Location location = searchCondition.getLocation();
        List<Instrument> interests = searchCondition.getInterest();
        List<Genre> genres = searchCondition.getGenre();

        QUserProfile profile = QUserProfile.userProfile;
        QUserInterest userInterest = QUserInterest.userInterest;
        QUserGenre userGenre = QUserGenre.userGenre;

        var whereBuilder = new BooleanBuilder();

        if (nickname != null && !nickname.isBlank()) {
            whereBuilder.and(profile.nickname.containsIgnoreCase(nickname));
        }
        if (location != null) {
            whereBuilder.and(profile.location.eq(location));
        }
        if (interests != null && !interests.isEmpty()) {
            whereBuilder.and(profile.userId.in(
                    JPAExpressions.select(userInterest.userProfile.userId)
                            .from(userInterest)
                            .where(userInterest.interest.in(interests))
            ));
        }
        if (genres != null && !genres.isEmpty()) {
            whereBuilder.and(profile.userId.in(
                    JPAExpressions.select(userGenre.userProfile.userId)
                            .from(userGenre)
                            .where(userGenre.genre.in(genres))
            ));
        }

        // Step 1: id만 페이징 추출
        List<Long> userIds = queryFactory
                .select(profile.userId)
                .from(profile)
                .where(whereBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (userIds.isEmpty()) return Page.empty(pageable);

        // Step 2: id 기준으로 fetch join 조회
        List<UserProfile> results = queryFactory
                .selectFrom(profile)
                .distinct()
                .leftJoin(profile.userInterests, userInterest).fetchJoin()
                .leftJoin(profile.userGenres, userGenre).fetchJoin()
                .where(profile.userId.in(userIds))
                .fetch();

        // Step 3: count query
        Long total = queryFactory
                .select(profile.count())
                .from(profile)
                .where(whereBuilder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }



    @Override
    public List<UserProfileResponse> findBatchProfiles(LocalDateTime updatedAfter) {
        return List.of();
    }


}
