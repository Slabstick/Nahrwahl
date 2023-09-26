package com.quinscape.Nahrwahl.service;

import com.quinscape.Nahrwahl.repository.NutritionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutritionLogService {

  private final NutritionLogRepository nutritionLogRepository;


}
