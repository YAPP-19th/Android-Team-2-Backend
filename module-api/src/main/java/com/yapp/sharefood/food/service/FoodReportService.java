package com.yapp.sharefood.food.service;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.domain.FoodReportStatus;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.food.repository.FoodRepository;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.domain.UserReportType;
import com.yapp.sharefood.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class FoodReportService {
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public void createReport(Long foodId, String reportMessage) {
        Food findFood = foodRepository.findById(foodId).orElseThrow(FoodNotFoundException::new);

        findFood.addReport(reportMessage);

        User writer = userRepository.findById(findFood.getWriter().getId()).orElseThrow(UserNotFoundException::new);

        FoodReportStatus foodReportStatus = findFood.getReportStatus();
        if (foodReportStatus == FoodReportStatus.JUDGED) {
            writer.addReport(UserReportType.POSTING_FOOD_JUDGED.getMessage());
        }

        if (foodReportStatus == FoodReportStatus.BANNDED) {
            writer.addReport(UserReportType.POSTING_FOOD_BANNDED.getMessage());
        }
    }
}
