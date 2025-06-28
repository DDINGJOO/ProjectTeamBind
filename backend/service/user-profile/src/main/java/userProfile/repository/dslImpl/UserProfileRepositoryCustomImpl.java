package userProfile.repository.dslImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.SubQueryExpression;
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
import userProfile.entity.QUserGerne;
import userProfile.entity.QUserInstrument;
import userProfile.entity.QUserProfile;
import userProfile.entity.UserProfile;
import userProfile.repository.UserProfileRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;


public class UserProfileRepositoryCustomImpl implements UserProfileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserProfileRepositoryCustomImpl(EntityManager em) {
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
        QUserInstrument userInterest = QUserInstrument.userInstrument;
        QUserGerne userGenre = QUserGerne.userGerne;

        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (nickname != null && !nickname.isBlank()) {
            whereBuilder.and(profile.nickname.containsIgnoreCase(nickname));
        }
        if (location != null) {
            whereBuilder.and(profile.location.eq(location));
        }
        if (interests != null && !interests.isEmpty()) {
            // 관심사에 해당하는 userId만
            SubQueryExpression<Long> interestSub = JPAExpressions
                    .<Long>select(userInterest.userProfile.userId)
                    .from(userInterest)
                    .where(userInterest.instrument.in(interests));
            whereBuilder.and(profile.userId.in(interestSub));
        }
        if (genres != null && !genres.isEmpty()) {
            // 장르에 해당하는 userId만
            SubQueryExpression<Long> genreSub = JPAExpressions
                    .<Long>select(userGenre.userProfile.userId)
                    .from(userGenre)
                    .where(userGenre.genre.in(genres));
            whereBuilder.and(profile.userId.in(genreSub));
        }

        // 페이징된 프로필 조회 (fetch join 없이)
        List<UserProfile> results = queryFactory
                .selectFrom(profile)
                .where(whereBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트
        Long total = queryFactory
                .select(profile.count())
                .from(profile)
                .where(whereBuilder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<UserProfile> search(
            ProfileSearchCondition searchCondition,
            Pageable pageable
    ) {
        String nickname = searchCondition.getNickname();
        Location location = searchCondition.getLocation();
        List<Instrument> interests = searchCondition.getInterest();
        List<Genre> genres = searchCondition.getGenre();

        QUserProfile profile = QUserProfile.userProfile;
        QUserInstrument userInterest = QUserInstrument.userInstrument;
        QUserGerne userGenre = QUserGerne.userGerne;

        BooleanBuilder whereBuilder = new BooleanBuilder();

        if (nickname != null && !nickname.isBlank()) {
            whereBuilder.and(profile.nickname.containsIgnoreCase(nickname));
        }
        if (location != null) {
            whereBuilder.and(profile.location.eq(location));
        }
        if (interests != null && !interests.isEmpty()) {
            SubQueryExpression<Long> interestSub = JPAExpressions
                    .<Long>select(userInterest.userProfile.userId)
                    .from(userInterest)
                    .where(userInterest.instrument.in(interests));
            whereBuilder.and(profile.userId.in(interestSub));
        }
        if (genres != null && !genres.isEmpty()) {
            SubQueryExpression<Long> genreSub = JPAExpressions
                    .<Long>select(userGenre.userProfile.userId)
                    .from(userGenre)
                    .where(userGenre.genre.in(genres));
            whereBuilder.and(profile.userId.in(genreSub));
        }

        // 1) id 목록 페이징
        List<Long> userIds = queryFactory
                .select(profile.userId)
                .from(profile)
                .where(whereBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (userIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2) fetch join 으로 연관 엔티티 함께 조회
        List<UserProfile> results = queryFactory
                .selectFrom(profile)
                .distinct()
                .leftJoin(profile.userInstruments, userInterest).fetchJoin()
                .leftJoin(profile.userGerne, userGenre).fetchJoin()
                .where(profile.userId.in(userIds))
                .fetch();

        // 3) count 쿼리
        Long total = queryFactory
                .select(profile.count())
                .from(profile)
                .where(whereBuilder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total == null ? 0 : total);
    }

    @Override
    public List<UserProfileResponse> findBatchProfiles(LocalDateTime updatedAfter) {
        // 배치 조회 구현 필요 시 여기에 작성
        return List.of();
    }
}
